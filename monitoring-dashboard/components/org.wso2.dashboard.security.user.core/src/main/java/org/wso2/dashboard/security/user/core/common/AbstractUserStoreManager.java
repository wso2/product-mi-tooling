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

package org.wso2.dashboard.security.user.core.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.micro.integrator.security.user.api.Permission;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.core.UserCoreConstants.RealmConfig;
import org.wso2.micro.integrator.security.user.core.UserStoreException;
import org.wso2.micro.integrator.security.user.core.UserStoreManager;
import org.wso2.micro.integrator.security.user.core.claim.ClaimManager;
import org.wso2.micro.integrator.security.user.core.claim.ClaimMapping;
import org.wso2.micro.integrator.security.user.core.common.RoleContext;
import org.wso2.micro.integrator.security.user.core.constants.UserCoreErrorConstants.ErrorMessages;
import org.wso2.micro.integrator.security.user.core.multiplecredentials.UserAlreadyExistsException;

import javax.sql.DataSource;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.wso2.dashboard.security.user.core.UserStoreConstants.RealmConfig.LEADING_OR_TRAILING_SPACE_ALLOWED_IN_USERNAME;
import static org.wso2.dashboard.security.user.core.UserStoreConstants.RealmConfig.PROPERTY_JAVA_REG_EX;
import static org.wso2.micro.integrator.security.user.core.UserCoreConstants.RealmConfig.READ_GROUPS_ENABLED;
import static org.wso2.micro.integrator.security.user.core.constants.UserCoreErrorConstants.ErrorMessages.*;
import static org.wso2.micro.integrator.security.user.core.constants.UserCoreErrorConstants.ErrorMessages.ERROR_CODE_CANNOT_REMOVE_ADMIN_ROLE_FROM_ADMIN;
import static org.wso2.micro.integrator.security.user.core.constants.UserCoreErrorConstants.ErrorMessages.ERROR_CODE_ERROR_WHILE_AUTHENTICATION;
import static org.wso2.micro.integrator.security.user.core.constants.UserCoreErrorConstants.ErrorMessages.ERROR_CODE_ERROR_WHILE_PRE_AUTHENTICATION;
import static org.wso2.micro.integrator.security.user.core.constants.UserCoreErrorConstants.ErrorMessages.ERROR_CODE_UNSUPPORTED_CREDENTIAL_TYPE;

public abstract class AbstractUserStoreManager implements UserStoreManager {
    private static final Log log = LogFactory.getLog(AbstractUserStoreManager.class);
    private static final int MAX_ITEM_LIMIT_UNLIMITED = -1;
    private static final String PROPERTY_PASSWORD_ERROR_MSG = "PasswordJavaRegExViolationErrorMsg";
    protected int tenantId;
    protected DataSource dataSource = null;
    protected RealmConfiguration realmConfig = null;
    protected ClaimManager claimManager = null;
    protected boolean readGroupsEnabled = false;
    protected boolean writeGroupsEnabled = false;

