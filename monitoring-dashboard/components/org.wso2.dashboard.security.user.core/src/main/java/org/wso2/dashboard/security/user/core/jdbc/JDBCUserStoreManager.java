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
import org.wso2.dashboard.security.user.core.DatabaseUtil;
import org.wso2.dashboard.security.user.core.UserStoreConstants;
import org.wso2.dashboard.security.user.core.common.AbstractUserStoreManager;
import org.wso2.dashboard.security.user.core.common.DashboardUserStoreException;
import org.wso2.dashboard.security.user.core.common.Secret;
import org.wso2.dashboard.security.user.core.common.UnsupportedSecretTypeException;
import org.wso2.micro.core.util.DatabaseCreator;
import org.wso2.micro.integrator.security.user.api.ClaimManager;
import org.wso2.micro.integrator.security.user.api.Permission;
import org.wso2.micro.integrator.security.user.api.Properties;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.core.UserCoreConstants;
import org.wso2.micro.integrator.security.user.core.UserStoreException;
import org.wso2.micro.integrator.security.user.core.UserStoreManager;
import org.wso2.micro.integrator.security.user.core.claim.Claim;
import org.wso2.micro.integrator.security.user.core.common.RoleContext;
import org.wso2.micro.integrator.security.user.core.constants.UserCoreErrorConstants;
import org.wso2.micro.integrator.security.user.core.jdbc.JDBCRoleContext;
import org.wso2.micro.integrator.security.user.core.ldap.LDAPConstants;
import org.wso2.micro.integrator.security.user.core.tenant.Tenant;
import org.wso2.micro.integrator.security.user.core.util.JDBCRealmUtil;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.wso2.dashboard.security.user.core.UserStoreConstants.RealmConfig.READ_GROUPS_ENABLED;
import static org.wso2.dashboard.security.user.core.UserStoreConstants.RealmConfig.WRITE_GROUPS_ENABLED;
import static org.wso2.micro.integrator.security.user.core.constants.UserCoreErrorConstants.ErrorMessages.ERROR_CODE_DUPLICATE_WHILE_ADDING_A_USER;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.ADD_ROLE;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.ADD_ROLE_TO_USER;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.ADD_USER_PROPERTY;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.ADD_USER_TO_ROLE;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.ADD_USER_WITH_ID;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.DELETE_ROLE;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.DELETE_USER;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.DIGEST_FUNCTION;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.GET_IS_ROLE_EXISTING;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.GET_IS_USER_EXISTING;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.GET_IS_USER_ROLE_EXIST;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.GET_ROLE_LIST;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.GET_USERS_IN_ROLE;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.GET_USER_FILTER;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.GET_USER_ROLE;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.ON_DELETE_ROLE_REMOVE_USER_ROLE;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.ON_DELETE_USER_REMOVE_ATTRIBUTE;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.ON_DELETE_USER_REMOVE_USER_ROLE;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.REMOVE_ROLE_FROM_USER;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.SELECT_USER;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.STORE_SALTED_PASSWORDS;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.UPDATE_USER_PASSWORD;
import static org.wso2.micro.integrator.security.user.core.jdbc.JDBCRealmConstants.USER_NAME_UNIQUE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.ADD_ROLE_TO_USER_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.ADD_USER_TO_ROLE_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.DELETE_USER_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.GET_IS_USER_EXISTING_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.GET_IS_USER_ROLE_EXIST_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.GET_USER_FILTER_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.GET_USER_ROLE_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.ON_DELETE_USER_REMOVE_ATTRIBUTE_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.ON_DELETE_USER_REMOVE_USER_ROLE_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.REMOVE_ROLE_FROM_USER_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.SELECT_USER_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.UPDATE_USER_PASSWORD_CASE_INSENSITIVE;
import static org.wso2.micro.integrator.security.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants.USER_NAME_UNIQUE_CASE_INSENSITIVE;

public class JDBCUserStoreManager extends AbstractUserStoreManager {
    public static final String EMPTY_STRING = "";
    private static final Log log = LogFactory.getLog(JDBCUserStoreManager.class);
    private static final String CASE_INSENSITIVE_USERNAME = "CaseInsensitiveUsername";
    private static final String SHA_1_PRNG = "SHA1PRNG";
    private static final String DISPLAY_NAME_CLAIM = "http://wso2.org/claims/displayName";
    private static final String TRUE_VALUE = "true";
    private DataSource jdbcDataSource = null;

