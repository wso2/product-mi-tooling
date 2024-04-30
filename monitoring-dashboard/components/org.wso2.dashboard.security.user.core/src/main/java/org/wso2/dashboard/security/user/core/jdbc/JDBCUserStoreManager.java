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
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
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
                                 Integer tenantId, boolean skipInitData) throws UserStoreException {
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
            throw new UserStoreException("User Management Data Source is null");
        }

        realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig
                .getUserStoreProperties()));

        if (log.isDebugEnabled()) {
            log.debug("Ended " + System.currentTimeMillis());
        }
    }

    public JDBCUserStoreManager(RealmConfiguration realmConfig, int tenantId) throws UserStoreException {
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

    public boolean doConnectAndAuthenticate(String userName, Object credential) throws UserStoreException {
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
            throw new UserStoreException("Authentication Failure", e);
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
            throws UserStoreException {

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
            throw new UserStoreException(msg, e);
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
                                                   String nonCaseSensitiveUsernameQuery) throws UserStoreException {
        String sqlStmt;
        if (isCaseSensitiveUsername()) {
            sqlStmt = caseSensitiveUsernameQuery;
        } else {
            sqlStmt = nonCaseSensitiveUsernameQuery;
        }
        if (sqlStmt == null) {
            throw new UserStoreException("The sql statement for retrieving user roles is null");
        }
        return sqlStmt;
    }

    private String preparePassword(String password, String saltValue) throws UserStoreException {
        try {
            String digestInput = password;
            if (saltValue != null) {
                digestInput = password + saltValue;
            }
            String digsestFunction = realmConfig.getUserStoreProperties().get(
                    JDBCRealmConstants.DIGEST_FUNCTION);
            if (digsestFunction != null) {

                if (digsestFunction
                        .equals(UserStoreConstants.RealmConfig.PASSWORD_HASH_METHOD_PLAIN_TEXT)) {
                    return password;
                }

                MessageDigest dgst = MessageDigest.getInstance(digsestFunction);
                byte[] byteValue = dgst.digest(digestInput.getBytes());
                password = Base64.encode(byteValue);
            }
            return password;
        } catch (NoSuchAlgorithmException e) {
            String msg = "Error occurred while preparing password.";
            if (log.isDebugEnabled()) {
                log.debug(msg, e);
            }
            throw new UserStoreException(msg, e);
        }
    }

    private boolean isCaseSensitiveUsername() {
        String isUsernameCaseInsensitiveString = realmConfig.getUserStoreProperty(CASE_INSENSITIVE_USERNAME);
        return !Boolean.parseBoolean(isUsernameCaseInsensitiveString);
    }

    protected Connection getDBConnection() throws SQLException, UserStoreException {
        Connection dbConnection = getJDBCDataSource().getConnection();
        dbConnection.setAutoCommit(false);
        if (dbConnection.getTransactionIsolation() != Connection.TRANSACTION_READ_COMMITTED) {
            dbConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }
        return dbConnection;
    }

    private DataSource getJDBCDataSource() throws UserStoreException {
        if (jdbcds == null) {
            jdbcds = loadUserStoreSpacificDataSoruce();
        }
        return jdbcds;
    }

    @Override
    public boolean isReadOnly() throws UserStoreException {
        return false;
    }

    /**
     *
     */
    public int getTenantId() throws UserStoreException {
        return this.tenantId;
    }

    @Override
    public RealmConfiguration getRealmConfiguration() {
        return null;
    }

    @Override
    protected String[] doGetExternalRoleListOfUser(String userName, String filter) throws UserStoreException {
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
    protected String[] doGetSharedRoleListOfUser(String s, String s1, String s2) throws UserStoreException {
        return new String[0];
    }
}

