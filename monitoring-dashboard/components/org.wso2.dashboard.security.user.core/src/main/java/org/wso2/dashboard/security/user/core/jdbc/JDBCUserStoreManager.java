/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.dashboard.security.user.core.jdbc;

import org.apache.axiom.om.util.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.security.user.core.*;
import org.wso2.dashboard.security.user.core.common.AbstractUserStoreManager;
import org.wso2.dashboard.security.user.core.common.DashboardUserStoreException;
import org.wso2.dashboard.security.user.core.common.Secret;
import org.wso2.dashboard.security.user.core.common.UnsupportedSecretTypeException;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.core.UserCoreConstants;
import org.wso2.micro.integrator.security.user.core.UserRealm;
import org.wso2.micro.integrator.security.user.core.UserStoreException;
import org.wso2.micro.integrator.security.user.core.claim.ClaimManager;
import org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants;
import org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants;
import org.wso2.micro.integrator.security.user.core.profile.ProfileConfigurationManager;
import org.wso2.micro.integrator.security.user.core.util.JDBCRealmUtil;

import javax.sql.DataSource;
import java.security.*;
import java.sql.Timestamp;
import java.sql.*;
import java.util.Date;
import java.util.*;

public class JDBCUserStoreManager extends AbstractUserStoreManager implements UserStoreManager {
    private static Log log = LogFactory.getLog(JDBCUserStoreManager.class);
    private static final String CASE_INSENSITIVE_USERNAME = "CaseInsensitiveUsername";
    protected DataSource jdbcds = null;

    public JDBCUserStoreManager() {

    }