    public JDBCUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties, Integer tenantId)
            throws UserStoreException {
        this(realmConfig, tenantId);
        logDebug("JDBCUserStoreManager initialization started at: " + System.currentTimeMillis());
        initializeDataSource(realmConfig, properties);
        realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig.getUserStoreProperties()));
        if (realmConfig.isPrimary()) {
            addInitialAdminData(Boolean.parseBoolean(realmConfig.getAddAdmin()));
        }
        logDebug("JDBCUserStoreManager initialization ended at: " + System.currentTimeMillis());
    }

    private void initializeDataSource(RealmConfiguration realmConfig, Map<String, Object> properties) throws DashboardUserStoreException {
        try {
            jdbcDataSource = loadUserStoreSpecificDataSource();
            properties.put(UserStoreConstants.DATA_SOURCE, jdbcDataSource);
            logDebug("The jdbcDataSource being used by JDBCUserStoreManager :: " + jdbcDataSource.hashCode());
        } catch (Exception e) {
            log.error("Loading JDBC datasource failed", e);
        }
        dataSource = (DataSource) properties.get(UserStoreConstants.DATA_SOURCE);
        if (dataSource == null) {
            dataSource = DatabaseUtil.getRealmDataSource(realmConfig);
        }
        if (dataSource == null) {
            throw new DashboardUserStoreException("User Management Data Source is null");
        }
    }

    private DataSource loadUserStoreSpecificDataSource() {
        return DatabaseUtil.createUserStoreDataSource(realmConfig);
    }

    public JDBCUserStoreManager(RealmConfiguration realmConfig, int tenantId) {
        this.realmConfig = realmConfig;
        this.tenantId = tenantId;
        realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig.getUserStoreProperties()));
        initializeGroupSettings();
    }

    private void initializeGroupSettings() {
        writeGroupsEnabled = parseGroupSetting(WRITE_GROUPS_ENABLED, !isReadOnly());
        // If write groups are enabled, read groups auto enabled
        readGroupsEnabled = writeGroupsEnabled || parseGroupSetting(READ_GROUPS_ENABLED, false);
        logDebug("ReadGroups is " + (readGroupsEnabled ? "enabled" : "disabled"));
        logDebug("WriteGroups is " + (writeGroupsEnabled ? "enabled" : "disabled"));
    }

    private boolean parseGroupSetting(String propertyName, boolean defaultValue) {
        String propertyValue = realmConfig.getUserStoreProperty(propertyName);
        return propertyValue != null ? Boolean.parseBoolean(propertyValue) : defaultValue;
    }

    private static void logDebug(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
    }

    private static void rollbackPersistUser(@NotNull Connection connection, String username, Exception e) throws UserStoreException {
        try {
            connection.rollback();
        } catch (SQLException e1) {
            String errorMessage = "Error rollback add user operation for user : " + username;
            logDebug(errorMessage, e1);
            throw new UserStoreException(errorMessage, e1);
        }

        String errorMessage = "Error while persisting user : " + username;
        logDebug(errorMessage, e);
        throw isDuplicateEntryError(e) ? new UserStoreException(errorMessage, ERROR_CODE_DUPLICATE_WHILE_ADDING_A_USER.getCode(), e)
                : new UserStoreException(errorMessage, e);

    }

    private static boolean isDuplicateEntryError(Exception e) {
        return e instanceof UserStoreException &&
                UserCoreErrorConstants.ErrorMessages.ERROR_CODE_DUPLICATE_WHILE_WRITING_TO_DATABASE.getCode().equals(((UserStoreException) e).getErrorCode());
    }

    @Override
    protected boolean doAuthenticate(String username, Object credential) throws UserStoreException {
        if (!isValidUsername(username) || !isValidPasswordFormat(credential)) {
            logDebug("Username or password validation failed");
            return false;
        }

        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            connection = getDBConnection();
            connection.setAutoCommit(false);
            statement = createSelectUserPreparedStatement(connection, username);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return authenticateUser(resultSet, credential);
            }
            logDebug("User " + username + " login attempt. Login failed");
            return false;
        } catch (SQLException e) {
            String message = "Error occurred while retrieving user authentication info for user : " + username;
            logDebug(message, e);
            throw new DashboardUserStoreException("Authentication Failure", e);
        } finally {
            DatabaseUtil.closeAllConnections(connection, resultSet, statement);
        }
    }

    private boolean authenticateUser(ResultSet resultSet, Object credential) throws SQLException, UserStoreException {
        String storedPassword = resultSet.getString(3);
        String saltValue = getSaltValue(resultSet);
        boolean requireChange = resultSet.getBoolean(5);
        Timestamp changedTime = resultSet.getTimestamp(6);

        if (passwordChangeNotRequired(requireChange, changedTime)) {
            String preparedPassword = preparePassword(credential, saltValue);
            if (storedPassword != null && storedPassword.equals(preparedPassword)) {
                logDebug("Authentication successful");
                return true;
            }
        }
        return false;
    }

    private boolean passwordChangeNotRequired(boolean requireChange, Timestamp changedTime) {
        if (!requireChange) return true;

        GregorianCalendar gc = new GregorianCalendar();
        gc.add(GregorianCalendar.HOUR, -24);
        return !changedTime.before(gc.getTime());
    }

    private String getSaltValue(ResultSet resultSet) throws SQLException {
        return TRUE_VALUE.equalsIgnoreCase(realmConfig.getUserStoreProperty(STORE_SALTED_PASSWORDS))
                ? resultSet.getString(4) : null;
    }

    private String preparePassword(Object password, String salt) throws UserStoreException {
        try (Secret secret = Secret.getSecret(password)) {
            addSaltIfPresent(secret, salt);
            return getHashedOrPlainPassword(secret);
        } catch (UnsupportedSecretTypeException e) {
            throw new DashboardUserStoreException("Unsupported credential type", e);
        } catch (NoSuchAlgorithmException e) {
            String message = "Error occurred while preparing password.";
            logDebug(message, e);
            throw new DashboardUserStoreException(message, e);
        }
    }

    private void addSaltIfPresent(Secret secret, String saltValue) {
        if (saltValue != null) {
            secret.addChars(saltValue.toCharArray());
        }
    }

    private String getHashedOrPlainPassword(Secret secret) throws NoSuchAlgorithmException {
        String digestFunction = realmConfig.getUserStoreProperties().get(DIGEST_FUNCTION);
        if (digestFunction == null || UserCoreConstants.RealmConfig.PASSWORD_HASH_METHOD_PLAIN_TEXT.equals(digestFunction)) {
            return new String(secret.getChars());
        }

        MessageDigest digest = MessageDigest.getInstance(digestFunction);
        byte[] hashedBytes = digest.digest(secret.getBytes());
        return Base64.encode(hashedBytes);
    }

    private static void logDebug(String message, Throwable throwable) {
        if (log.isDebugEnabled()) {
            log.debug(message, throwable);
        }
    }

    private PreparedStatement createSelectUserPreparedStatement(@NotNull Connection connection, String username)
            throws SQLException, UserStoreException {
        String sqlStatement = getSqlQuery(SELECT_USER, SELECT_USER_CASE_INSENSITIVE, "select user");
        logDebug(sqlStatement);

        PreparedStatement statement = connection.prepareStatement(sqlStatement);
        statement.setString(1, username);
        if (sqlStatement.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
            statement.setInt(2, tenantId);
        }
        return statement;
    }

    private String getSqlQuery(String caseSensitiveRealmProperty, String caseInsensitiveRealmProperty,
                               String operation) throws UserStoreException {
        String query = isCaseSensitiveUsername() ?
                realmConfig.getUserStoreProperty(caseSensitiveRealmProperty) :
                realmConfig.getUserStoreProperty(caseInsensitiveRealmProperty);
        if (query == null) {
            throw new UserStoreException("The SQL statement for " + operation + " is null");
        }
        return query;
    }

    private boolean isCaseSensitiveUsername() {
        String isUsernameCaseInsensitiveString = realmConfig.getUserStoreProperty(CASE_INSENSITIVE_USERNAME);
        return !Boolean.parseBoolean(isUsernameCaseInsensitiveString);
    }

    @Override
    public String[] doListUsers(String filter, int maxItemLimit) throws UserStoreException {
        if (maxItemLimit == 0) {
            return new String[0];
        }

        int givenMax = getConfiguredMaxUserList();
        int searchTime = getConfiguredSearchTime();

        maxItemLimit = adjustMaxItemLimit(maxItemLimit, givenMax);
        filter = prepareFilter(filter);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getDBConnection();
            statement = createSelectUserWithFilterPreparedStatement(connection, filter, maxItemLimit, searchTime);
            resultSet = statement.executeQuery();

            List<String> userList = extractUserList(resultSet);
            String[] users = userList.toArray(new String[0]);
            Arrays.sort(users);
            return users;
        } catch (SQLTimeoutException e) {
            log.error("Query timeout while fetching users. Ignoring error.", e);
            return new String[0];
        } catch (SQLException e) {
            String message = "Error occurred while retrieving users for filter : " + filter + " & max Item limit : " + maxItemLimit;
            logDebug(message, e);
            throw new DashboardUserStoreException("Authentication Failure", e);
        } finally {
            DatabaseUtil.closeAllConnections(connection, resultSet, statement);
        }
    }

    private List<String> extractUserList(ResultSet resultSet) throws SQLException {
        List<String> userList = new LinkedList<>();
        while (resultSet.next()) {
            String name = resultSet.getString(1);
            userList.add(name);
        }
        return userList;
    }

    private PreparedStatement createSelectUserWithFilterPreparedStatement(Connection connection, String filter,
                                                                          int maxItemLimit, int searchTime)
            throws SQLException, UserStoreException {
        if (connection == null) {
            throw new UserStoreException("Database connection is null");
        }

        String sqlQuery = getSqlQuery(GET_USER_FILTER, GET_USER_FILTER_CASE_INSENSITIVE, "select user with filter");
        PreparedStatement statement = connection.prepareStatement(sqlQuery);
        statement.setString(1, filter);
        if (sqlQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
            statement.setInt(2, tenantId);
        }
        statement.setMaxRows(maxItemLimit);
        try {
            statement.setQueryTimeout(searchTime);
        } catch (Exception e) {
            logDebug("Query timeout setting is not supported.", e);
        }
        return statement;
    }

    private int getConfiguredMaxUserList() {
        try {
            return Integer.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_USER_LIST));
        } catch (Exception e) {
            return UserCoreConstants.MAX_USER_ROLE_LIST;
        }
    }

    private int getConfiguredSearchTime() {
        try {
            return Integer.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_SEARCH_TIME));
        } catch (Exception e) {
            return UserCoreConstants.MAX_SEARCH_TIME;
        }
    }

    private int adjustMaxItemLimit(int maxItemLimit, int givenMax) {
        return (maxItemLimit < 0 || maxItemLimit > givenMax) ? givenMax : maxItemLimit;
    }

    private String prepareFilter(String filter) {
        if (filter == null || filter.trim().isEmpty()) {
            return "%";
        }
        return filter.trim().replace("*", "%").replace("?", "_");
    }

    @Override
    protected String[] doGetExternalRoleListOfUser(String username, String filter) throws UserStoreException {
        logDebug("Getting roles of user: " + username + " with filter: " + filter);
        boolean isWildcardFilter = StringUtils.isEmpty(filter) || "*".equals(filter);
        String[] names = isWildcardFilter ? executeRoleQuery(username)
                : executeRoleQueryWithFilter(username, filter);
        if (names == null) {
            logDebug("No external role found for the user: " + username);
        }
        return names != null ? names : new String[0];
    }

    @Override
    public String[] doGetUserListOfRole(String roleName) throws UserStoreException {
        RoleContext roleContext = createRoleContext(roleName);
        return getUserListOfJDBCRole(roleContext);
    }

    public String[] getUserListOfJDBCRole(RoleContext ctx) throws UserStoreException {
        String roleName = ctx.getRoleName();
        String sqlQuery = realmConfig.getUserStoreProperty(GET_USERS_IN_ROLE);
        if (sqlQuery == null) {
            throw new UserStoreException("The sql statement for retrieving user roles is null");
        }
        String[] userNames = sqlQuery.contains(UserCoreConstants.UM_TENANT_COLUMN) ?
                getStringValuesFromDatabase(sqlQuery, roleName, tenantId, tenantId, tenantId) :
                getStringValuesFromDatabase(sqlQuery, roleName);

        if (userNames == null || userNames.length == 0) {
            logDebug("Roles are not defined for the role name: " + roleName);
            return new String[0];
        }
        return userNames;
    }

    private String[] getStringValuesFromDatabase(String sqlQuery, Object... params) throws UserStoreException {
        logDebug("Executing Query: " + sqlQuery);
        Connection connection = null;
        try {
            connection = getDBConnection();
            return DatabaseUtil.getStringValuesFromDatabase(connection, sqlQuery, params);
        } catch (SQLException e) {
            String message = "Error occurred while retrieving string values.";
            logDebug(message, e);
            throw new DashboardUserStoreException(message, e);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

    @Override
    public boolean doCheckExistingUser(String username) throws UserStoreException {
        String sqlQuery = getSqlQuery(GET_IS_USER_EXISTING, GET_IS_USER_EXISTING_CASE_INSENSITIVE, "check existing user");
        boolean isUnique = Boolean.parseBoolean(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_USERNAME_UNIQUE));
        if (isUnique) {
            logDebug("The username should be unique across tenants.");
            return checkUsernameUniqueness(username);
        }
        return checkUserExistence(sqlQuery, username);
    }

    @Override
    public void doAddUser(String username, Object credential, String[] roleList,
                          Map<String, String> claims, String profileName, boolean requirePasswordChange)
            throws UserStoreException {
        String userId = UUID.randomUUID().toString();
        persistUser(userId, username, credential, roleList, claims, profileName, requirePasswordChange);
    }

    @Override
    public void doDeleteUser(String username) throws UserStoreException {
        String sqlDeleteUserRole = getSqlQuery(ON_DELETE_USER_REMOVE_USER_ROLE,
                ON_DELETE_USER_REMOVE_USER_ROLE_CASE_INSENSITIVE,
                "delete user-role mapping");
        String sqlDeleteUserAttribute = getSqlQuery(ON_DELETE_USER_REMOVE_ATTRIBUTE,
                ON_DELETE_USER_REMOVE_ATTRIBUTE_CASE_INSENSITIVE,
                "delete user attribute");
        String sqlDeleteUser = getSqlQuery(DELETE_USER,
                DELETE_USER_CASE_INSENSITIVE,
                "delete user");

        Connection connection = null;
        try {
            connection = getDBConnection();
            if (sqlDeleteUserRole.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
                updateStringValuesToDatabase(connection, sqlDeleteUserRole, username, tenantId, tenantId);
                updateStringValuesToDatabase(connection, sqlDeleteUserAttribute, username, tenantId, tenantId);
                updateStringValuesToDatabase(connection, sqlDeleteUser, username, tenantId);
            } else {
                updateStringValuesToDatabase(connection, sqlDeleteUserRole, username);
                updateStringValuesToDatabase(connection, sqlDeleteUserAttribute, username);
                updateStringValuesToDatabase(connection, sqlDeleteUser, username);
            }
            connection.commit();
        } catch (SQLException e) {
            String message = "Error occurred while deleting user : " + username;
            logDebug(message, e);
            throw new UserStoreException(message, e);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

    @Override
    public void doUpdateRoleListOfUser(String username, String[] rolesToRemove, String[] rolesToAdd)
            throws UserStoreException {
        Connection connection = null;
        try {
            connection = getDBConnection();
            removeRolesFromUser(getDBConnection(), username, rolesToRemove);
            addRolesToUser(connection, username, rolesToAdd);
            connection.commit();
        } catch (SQLException e) {
            String errorMessage = "Database error occurred while updating role list of user : " + username;
            logDebug(errorMessage, e);
            throw new UserStoreException(errorMessage, e);
        } catch (UserStoreException e) {
            String errorMessage = "Error occurred while updating role list of user:" + username;
            logDebug(errorMessage, e);
            throw new UserStoreException(e.getMessage(), e);
        } catch (Exception e) {
            String errorMessage = "Error occurred while getting database type from DB connection";
            logDebug(errorMessage, e);
            throw new UserStoreException(errorMessage, e);
        } finally {
            DatabaseUtil.closeAllConnections(connection);
        }
    }

    @Override
    public void doDeleteRole(String roleName) throws UserStoreException {
        String deleteUserRoleQuery = getSqlQuery(ON_DELETE_ROLE_REMOVE_USER_ROLE);
        String deleteRoleQuery = getSqlQuery(DELETE_ROLE);
        Connection connection = null;
        try {
            connection = getDBConnection();
            if (deleteUserRoleQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
                updateStringValuesToDatabase(connection, deleteUserRoleQuery, roleName, tenantId, tenantId);
                updateStringValuesToDatabase(connection, deleteRoleQuery, roleName, tenantId);
            } else {
                updateStringValuesToDatabase(connection, deleteUserRoleQuery, roleName);
                updateStringValuesToDatabase(connection, deleteRoleQuery, roleName);
            }
            connection.commit();
        } catch (SQLException e) {
            String message = "Error occurred while deleting role : " + roleName;
            logDebug(message, e);
            throw new UserStoreException(message, e);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

    @Override
    public void doUpdateCredentialByAdmin(String username, Object newCredential) throws UserStoreException {
        String sqlQuery = getSqlQuery(UPDATE_USER_PASSWORD, UPDATE_USER_PASSWORD_CASE_INSENSITIVE, "delete user claim");
        boolean hasTenantColumn = sqlQuery.contains(UserCoreConstants.UM_TENANT_COLUMN);

        String saltValue = shouldStoreSaltedPassword() ? generateSaltValue() : null;
        String password = preparePassword(newCredential, saltValue);

        if (hasTenantColumn && saltValue == null) {
            updateStringValuesToDatabase(null, sqlQuery, password, EMPTY_STRING, false, new Date(), username, tenantId);
            return;
        }
        if (hasTenantColumn) {
            updateStringValuesToDatabase(null, sqlQuery, password, saltValue, false, new Date(), username, tenantId);
            return;
        }
        if (saltValue == null) {
            updateStringValuesToDatabase(null, sqlQuery, password, EMPTY_STRING, false, new Date(), username);
            return;
        }
        updateStringValuesToDatabase(null, sqlQuery, password, saltValue, false, new Date(), username);
    }

    private boolean shouldStoreSaltedPassword() {
        return TRUE_VALUE.equalsIgnoreCase(realmConfig.getUserStoreProperties().get(STORE_SALTED_PASSWORDS));
    }

    @Override
    public void doUpdateCredential(String username, Object newCredential, Object oldCredential) throws UserStoreException {
        doUpdateCredentialByAdmin(username, newCredential);
    }

    @Override
    public String[] doGetRoleNames(String filter, int maxItemLimit) throws UserStoreException {
        if (maxItemLimit == 0) {
            return new String[0];
        }
        List<String> roles = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getDBConnection();
            statement = createGetRoleListPreparedStatement(connection, filter, maxItemLimit);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                roles.add(resultSet.getString(1));
            }
        } catch (SQLTimeoutException e) {
            log.error("The cause might be a time out. Hence ignored", e);
        } catch (SQLException e) {
            String errorMessage = "Error occurred while retrieving role names for filter : " + filter
                    + " & max item limit : " + maxItemLimit;
            logDebug(errorMessage, e);
            throw new UserStoreException(errorMessage, e);
        } finally {
            DatabaseUtil.closeAllConnections(connection, resultSet, statement);
        }
        return roles.toArray(new String[0]);
    }

    private PreparedStatement createGetRoleListPreparedStatement(@NotNull Connection connection, String filter, int maxItemLimit) throws SQLException, UserStoreException {
        String sqlQuery = getSqlQuery(GET_ROLE_LIST);
        PreparedStatement statement = connection.prepareStatement(sqlQuery);
        byte count = 0;
        statement.setString(++count, prepareFilter(filter));
        if (sqlQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
            statement.setInt(++count, tenantId);
        }
        setPSRestrictions(statement, maxItemLimit);
        return statement;
    }

    private void setPSRestrictions(PreparedStatement statement, int maxItemLimit) throws SQLException {
        int givenMax = getIntProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_ROLE_LIST, UserCoreConstants.MAX_USER_ROLE_LIST);
        int searchTime = getIntProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_SEARCH_TIME, UserCoreConstants.MAX_SEARCH_TIME);
        maxItemLimit = Math.max(0, Math.min(maxItemLimit, givenMax));


        statement.setMaxRows(maxItemLimit);
        try {
            statement.setQueryTimeout(searchTime);
        } catch (Exception e) {
            // this can be ignored since timeout method is not implemented
            logDebug(e.getMessage(), e);
        }
    }

    private int getIntProperty(String propertyKey, int defaultValue) {
        try {
            return Integer.parseInt(realmConfig.getUserStoreProperty(propertyKey));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String getSqlQuery(String realmProperty) throws UserStoreException {
        String query = realmConfig.getUserStoreProperty(realmProperty);
        if (query == null) {
            throw new UserStoreException("The sql statement for delete user-role mapping is null");
        }
        return query;
    }

    public boolean doCheckExistingRole(String roleName) throws UserStoreException {
        RoleContext roleContext = createRoleContext(roleName);
        return isExistingJDBCRole(roleContext);
    }

    @Override
    protected RoleContext createRoleContext(String roleName) {
        JDBCRoleContext searchCtx = new JDBCRoleContext();
        searchCtx.setTenantId(this.tenantId);
        searchCtx.setRoleName(roleName);
        return searchCtx;
    }

    @Override
    public void doAddRole(String roleName, String[] userList) throws UserStoreException {
        if (userList == null) {
            return;
        }
        Connection connection = null;
        try {
            connection = getDBConnection();
            String addRoleQuery = getSqlQuery(ADD_ROLE);
            if (addRoleQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
                updateStringValuesToDatabase(connection, addRoleQuery, roleName, tenantId);
            } else {
                updateStringValuesToDatabase(connection, addRoleQuery, roleName);
            }

            String databaseType = DatabaseCreator.getDatabaseType(connection);
            String addUserToRoleQuery = getAddUserToRoleQuery(databaseType);
            if (addUserToRoleQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
                if (UserCoreConstants.OPENEDGE_TYPE.equals(databaseType)) {
                    DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, addUserToRoleQuery, tenantId, userList, tenantId, roleName, tenantId);
                } else {
                    DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, addUserToRoleQuery, userList, tenantId, roleName, tenantId, tenantId);
                }
            } else {
                DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, addUserToRoleQuery, userList, roleName);
            }
            connection.commit();
        } catch (SQLException e) {
            String errorMessage = "Error occurred while adding role : " + roleName;
            logDebug(errorMessage, e);
            throw new UserStoreException(errorMessage, e);
        } catch (UserStoreException e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = "Error occurred while getting database type from DB connection";
            logDebug(errorMessage, e);
            throw new UserStoreException(errorMessage, e);
        } finally {
            DatabaseUtil.closeAllConnections(connection);
        }
    }

    @Override
    public boolean doCheckIsUserInRole(String username, String roleName) throws UserStoreException {
        String[] roles = doGetExternalRoleListOfUser(username, roleName);
        if (roles == null) {
            return false;
        }
        return Arrays.stream(roles).anyMatch(role -> role.equalsIgnoreCase(roleName));
    }

    protected boolean isExistingJDBCRole(RoleContext context) throws UserStoreException {
        String roleName = context.getRoleName();
        String sqlQuery = getSqlQuery(GET_IS_ROLE_EXISTING);
        return sqlQuery.contains(UserCoreConstants.UM_TENANT_COLUMN) ? isValueExisting(sqlQuery, roleName, ((JDBCRoleContext) context).getTenantId())
                : isValueExisting(sqlQuery, roleName);
    }

    protected boolean isValueExisting(String sqlQuery, Object... params) throws UserStoreException {
        Connection connection = null;
        try {
            connection = getDBConnection();
            return DatabaseUtil.getIntegerValueFromDatabase(connection, sqlQuery, params) > -1;
        } catch (SQLException e) {
            String message = "Error occurred while checking existence of values.";
            logDebug(message, e);
            throw new UserStoreException(message, e);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

    private String generateSaltValue() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SHA_1_PRNG);
            byte[] bytes = new byte[16];
            //secureRandom is automatically seeded by calling nextBytes
            secureRandom.nextBytes(bytes);
            return Base64.encode(bytes);
        } catch (NoSuchAlgorithmException e) {
            String errorMessage = "SHA1PRNG algorithm could not be found.";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    private void updateStringValuesToDatabase(Connection connection, String sqlQuery,
                                              Object... params) throws UserStoreException {
        PreparedStatement statement = null;
        boolean localConnection = false;
        try {
            if (connection == null) {
                localConnection = true;
                connection = getDBConnection();
            }
            statement = connection.prepareStatement(sqlQuery);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    setPreparedStatementParameter(statement, i + 1, param);
                }
            }
            int effectedRows = statement.executeUpdate();
            if (effectedRows == 0) {
                logDebug("No rows were updated");
            }
            logDebug("Executed query is " + sqlQuery + " and number of updated rows :: " + effectedRows);
            if (localConnection) {
                connection.commit();
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            String errorMessage = "Error occurred while updating string values to database.";
            throw new UserStoreException(errorMessage, UserCoreErrorConstants.ErrorMessages.ERROR_CODE_DUPLICATE_WHILE_WRITING_TO_DATABASE.getCode(), e);
        } catch (SQLException e) {
            String errorMessage = "Error occurred while updating string values to database.";
            logDebug(errorMessage, e);
            throw new UserStoreException(errorMessage, e);
        } finally {
            if (localConnection) {
                DatabaseUtil.closeConnection(connection);
            }
            DatabaseUtil.closeAllConnections(null, statement);
        }
    }

    private void setPreparedStatementParameter(PreparedStatement statement, int index, Object param)
            throws SQLException, UserStoreException {
        if (param == null) {
            throw new UserStoreException("Invalid data provided at parameter index: " + index);
        }
        if (param instanceof String) {
            statement.setString(index, (String) param);
        } else if (param instanceof Integer) {
            statement.setInt(index, (Integer) param);
        } else if (param instanceof Date) {
            statement.setTimestamp(index, new Timestamp(((Date) param).getTime()));
        } else if (param instanceof Boolean) {
            statement.setBoolean(index, (Boolean) param);
        } else {
            throw new UserStoreException("Unsupported parameter type at index " + index + ": " + param.getClass().getSimpleName());
        }
    }

    protected Connection getDBConnection() throws SQLException {
        Connection connection = getJDBCDataSource().getConnection();
        connection.setAutoCommit(false);
        if (connection.getTransactionIsolation() != Connection.TRANSACTION_READ_COMMITTED) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }
        return connection;
    }

    private DataSource getJDBCDataSource() {
        if (jdbcDataSource == null) {
            jdbcDataSource = loadUserStoreSpecificDataSource();
        }
        return jdbcDataSource;
    }

    private String getAddUserToRoleQuery(String databaseType) throws UserStoreException {
        try {
            return getSqlQuery(ADD_USER_TO_ROLE + "-" + databaseType, ADD_USER_TO_ROLE_CASE_INSENSITIVE + "-" + databaseType, "add user to role based on database databaseType");
        } catch (UserStoreException e) {
            return getSqlQuery(ADD_USER_TO_ROLE, ADD_USER_TO_ROLE_CASE_INSENSITIVE, "add user to role");
        }
    }

    private void removeRolesFromUser(@NotNull Connection connection, String username, String[] roles)
            throws SQLException, UserStoreException {
        if (roles == null || roles.length == 0) {
            return;
        }
        roles = Arrays.stream(roles).filter(StringUtils::isNotEmpty).toArray(String[]::new);
        if (roles.length == 0) {
            return;
        }
        String removeUserQuery = getSqlQuery(REMOVE_ROLE_FROM_USER, REMOVE_ROLE_FROM_USER_CASE_INSENSITIVE, "remove user");
        if (removeUserQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
            DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, removeUserQuery, roles, tenantId, username, tenantId, tenantId);
        } else {
            DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, removeUserQuery, roles, username);
        }
    }

    private void addRolesToUser(@NotNull Connection connection, String username, String[] rolesToAdd) throws Exception {
        if (rolesToAdd == null || rolesToAdd.length == 0) {
            return;
        }
        List<String> validNewRoles = filterValidNewRoles(username, rolesToAdd);
        if (validNewRoles.isEmpty()) {
            return;
        }
        String[] roles = validNewRoles.toArray(new String[0]);
        String addRoleToUserQuery = getSqlQuery(ADD_ROLE_TO_USER, ADD_ROLE_TO_USER_CASE_INSENSITIVE, "add role to user");
        String databaseType = DatabaseCreator.getDatabaseType(connection);

        if (!addRoleToUserQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
            DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, addRoleToUserQuery, roles, username);
            return;
        }
        if (UserCoreConstants.OPENEDGE_TYPE.equals(databaseType)) {
            DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, addRoleToUserQuery,
                    tenantId, roles, tenantId, username, tenantId);
            return;
        }
        DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, addRoleToUserQuery,
                roles, tenantId, username, tenantId, tenantId);
    }

    private List<String> filterValidNewRoles(String username, String[] newRoles) throws UserStoreException {
        List<String> validRoles = new ArrayList<>();
        for (String role : newRoles) {
            if (!StringUtils.isNotEmpty(role)) {
                continue;
            }
            if (!isExistingRole(role)) {
                throw new UserStoreException("The role: " + role + " does not exist.");
            }
            if (!isUserInRole(username, role)) {
                validRoles.add(role);
            }
        }
        return validRoles;
    }

    private boolean checkUsernameUniqueness(String username) throws UserStoreException {
        String uniquenessSql = isCaseSensitiveUsername()
                ? realmConfig.getUserStoreProperty(USER_NAME_UNIQUE)
                : realmConfig.getUserStoreProperty(USER_NAME_UNIQUE_CASE_INSENSITIVE);
        return isValueExisting(uniquenessSql, username);
    }

    private boolean checkUserExistence(String sqlQuery, String username) throws UserStoreException {
        return sqlQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)
                ? isValueExisting(sqlQuery, username, tenantId)
                : isValueExisting(sqlQuery, username);
    }

    private String[] executeRoleQuery(String username) throws UserStoreException {
        String sqlQuery = getSqlQuery(GET_USER_ROLE, GET_USER_ROLE_CASE_INSENSITIVE, "get user roles");
        if (sqlQuery.contains(UserStoreConstants.UM_TENANT_COLUMN)) {
            return getStringValuesFromDatabase(sqlQuery, username, tenantId, tenantId, tenantId);
        }
        return getStringValuesFromDatabase(sqlQuery, username);
    }

    private String[] executeRoleQueryWithFilter(String username, String filter) throws UserStoreException {
        String sqlQuery = getSqlQuery(GET_IS_USER_ROLE_EXIST, GET_IS_USER_ROLE_EXIST_CASE_INSENSITIVE, "user role exisit");
        filter = prepareFilter(filter);
        if (sqlQuery.contains(UserStoreConstants.UM_TENANT_COLUMN)) {
            return getStringValuesFromDatabase(sqlQuery, username, tenantId, tenantId, tenantId, filter);
        }
        return getStringValuesFromDatabase(sqlQuery, username, filter);
    }

    @Override
    public String[] getProfileNames(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUserClaimValue(String s, String s1, String s2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getUserClaimValues(String s, String[] strings, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Claim[] getUserClaimValues(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getAllProfileNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void addUser(String s, Object o, String[] strings, Map<String, String> map, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateUserListOfRole(String s, String[] strings, String[] strings1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserClaimValue(String s, String s1, String s2, String s3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserClaimValues(String s, Map<String, String> map, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUserClaimValue(String s, String s1, String s2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUserClaimValues(String s, String[] strings, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getAllSecondaryRoles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getPasswordExpirationTime(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUserId(String s) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTenantId(String s) {
        throw new UnsupportedOperationException();
    }

    public int getTenantId() {
        return this.tenantId;
    }

    @Override
    public Map<String, String> getProperties(Tenant tenant) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRoleName(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBulkImportSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getUserList(String s, String s1, String s2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserStoreManager getSecondaryUserStoreManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSecondaryUserStoreManager(UserStoreManager userStoreManager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserStoreManager getSecondaryUserStoreManager(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addSecondaryUserStoreManager(String s, UserStoreManager userStoreManager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RealmConfiguration getRealmConfiguration() {
        return realmConfig;
    }

    @Override
    public boolean isExistingRole(String s, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRole(String s, String[] strings, Permission[] permissions) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getProperties(org.wso2.micro.integrator.security.user.api.Tenant tenant) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMultipleProfilesAllowed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRememberMe(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValidRememberMeToken(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClaimManager getClaimManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSCIMEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Properties getDefaultUserStoreProperties() {
        throw new UnsupportedOperationException();
    }

    protected void persistUser(String userID, String username, Object credential, String[] roleList,
                               Map<String, String> claims, String profileName, boolean requirePasswordChange)
            throws UserStoreException {
        Connection connection;
        try {
            connection = getDBConnection();
        } catch (SQLException e) {
            String errorMessage = "Error occurred while getting DB connection";
            logDebug(errorMessage, e);
            throw new UserStoreException(errorMessage, e);
        }
        try (Secret secret = Secret.getSecret(credential)) {
            String addUserWithIdQuery = getSqlQuery(ADD_USER_WITH_ID);
            String saltValue = shouldStoreSaltedPassword() ? generateSaltValue() : null;
            String password = preparePassword(secret, saltValue);

            boolean hasTenantColumn = addUserWithIdQuery.contains(UserCoreConstants.UM_TENANT_COLUMN);
            if (hasTenantColumn && (saltValue == null)) {
                updateStringValuesToDatabase(connection, addUserWithIdQuery, userID, username, password, EMPTY_STRING, requirePasswordChange, new Date(), tenantId);
            } else if (hasTenantColumn) {
                updateStringValuesToDatabase(connection, addUserWithIdQuery, userID, username, password, saltValue, requirePasswordChange, new Date(), tenantId);
            } else if (saltValue == null) {
                updateStringValuesToDatabase(connection, addUserWithIdQuery, userID, username, password, EMPTY_STRING, requirePasswordChange, new Date());
            } else {
                updateStringValuesToDatabase(connection, addUserWithIdQuery, userID, username, password, saltValue, requirePasswordChange, new Date());
            }

            if (roleList != null && roleList.length > 0) {
                String[] roles = Arrays.stream(roleList).filter(StringUtils::isNotEmpty).toArray(String[]::new);
                String databaseType = DatabaseCreator.getDatabaseType(connection);
                if (roles.length > 0) {
                    String addRoleToUserQuery = getAddRoleToUserQuery(databaseType);
                    if (addRoleToUserQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
                        if (UserCoreConstants.OPENEDGE_TYPE.equals(databaseType)) {
                            DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, addRoleToUserQuery, tenantId, roles, tenantId, username, tenantId);
                        } else {
                            DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, addRoleToUserQuery, roles, tenantId, username, tenantId, tenantId);
                        }
                    } else {
                        DatabaseUtil.udpateUserRoleMappingInBatchMode(connection, addRoleToUserQuery, roleList, username);
                    }
                }
            }
            if (claims != null) {
                profileName = (profileName == null) ? UserCoreConstants.DEFAULT_PROFILE : profileName;
                addProperties(connection, username, claims, profileName);
            }
            connection.commit();
        } catch (UnsupportedSecretTypeException e) {
            throw new UserStoreException("Unsupported credential type", e);
        } catch (Exception e) {
            rollbackPersistUser(connection, username, e);
        } finally {
            DatabaseUtil.closeAllConnections(connection);
        }
    }

    private String getAddRoleToUserQuery(String databaseType) throws UserStoreException {
        try {
            return getSqlQuery(ADD_ROLE_TO_USER + "-" + databaseType, ADD_ROLE_TO_USER_CASE_INSENSITIVE + "-" + databaseType, "add role to user with database type");
        } catch (UserStoreException e) {
            return getSqlQuery(ADD_ROLE_TO_USER, ADD_ROLE_TO_USER_CASE_INSENSITIVE, "add role to user");
        }
    }

    private void addProperties(@NotNull Connection connection, String username, Map<String, String> properties,
                               String profileName) throws UserStoreException {
        String databaseType;
        try {
            databaseType = DatabaseCreator.getDatabaseType(connection);
        } catch (Exception e) {
            String message = "Error occurred while adding user properties for user : " + username;
            logDebug(message, e);
            throw new UserStoreException(message, e);
        }

        String sqlQuery = getAddUserPropertySqlStatement(databaseType);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlQuery);
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String propertyName = getClaimAttribute(entry.getKey());
                String propertyValue = entry.getValue();
                if (sqlQuery.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
                    if (UserCoreConstants.OPENEDGE_TYPE.equals(databaseType)) {
                        batchUpdateStringValuesToDatabase(statement, propertyName, propertyValue, profileName, tenantId, username, tenantId);
                    } else {
                        batchUpdateStringValuesToDatabase(statement, username, tenantId, propertyName, propertyValue, profileName, tenantId);
                    }
                } else {
                    batchUpdateStringValuesToDatabase(statement, username, propertyName, propertyValue, profileName);
                }
            }
            int[] counts = statement.executeBatch();
            int totalUpdated = Arrays.stream(counts).sum();
            logDebug("Executed query is " + sqlQuery + " and number of updated rows :: " + totalUpdated);
        } catch (SQLException e) {
            String message = "Error occurred while updating string values to database.";
            logDebug(message, e);
            throw new UserStoreException(message, e);
        } finally {
            DatabaseUtil.closeAllConnections(null, statement);
        }
    }

    private String getAddUserPropertySqlStatement(String databaseType) throws UserStoreException {
        String sqlQuery = realmConfig.getUserStoreProperty(ADD_USER_PROPERTY + "-" + databaseType);
        if (sqlQuery == null) {
            sqlQuery = realmConfig.getUserStoreProperty(ADD_USER_PROPERTY);
        }
        if (sqlQuery == null) {
            throw new UserStoreException("The sql statement for add user property sql is null");
        }
        return sqlQuery;
    }

    protected String getClaimAttribute(String claimURI) throws UserStoreException {
        try {
            String attributeName = claimManager.getAttributeName(claimURI);
            if (attributeName != null) {
                return attributeName;
            }
        } catch (org.wso2.micro.integrator.security.user.api.UserStoreException e) {
            throw new UserStoreException(e.getMessage(), e);
        }
        switch (claimURI) {
            case UserCoreConstants.PROFILE_CONFIGURATION:
                return claimURI;
            case DISPLAY_NAME_CLAIM:
                return realmConfig.getUserStoreProperty(LDAPConstants.DISPLAY_NAME_ATTRIBUTE);
            default:
                throw new UserStoreException("Mapped attribute cannot be found for claim: " + claimURI + " in user store.");
        }
    }

    private void batchUpdateStringValuesToDatabase(PreparedStatement statement, Object... params) throws UserStoreException {
        if (params == null || params.length == 0) {
            throw new UserStoreException("No parameters provided for batch update.");
        }
        try {
            for (int i = 0; i < params.length; i++) {
                setPreparedStatementParameter(statement, i + 1, params[i]);
            }
            statement.addBatch();
        } catch (SQLException e) {
            String message = "Error occurred while updating property values to database.";
            logDebug(message, e);
            throw new UserStoreException(message, e);
        }
    }
}