    @Override
    public boolean authenticate(final String username, final Object credential) throws UserStoreException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>) () -> {
                validateUsernameAndCredentialPresence(username, credential);
                return authenticateInternal(username, credential);
            });
        } catch (PrivilegedActionException e) {
            handlePrivilegedActionException(e);
            return false;
        }
    }

    private boolean authenticateInternal(String username, Object credential) throws UserStoreException {
        try (Secret credentialObj = Secret.getSecret(credential)) {
            if (doAuthenticate(username, credentialObj)) {
                return true;
            }
        } catch (UnsupportedSecretTypeException e) {
            log.error("Error occurred while authenticating user: " + username, e);
            throw new DashboardUserStoreException(ERROR_CODE_UNSUPPORTED_CREDENTIAL_TYPE.getMessage(),
                    ERROR_CODE_UNSUPPORTED_CREDENTIAL_TYPE.getCode(), e);
        } catch (Exception e) {
            log.error("Error occurred while authenticating user: " + username, e);
            throw new UserStoreException(ERROR_CODE_ERROR_WHILE_AUTHENTICATION.getMessage(),
                    ERROR_CODE_ERROR_WHILE_AUTHENTICATION.getCode(), e);
        }
        logDebug("Authentication failure. Wrong username or password is provided.");
        throw new DashboardUserStoreException(ERROR_CODE_ERROR_WHILE_AUTHENTICATION.getMessage(),
                ERROR_CODE_ERROR_WHILE_AUTHENTICATION.getCode());
    }

    /**
     * Validates the user's authentication by verifying the provided credentials.
     *
     * @param username   the username to authenticate
     * @param credential the user's credentials to validate
     * @return {@code true} if the credentials match the username; {@code false} if the credentials are invalid,
     * the username is invalid, or the credentials do not match the username
     * @throws UserStoreException if an unexpected error occurs during authentication
     */
    protected abstract boolean doAuthenticate(String username, Object credential) throws UserStoreException;

    private static void logDebug(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
    }

    /**
     * Validates the provided username and credential for authentication.
     *
     * @param username   the name of the user
     * @param credential the user's credential
     * @throws UserStoreException if authentication validation fails
     */
    private void validateUsernameAndCredentialPresence(String username, Object credential) throws UserStoreException {
        if (username == null || credential == null) {
            String message = String.format(ERROR_CODE_ERROR_WHILE_PRE_AUTHENTICATION.getMessage(),
                    "Authentication failure. Either Username or Password is null");
            log.error(message);
            throw new DashboardUserStoreException(message, ERROR_CODE_ERROR_WHILE_PRE_AUTHENTICATION.getCode());
        }
    }

    public final String[] listUsers(String filter, int maxItemLimit) throws UserStoreException {
        return doListUsers(filter, maxItemLimit);
    }

    protected abstract String[] doListUsers(String filter, int maxItemLimit) throws UserStoreException;

    @Override
    public boolean isExistingUser(String username) throws UserStoreException {
        return this.doCheckExistingUser(username);
    }

    @Override
    public boolean isExistingRole(String roleName) throws UserStoreException {
        return doCheckExistingRole(roleName);
    }

    @Override
    public final String[] getRoleNames() throws UserStoreException {
        return getRoleNames("*", MAX_ITEM_LIMIT_UNLIMITED);
    }

    @Override
    public final String[] getRoleNames(boolean noHybridRoles) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getRoleListOfUser(String username) throws UserStoreException {
        return doGetRoleListOfUser(username, "*");
    }

    public final String[] doGetRoleListOfUser(String username, String filter) throws UserStoreException {
        if (!readGroupsEnabled) {
            return new String[0];
        }
        String[] externalRoles = doGetExternalRoleListOfUser(username, filter);
        return externalRoles != null ? externalRoles : new String[0];
    }

    /**
     * Retrieves only the external roles associated with the specified user.
     *
     * @param username the name of the user whose roles are to be retrieved
     * @param filter   an optional filter to refine the role list
     * @return an array of external roles associated with the user
     * @throws UserStoreException if an error occurs while fetching the external roles
     */
    protected abstract String[] doGetExternalRoleListOfUser(String username, String filter) throws UserStoreException;

    public final String[] getUserListOfRole(String roleName) throws UserStoreException {
        if (!isExistingRole(roleName)) {
            return new String[0];
        }
        return readGroupsEnabled ? doGetUserListOfRole(roleName) : new String[0];
    }

    protected abstract String[] doGetUserListOfRole(String roleName) throws UserStoreException;

    @Override
    public void addUser(String username, Object credential, String[] roles, Map<String, String> claims,
                        String profileName, boolean requirePasswordChange) throws UserStoreException {
        if (isReadOnly()) {
            throw new UserStoreException(ERROR_CODE_READONLY_USER_STORE.toString());
        }

        validateUsername(username);
        validateForExistingUsername(username);

        try (Secret secret = Secret.getSecret(credential)) {
            validatePassword(secret);

            if (roles == null) {
                roles = new String[0];
            }
            List<String> filteredRoles = Arrays.stream(roles).filter(role -> role != null && !role.trim().isEmpty())
                    .collect(Collectors.toList());
            validateExistingRoles(filteredRoles);

            if (claims == null) {
                claims = new HashMap<>();
            }
            validateClaims(claims);

            doAddUser(username, secret, filteredRoles.toArray(new String[0]), claims, profileName, requirePasswordChange);
        } catch (UnsupportedSecretTypeException e) {
            throw new UserStoreException(ERROR_CODE_UNSUPPORTED_CREDENTIAL_TYPE.toString(), e);
        }
    }

    private void validateClaims(Map<String, String> claims) throws UserStoreException {
        for (String claimUri : claims.keySet()) {
            validateClaim(claimUri);
        }
    }

    private void validateClaim(String claimUri) throws UserStoreException {
        try {
            ClaimMapping claimMapping = (ClaimMapping) claimManager.getClaimMapping(claimUri);
            if (claimMapping != null) {
                return;
            }
        } catch (org.wso2.micro.integrator.security.user.api.UserStoreException e) {
            String errorMessage = String.format(ERROR_CODE_UNABLE_TO_FETCH_CLAIM_MAPPING.getMessage(), "persisting user attributes.");
            String errorCode = ERROR_CODE_UNABLE_TO_FETCH_CLAIM_MAPPING.getCode();
            throw new UserStoreException(errorCode + " - " + errorMessage, e);
        }
        String errorMessage = String.format(ERROR_CODE_INVALID_CLAIM_URI.getMessage(), claimUri);
        String errorCode = ERROR_CODE_INVALID_CLAIM_URI.getCode();
        throw new UserStoreException(errorCode + " - " + errorMessage);
    }

    private void validateExistingRoles(List<String> roles) throws UserStoreException {
        for (String role : roles) {
            validateExistingRole(role);
        }
    }

    private void validateExistingRole(String role) throws UserStoreException {
        if (!doCheckExistingRole(role)) {
            String errorMessage = String.format(ERROR_CODE_EXTERNAL_ROLE_NOT_EXISTS.getMessage(), role);
            String errorCode = ERROR_CODE_EXTERNAL_ROLE_NOT_EXISTS.getCode();
            throw new UserStoreException(errorCode + " - " + errorMessage);
        }
    }

    private void validatePassword(Secret secret) throws UserStoreException {
        if (!isValidPasswordFormat(secret)) {
            String passwordRegex = realmConfig.getUserStoreProperty(RealmConfig.PROPERTY_JAVA_REG_EX);
            String message = String.format(ERROR_CODE_INVALID_PASSWORD.getMessage(), passwordRegex);
            String errorCode = ERROR_CODE_INVALID_PASSWORD.getCode();
            throw new UserStoreException(errorCode + " - " + message);
        }
    }

    protected boolean isValidPasswordFormat(Object credential) throws UserStoreException {
        if (credential == null) {
            return false;
        }
        try (Secret secret = Secret.getSecret(credential)) {
            char[] passwordChars = secret.getChars();
            if (passwordChars.length <= 1) {
                return false;
            }
            String passwordRegex = realmConfig.getUserStoreProperty(PROPERTY_JAVA_REG_EX);
            if (passwordRegex == null) {
                return true;
            }
            boolean isValid = hasValidFormat(passwordRegex, passwordChars);
            if (!isValid) {
                logDebug("Submitted password does not match the regex: " + passwordRegex);
            }
            return isValid;
        } catch (UnsupportedSecretTypeException e) {
            throw new DashboardUserStoreException("Unsupported credential type", e);
        }
    }

    private boolean hasValidFormat(String regex, char[] attribute) {
        return hasValidFormat(regex, String.valueOf(attribute));
    }

    private void validateUsername(String username) throws UserStoreException {
        if (!isValidUsername(username)) {
            String usernameRegex = getEffectiveUsernameRegex();
            String errorMessage = String.format(ERROR_CODE_INVALID_USER_NAME.getMessage(), null, usernameRegex);
            String errorCode = ERROR_CODE_INVALID_USER_NAME.getCode();
            throw new UserStoreException(errorCode + " - " + errorMessage);
        }
    }

    protected boolean isValidUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }

        String allowLeadingOrTrailingSpace = realmConfig.getUserStoreProperty(LEADING_OR_TRAILING_SPACE_ALLOWED_IN_USERNAME);
        if (StringUtils.isEmpty(allowLeadingOrTrailingSpace)) {
            // Keeping old behavior for backward-compatibility.
            username = username.trim();
        } else {
            logDebug("'LeadingOrTrailingSpaceAllowedInUserName' property is set to : "
                    + allowLeadingOrTrailingSpace + ". Hence username trimming will be skipped during "
                    + "validation for the username: " + username);
        }

        if (username.isEmpty()) {
            return false;
        }

        String usernameRegex = getEffectiveUsernameRegex();
        if (StringUtils.isNotEmpty(usernameRegex)) {
            if (hasValidFormat(usernameRegex.trim(), username)) {
                return true;
            }
            logDebug("Username " + username + " does not match with the regex " + usernameRegex);
            return false;
        }
        return true;
    }

    private String getEffectiveUsernameRegex() {
        String regex = realmConfig.getUserStoreProperty(RealmConfig.PROPERTY_USER_NAME_JAVA_REG_EX);
        if (StringUtils.isEmpty(regex) || StringUtils.isEmpty(regex.trim())) {
            regex = realmConfig.getUserStoreProperty(RealmConfig.PROPERTY_USER_NAME_JAVA_REG);
        }
        return regex;
    }

    private boolean hasValidFormat(String regularExpression, String attribute) {
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(attribute);
        return matcher.matches();
    }

    private void validateForExistingUsername(String username) throws UserStoreException {
        if (doCheckExistingUser(username)) {
            String message = String.format(ERROR_CODE_USER_ALREADY_EXISTS.getMessage(), username);
            String errorCode = ERROR_CODE_USER_ALREADY_EXISTS.getCode();
            throw new UserAlreadyExistsException(errorCode + " - " + message);
        }
    }

    /**
     * Adds a user to the user store.
     *
     * @param username              the username of the user
     * @param credential            the user's credential or password
     * @param roleList              the list of roles the user belongs to
     * @param claims                user properties or attributes
     * @param profileName           the profile name; if {@code null}, the default profile is used
     * @param requirePasswordChange indicates whether the user is required to change the password
     * @throws UserStoreException if an unexpected error occurs during user addition
     */
    protected abstract void doAddUser(String username, Object credential, String[] roleList, Map<String, String> claims,
                                      String profileName, boolean requirePasswordChange) throws UserStoreException;

    public final void updateCredential(String username, Object newCredential, Object oldCredential) throws UserStoreException {
        if (isReadOnly()) {
            throw new DashboardUserStoreException(ERROR_CODE_READONLY_USER_STORE.toString(), ERROR_CODE_READONLY_USER_STORE.getCode());
        }

        try (Secret newSecret = Secret.getSecret(newCredential); Secret oldSecret = Secret.getSecret(oldCredential)) {
            boolean authenticated = this.doAuthenticate(username, oldSecret);
            if (!authenticated) {
                throw new DashboardUserStoreException(ERROR_CODE_OLD_CREDENTIAL_DOES_NOT_MATCH.getMessage(), ERROR_CODE_OLD_CREDENTIAL_DOES_NOT_MATCH.getCode());
            }
            validateNewCredential(newCredential, false);
            doUpdateCredential(username, newSecret, oldSecret);
        } catch (UnsupportedSecretTypeException e) {
            throw new DashboardUserStoreException(ERROR_CODE_UNSUPPORTED_CREDENTIAL_TYPE.toString(), ERROR_CODE_UNSUPPORTED_CREDENTIAL_TYPE.getCode());
        }
    }

    private void validateNewCredential(Object credential, boolean byAdmin) throws UserStoreException {
        if (!isValidPasswordFormat(credential)) {
            String errorMsg = realmConfig.getUserStoreProperty(PROPERTY_PASSWORD_ERROR_MSG);
            if (errorMsg != null) {
                ErrorMessages error = byAdmin ? ERROR_CODE_ERROR_DURING_PRE_UPDATE_CREDENTIAL_BY_ADMIN
                        : ERROR_CODE_ERROR_DURING_PRE_UPDATE_CREDENTIAL;
                String message = String.format(error.getMessage(), errorMsg);
                throw new UserStoreException(error.getCode() + " - " + message);
            }
            String errorMessage = String.format(ERROR_CODE_INVALID_PASSWORD.getMessage(), realmConfig.getUserStoreProperty(RealmConfig.PROPERTY_JAVA_REG_EX));
            String errorCode = ERROR_CODE_INVALID_PASSWORD.getCode();
            throw new UserStoreException(errorCode + " - " + errorMessage);
        }
    }

    public final void updateCredentialByAdmin(String username, Object newCredential) throws UserStoreException {
        if (isReadOnly()) {
            throw new UserStoreException(ERROR_CODE_READONLY_USER_STORE.toString());
        }
        validateNewCredential(newCredential, true);
        try (Secret secret = Secret.getSecret(newCredential)) {
            if (!doCheckExistingUser(username)) {
                String errorMessage = String.format(ERROR_CODE_NON_EXISTING_USER.getMessage(), username,
                        realmConfig.getUserStoreProperty(RealmConfig.PROPERTY_DOMAIN_NAME));
                String errorCode = ERROR_CODE_NON_EXISTING_USER.getCode();
                throw new UserStoreException(errorCode + "-" + errorMessage);
            }
            doUpdateCredentialByAdmin(username, secret);
        } catch (UnsupportedSecretTypeException e) {
            throw new UserStoreException(ERROR_CODE_UNSUPPORTED_CREDENTIAL_TYPE.toString(), e);
        }
    }

    public final void deleteUser(String username) throws UserStoreException {
        if (isReadOnly()) {
            throw new UserStoreException(ERROR_CODE_READONLY_USER_STORE.toString());
        }
        if (realmConfig.getAdminUserName().equals(username)) {
            throw new UserStoreException(ERROR_CODE_DELETE_ADMIN_USER.toString());
        }
        if (!doCheckExistingUser(username)) {
            String errorMessage = String.format(ERROR_CODE_NON_EXISTING_USER.getMessage(), username,
                    realmConfig.getUserStoreProperty(RealmConfig.PROPERTY_DOMAIN_NAME));
            String errorCode = ERROR_CODE_NON_EXISTING_USER.getCode();
            throw new UserStoreException(errorCode + " - " + errorMessage);
        }
        doDeleteUser(username);
    }

    /**
     * Delete the user with the given username
     *
     * @param username The username
     * @throws UserStoreException An unexpected exception has occurred
     */
    protected abstract void doDeleteUser(String username) throws UserStoreException;

    /**
     * Delete the role with the given role name.
     *
     * @param roleName The role name to delete.
     * @throws UserStoreException If an error occurs while deleting the role.
     */
    public final void deleteRole(String roleName) throws UserStoreException {
        if (isReadOnly()) {
            throw new UserStoreException(ERROR_CODE_READONLY_USER_STORE.toString());
        }
        if (realmConfig.getAdminRoleName().equalsIgnoreCase(roleName)) {
            throw new UserStoreException(ERROR_CODE_CANNOT_DELETE_ADMIN_ROLE.toString());
        }
        if (!doCheckExistingRole(roleName)) {
            throw new UserStoreException(ERROR_CODE_CANNOT_DELETE_NON_EXISTING_ROLE.toString());
        }
        if (!writeGroupsEnabled) {
            throw new UserStoreException(ERROR_CODE_WRITE_GROUPS_NOT_ENABLED.toString());
        }
        doDeleteRole(roleName);
    }

    public final void updateRoleListOfUser(final String username, final String[] deletedRoles, final String[] newRoles)
            throws UserStoreException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<String>) () -> {
                updateRoleListOfUserInternal(username, deletedRoles, newRoles);
                return null;
            });
        } catch (PrivilegedActionException e) {
            throw (UserStoreException) e.getException();
        }
    }

    private void updateRoleListOfUserInternal(String username, String[] deletedRoles, String[] newRoles)
            throws UserStoreException {
        if (deletedRoles == null) {
            deletedRoles = new String[0];
        }
        if (deletedRoles.length > 0) {
            Arrays.sort(deletedRoles);
            validateRemovalOfAdminRoleFromSuperAdmin(username, deletedRoles);
        }

        if (newRoles == null) {
            newRoles = new String[0];
        }
        if (deletedRoles.length > 0 || newRoles.length > 0) {
            if (!isReadOnly() && writeGroupsEnabled) {
                doUpdateRoleListOfUser(username, deletedRoles, newRoles);
                return;
            }
            throw new UserStoreException(ERROR_CODE_READONLY_USER_STORE.toString());
        }
    }

    private void validateRemovalOfAdminRoleFromSuperAdmin(String username, String[] deletedRoles) throws UserStoreException {
        if (!realmConfig.getAdminUserName().equals(username)) {
            return;
        }
        // Check if the admin role is being deleted from the admin user
        for (String deletedRole : deletedRoles) {
            if (deletedRole.equalsIgnoreCase(realmConfig.getAdminRoleName())) {
                String errorMessage = String.format("Attempt to remove admin role from the admin user: %s", username);
                log.error(errorMessage);  // Log the error
                throw new UserStoreException(ERROR_CODE_CANNOT_REMOVE_ADMIN_ROLE_FROM_ADMIN.toString());
            }
        }
    }

    /**
     * Update role list of a particular user
     *
     * @param username     The username
     * @param deletedRoles Array of role names, that is going to be removed from the user
     * @param newRoles     Array of role names, that is going to be added to the user
     * @throws UserStoreException An unexpected exception has occurred
     */
    protected abstract void doUpdateRoleListOfUser(String username, String[] deletedRoles,
                                                   String[] newRoles) throws UserStoreException;

    @Override
    public final String[] getHybridRoles() {
        throw new UnsupportedOperationException();
    }

    protected abstract void doDeleteRole(String roleName) throws UserStoreException;

    /**
     * Update credential/password by the admin of another user
     *
     * @param username      The username
     * @param newCredential The new credential
     * @throws UserStoreException An unexpected exception has occurred
     */
    protected abstract void doUpdateCredentialByAdmin(String username, Object newCredential) throws UserStoreException;

    /**
     * Update the credential/password of the user
     *
     * @param username      The username
     * @param newCredential The new credential/password
     * @param oldCredential The old credential/password
     * @throws UserStoreException An unexpected exception has occurred
     */
    protected abstract void doUpdateCredential(String username, Object newCredential, Object oldCredential)
            throws UserStoreException;

    public final String[] getRoleNames(String filter, int maxItemLimit) throws UserStoreException {
        return readGroupsEnabled ? doGetRoleNames(filter, maxItemLimit) : new String[0];
    }

    /**
     * Retrieves the role names based on the given filter and maximum item limit.
     *
     * @param filter       the filter to apply when retrieving role names
     * @param maxItemLimit the maximum number of role names to return
     * @return an array of role names matching the filter
     * @throws UserStoreException if an error occurs while retrieving the role names
     */
    protected abstract String[] doGetRoleNames(String filter, int maxItemLimit) throws UserStoreException;

    protected abstract boolean doCheckExistingRole(String roleName) throws UserStoreException;

    protected abstract boolean doCheckExistingUser(String username) throws UserStoreException;

    private void handlePrivilegedActionException(PrivilegedActionException exception) throws UserStoreException {
        throw new UserStoreException(ERROR_CODE_ERROR_WHILE_AUTHENTICATION.getMessage(), exception.getCause());
    }

    protected abstract RoleContext createRoleContext(String roleName) throws UserStoreException;

    @Override
    public void addRole(String roleName, String[] userList, Permission[] permissions, boolean isSharedRole) throws UserStoreException {
        if (isReadOnly()) {
            throw new UserStoreException(ERROR_CODE_READONLY_USER_STORE.toString());
        }
        if (StringUtils.isEmpty(roleName)) {
            throw new UserStoreException(ERROR_CODE_CANNOT_ADD_EMPTY_ROLE.toString());
        }
        if (userList == null) {
            userList = new String[0];
        }
        validateRoleName(roleName);
        validateExistingRoleName(roleName);
        if (!writeGroupsEnabled) {
            throw new UserStoreException(ERROR_CODE_WRITE_GROUPS_NOT_ENABLED.toString());
        }
        doAddRole(roleName, userList);
    }

    private void validateExistingRoleName(String roleName) throws UserStoreException {
        if (doCheckExistingRole(roleName)) {
            String errorCode = ERROR_CODE_ROLE_ALREADY_EXISTS.getCode();
            String errorMessage = String.format(ERROR_CODE_ROLE_ALREADY_EXISTS.getMessage(), roleName);
            throw new UserStoreException(errorCode + " - " + errorMessage);
        }
    }

    private void validateRoleName(String roleName) throws UserStoreException {
        if (!isRoleNameValid(roleName)) {
            String regEx = realmConfig.getUserStoreProperty(RealmConfig.PROPERTY_ROLE_NAME_JAVA_REG_EX);
            String errorMessage = String.format(ERROR_CODE_INVALID_ROLE_NAME.getMessage(), roleName, regEx);
            String errorCode = ERROR_CODE_INVALID_ROLE_NAME.getCode();
            throw new UserStoreException(errorCode + " - " + errorMessage);
        }
    }

    protected boolean isRoleNameValid(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            return false;
        }
        String regularExpression = realmConfig.getUserStoreProperty(RealmConfig.PROPERTY_ROLE_NAME_JAVA_REG_EX);
        if (regularExpression != null) {
            return hasValidFormat(regularExpression, roleName);
        }
        return true;
    }

    protected abstract void doAddRole(String roleName, String[] userList) throws UserStoreException;

    public boolean isUserInRole(String username, String roleName) throws UserStoreException {
        if (roleName == null || roleName.trim().isEmpty() || username == null || username.trim().isEmpty()) {
            return false;
        }
        return readGroupsEnabled && doCheckIsUserInRole(username, roleName);
    }

    public abstract boolean doCheckIsUserInRole(String username, String roleName) throws UserStoreException;

    protected void addInitialAdminData(boolean addAdmin) throws UserStoreException {
        String adminUsername = realmConfig.getAdminUserName();
        String adminRoleName = realmConfig.getAdminRoleName();
        if (adminUsername == null || adminRoleName == null) {
            String message = "Admin user name or role name is not valid. Please provide valid values.";
            log.error(message);
            throw new UserStoreException(message);
        }
        if (!checkUserExistence(adminUsername)) {
            handleAdminUserCreation(adminUsername, addAdmin);
        }
        if (!checkRoleExistence(adminRoleName)) {
            handleAdminRoleCreation(adminRoleName, adminUsername, addAdmin);
        }
        assignUserToRole(adminUsername, adminRoleName, addAdmin);
    }

    private boolean checkRoleExistence(String roleName) {
        try {
            if (Boolean.parseBoolean(this.getRealmConfiguration().getUserStoreProperty(READ_GROUPS_ENABLED))) {
                return doCheckExistingRole(roleName);
            }
        } catch (Exception e) {
            log.error("Error while checking role existence: " + e.getMessage(), e);
        }
        return false;
    }

    private boolean checkUserExistence(String username) {
        try {
            return doCheckExistingUser(username);
        } catch (Exception e) {
            log.debug("Error while checking user existence: " + e.getMessage(), e);
            return false;
        }
    }

    private void handleAdminUserCreation(String adminUsername, boolean addAdmin) throws UserStoreException {
        if (isReadOnly() || !addAdmin) {
            return;
        }

        try {
            doAddUser(adminUsername, realmConfig.getAdminPassword(), null, null, null, false);
        } catch (Exception e) {
            log.error("Admin user has not been created. Error occurred while creating admin user.", e);
        }
    }

    private void handleAdminRoleCreation(String adminRoleName, String adminUsername, boolean addAdmin) throws UserStoreException {
        if (!addAdmin) {
            log.error("Admin role cannot be created. 'Add-Admin' is set to false. Please use an existing role as the admin role.");
            return;
        }
        if (isReadOnly() || !writeGroupsEnabled) {
            return;
        }
        try {
            doAddRole(adminRoleName, new String[]{adminUsername});
        } catch (UserStoreException e) {
            log.error("Admin role has not been created. Error occurred while creating admin role.", e);
        }
    }

    private void assignUserToRole(String adminUsername, String adminRoleName, boolean addAdmin) throws UserStoreException {
        if (isReadOnly() || !writeGroupsEnabled) {
            return;
        }
        try {
            if (!doCheckIsUserInRole(adminUsername, adminRoleName)) {
                if (addAdmin) {
                    doUpdateRoleListOfUser(adminUsername, null, new String[]{adminRoleName});
                } else {
                    log.error("Admin user cannot be assigned to admin role. Add-Admin is set to false. Please assign the role manually.");
                }
            }
        } catch (Exception e) {
            log.error("Error while assigning admin user to admin role.", e);
        }
    }
}