    public JDBCUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties,
                                 ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm,
                                 Integer tenantId, boolean skipInitData) throws DashboardUserStoreException {
        this(realmConfig, tenantId);
        if (log.isDebugEnabled()) {
            log.debug("Started " + System.currentTimeMillis());
        }
        this.claimManager = claimManager;
        this.userRealm = realm;

        try {
            jdbcds = loadUserStoreSpacificDataSoruce();
            properties.put(UserStoreConstants.DATA_SOURCE, jdbcds);

            if (log.isDebugEnabled()) {
                log.debug("The jdbcDataSource being used by JDBCUserStoreManager :: "
                        + jdbcds.hashCode());
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Loading JDBC datasource failed", e);
            }
        }

        dataSource = (DataSource) properties.get(UserStoreConstants.DATA_SOURCE);
        if (dataSource == null) {
            dataSource = DatabaseUtil.getRealmDataSource(realmConfig);
        }
        if (dataSource == null) {
            throw new DashboardUserStoreException("User Management Data Source is null");
        }

        realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig
                .getUserStoreProperties()));

        if (log.isDebugEnabled()) {
            log.debug("Ended " + System.currentTimeMillis());
        }
    }

    public JDBCUserStoreManager(RealmConfiguration realmConfig, int tenantId) throws DashboardUserStoreException {
        this.realmConfig = realmConfig;
        this.tenantId = tenantId;
        realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig
                .getUserStoreProperties()));

        if (realmConfig.getUserStoreProperty(UserStoreConstants.RealmConfig.READ_GROUPS_ENABLED) != null) {
            readGroupsEnabled = Boolean.parseBoolean(realmConfig
                    .getUserStoreProperty(UserStoreConstants.RealmConfig.READ_GROUPS_ENABLED));
        }

        if (log.isDebugEnabled()) {
            if (readGroupsEnabled) {
                log.debug("ReadGroups is enabled for " + getMyDomainName());
            } else {
                log.debug("ReadGroups is disabled for " + getMyDomainName());
            }
        }

        if (realmConfig.getUserStoreProperty(UserStoreConstants.RealmConfig.WRITE_GROUPS_ENABLED) != null) {
            writeGroupsEnabled = Boolean.parseBoolean(realmConfig
                    .getUserStoreProperty(UserStoreConstants.RealmConfig.WRITE_GROUPS_ENABLED));
        } else {
            if (!isReadOnly()) {
                writeGroupsEnabled = true;
            }
        }

        if (log.isDebugEnabled()) {
            if (writeGroupsEnabled) {
                log.debug("WriteGroups is enabled for " + getMyDomainName());
            } else {
                log.debug("WriteGroups is disabled for " + getMyDomainName());
            }
        }
        if (writeGroupsEnabled) {
            readGroupsEnabled = true;
        }
    }

    /**
     * @return
     * @throws UserStoreException
     */
    private DataSource loadUserStoreSpacificDataSoruce() {
        return DatabaseUtil.createUserStoreDataSource(realmConfig);
    }

    public boolean doAuthenticate(String userName, String credential) throws UserStoreException {
        try {

            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws Exception {
                    if (!UserStoreManagerUtils.validateUserNameAndCredential(userName, credential)) {
                        return  false;
                    }
                    int index = userName.indexOf("/");
                    boolean domainProvided = index > 0;
                    return doConnectAndAuthenticate(userName, credential);
                }
            });
        } catch (PrivilegedActionException e) {
            throw new UserStoreException("Error occurred while authenticating user", e);
        }
    }

    @Override
    protected boolean doAuthenticate(String userName, Object credential) throws DashboardUserStoreException {
        if (!checkUserNameValid(userName)) {
            if (log.isDebugEnabled()) {
                log.debug("Username validation failed");
            }
            return false;
        }

        if (!checkUserPasswordValid(credential)) {
            if (log.isDebugEnabled()) {
                log.debug("Password validation failed");
            }
            return false;
        }

        if (UserStoreConstants.REGISTRY_SYSTEM_USERNAME.equals(userName)) {
            log.error("Anonymous user trying to login");
            return false;
        }

        Connection dbConnection = null;
        ResultSet rs = null;
        PreparedStatement prepStmt = null;
        String sqlstmt = null;
        String password = null;
        boolean isAuthed = false;

        try {
            dbConnection = getDBConnection();
            dbConnection.setAutoCommit(false);

            if (isCaseSensitiveUsername()) {
                sqlstmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.SELECT_USER);
            } else {
                sqlstmt = realmConfig.getUserStoreProperty(JDBCCaseInsensitiveConstants.SELECT_USER_CASE_INSENSITIVE);
            }

            if (log.isDebugEnabled()) {
                log.debug(sqlstmt);
            }

            prepStmt = dbConnection.prepareStatement(sqlstmt);
            prepStmt.setString(1, userName);
            if (sqlstmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
                prepStmt.setInt(2, tenantId);
            }

            rs = prepStmt.executeQuery();

            if (rs.next() == true) {
                String storedPassword = rs.getString(3);
                String saltValue = null;
                if ("true".equalsIgnoreCase(realmConfig
                        .getUserStoreProperty(JDBCRealmConstants.STORE_SALTED_PASSWORDS))) {
                    saltValue = rs.getString(4);
                }

                boolean requireChange = rs.getBoolean(5);
                Timestamp changedTime = rs.getTimestamp(6);

                GregorianCalendar gc = new GregorianCalendar();
                gc.add(GregorianCalendar.HOUR, -24);
                Date date = gc.getTime();

                if (requireChange == true && changedTime.before(date)) {
                    isAuthed = false;
                } else {
                    password = this.preparePassword(credential, saltValue);
                    if ((storedPassword != null) && (storedPassword.equals(password))) {
                        isAuthed = true;
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while retrieving user authentication info for user : " + userName;
            if (log.isDebugEnabled()) {
                log.debug(msg, e);
            }
            throw new DashboardUserStoreException("Authentication Failure", e);
        } finally {
            DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
        }

        if (log.isDebugEnabled()) {
            log.debug("User " + userName + " login attempt. Login success :: " + isAuthed);
        }

        return isAuthed;
    }

    private void checkMandatoryParams(String userName, Object credential) {
    }

    public boolean doConnectAndAuthenticate(String userName, Object credential) throws DashboardUserStoreException {
        Connection dbConnection = null;
        ResultSet rs = null;
        PreparedStatement prepStmt = null;
        String sqlstmt = null;
        String password = null;
        boolean isAuthed = false;

        try {
            dbConnection = getDBConnection();
            dbConnection.setAutoCommit(false);

            if (isCaseSensitiveUsername()) {
                sqlstmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.SELECT_USER);
            } else {
                sqlstmt = realmConfig.getUserStoreProperty(JDBCCaseInsensitiveConstants.SELECT_USER_CASE_INSENSITIVE);
            }

            if (log.isDebugEnabled()) {
                log.debug(sqlstmt);
            }

            prepStmt = dbConnection.prepareStatement(sqlstmt);
            prepStmt.setString(1, userName);
            if (sqlstmt.contains(UserStoreConstants.UM_TENANT_COLUMN)) {
                prepStmt.setInt(2, tenantId);
            }

            rs = prepStmt.executeQuery();

            if (rs.next() == true) {
                String storedPassword = rs.getString(3);
                String saltValue = null;
                if ("true".equalsIgnoreCase(realmConfig
                        .getUserStoreProperty(JDBCRealmConstants.STORE_SALTED_PASSWORDS))) {
                    saltValue = rs.getString(4);
                }

                boolean requireChange = rs.getBoolean(5);
                Timestamp changedTime = rs.getTimestamp(6);

                GregorianCalendar gc = new GregorianCalendar();
                gc.add(GregorianCalendar.HOUR, -24);
                Date date = gc.getTime();

                if (requireChange == true && changedTime.before(date)) {
                    isAuthed = false;
                } else {
                    password = preparePassword(credential.toString(), saltValue);
                    if ((storedPassword != null) && (storedPassword.equals(password))) {
                        isAuthed = true;
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while retrieving user authentication info for user : " + userName;
            if (log.isDebugEnabled()) {
                log.debug(msg, e);
            }
            throw new DashboardUserStoreException("Authentication Failure", e);
        } finally {
            DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
        }

        if (log.isDebugEnabled()) {
            log.debug("User " + userName + " login attempt. Login success :: " + isAuthed);
        }

        return isAuthed;
    }

    /**
     * @param sqlStmt
     * @param params
     * @return
     * @throws UserStoreException
     */
    private String[] getStringValuesFromDatabase(String sqlStmt, Object... params)
            throws DashboardUserStoreException {

        if (log.isDebugEnabled()) {
            log.debug("Executing Query: " + sqlStmt);
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                log.debug("Input value: " + param);
            }
        }

        String[] values = new String[0];
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        try {
            dbConnection = getDBConnection();
            values = DatabaseUtil.getStringValuesFromDatabase(dbConnection, sqlStmt, params);
        } catch (SQLException e) {
            String msg = "Error occurred while retrieving string values.";
            if (log.isDebugEnabled()) {
                log.debug(msg, e);
            }
            throw new DashboardUserStoreException(msg, e);
        } finally {
            DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
        }
        return values;
    }

    /**
     * Get the SQL statement for ExternalRoles.
     *
     * @param caseSensitiveUsernameQuery    query for getting role with case sensitive username.
     * @param nonCaseSensitiveUsernameQuery query for getting role with non-case sensitive username.
     * @return sql statement.
     * @throws UserStoreException
     */
    private String getExternalRoleListSqlStatement(String caseSensitiveUsernameQuery,
                                                   String nonCaseSensitiveUsernameQuery)
            throws DashboardUserStoreException {
        String sqlStmt;
        if (isCaseSensitiveUsername()) {
            sqlStmt = caseSensitiveUsernameQuery;
        } else {
            sqlStmt = nonCaseSensitiveUsernameQuery;
        }
        if (sqlStmt == null) {
            throw new DashboardUserStoreException("The sql statement for retrieving user roles is null");
        }
        return sqlStmt;
    }

    private String preparePassword(Object password, String saltValue) throws DashboardUserStoreException {
        Secret credentialObj;
        try {
            credentialObj = Secret.getSecret(password);
        } catch (UnsupportedSecretTypeException e) {
            throw new DashboardUserStoreException("Unsupported credential type", e);
        }
        try {
            String passwordString;
            if (saltValue != null) {
                credentialObj.addChars(saltValue.toCharArray());
            }

            String digestFunction = realmConfig.getUserStoreProperties().get(JDBCRealmConstants.DIGEST_FUNCTION);
            if (digestFunction != null) {
                if (digestFunction.equals(UserCoreConstants.RealmConfig.PASSWORD_HASH_METHOD_PLAIN_TEXT)) {
                    passwordString = new String(credentialObj.getChars());
                    return passwordString;
                }

                MessageDigest digest = MessageDigest.getInstance(digestFunction);
                byte[] byteValue = digest.digest(credentialObj.getBytes());
                passwordString = Base64.encode(byteValue);
            } else {
                passwordString = new String(credentialObj.getChars());
            }

            return passwordString;
        } catch (NoSuchAlgorithmException e) {
            String msg = "Error occurred while preparing password.";
            if (log.isDebugEnabled()) {
                log.debug(msg, e);
            }
            throw new DashboardUserStoreException(msg, e);
        } finally {
            credentialObj.clear();
        }
    }

    private boolean isCaseSensitiveUsername() {
        String isUsernameCaseInsensitiveString = realmConfig.getUserStoreProperty(CASE_INSENSITIVE_USERNAME);
        return !Boolean.parseBoolean(isUsernameCaseInsensitiveString);
    }

    protected Connection getDBConnection() throws SQLException {
        Connection dbConnection = getJDBCDataSource().getConnection();
        dbConnection.setAutoCommit(false);
        if (dbConnection.getTransactionIsolation() != Connection.TRANSACTION_READ_COMMITTED) {
            dbConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }
        return dbConnection;
    }

    private DataSource getJDBCDataSource() {
        if (jdbcds == null) {
            jdbcds = loadUserStoreSpacificDataSoruce();
        }
        return jdbcds;
    }

    @Override
    public boolean isReadOnly() throws DashboardUserStoreException {
        return false;
    }

    /**
     *
     */
    public int getTenantId() throws DashboardUserStoreException {
        return this.tenantId;
    }

    @Override
    public RealmConfiguration getRealmConfiguration() {
        return null;
    }

    @Override
    protected String[] doGetExternalRoleListOfUser(String userName, String filter) throws DashboardUserStoreException {
        if (log.isDebugEnabled()) {
            log.debug("Getting roles of user: " + userName + " with filter: " + filter);
        }
        String sqlStmt;
        String[] names;
        if (filter.equals("*") || StringUtils.isEmpty(filter)) {
            sqlStmt = getExternalRoleListSqlStatement(
                    realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USER_ROLE),
                    realmConfig.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_USER_ROLE_CASE_INSENSITIVE));
            if (sqlStmt.contains(UserStoreConstants.UM_TENANT_COLUMN)) {
                names = getStringValuesFromDatabase(sqlStmt, userName, tenantId, tenantId, tenantId);
            } else {
                names = getStringValuesFromDatabase(sqlStmt, userName);
            }
        } else {
            filter = filter.trim();
            filter = filter.replace("*", "%");
            filter = filter.replace("?", "_");
            sqlStmt = getExternalRoleListSqlStatement(
                    realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_IS_USER_ROLE_EXIST), realmConfig
                            .getUserStoreProperty(
                                    JDBCCaseInsensitiveConstants.GET_IS_USER_ROLE_EXIST_CASE_INSENSITIVE));

            if (sqlStmt.contains(UserStoreConstants.UM_TENANT_COLUMN)) {
                names = getStringValuesFromDatabase(sqlStmt, userName, tenantId, tenantId, tenantId, filter);
            } else {
                names = getStringValuesFromDatabase(sqlStmt, userName, filter);
            }
        }
        List<String> roles = new ArrayList<String>();
        if (log.isDebugEnabled()) {
            if (names != null) {
                for (String name : names) {
                    log.debug("Found role: " + name);
                }
            } else {
                log.debug("No external role found for the user: " + userName);
            }
        }

        Collections.addAll(roles, names);
        return roles.toArray(new String[roles.size()]);
    }

    @Override
    protected String[] doGetSharedRoleListOfUser(String s, String s1, String s2) {
        return new String[0];
    }
}

