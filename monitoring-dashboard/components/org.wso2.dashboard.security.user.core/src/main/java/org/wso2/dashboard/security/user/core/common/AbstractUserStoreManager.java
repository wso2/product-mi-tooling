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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.security.user.core.UserStoreConstants;
import org.wso2.dashboard.security.user.core.UserStoreManager;
import org.wso2.dashboard.security.user.core.UserStoreManagerUtils;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.core.UserCoreConstants;
import org.wso2.micro.integrator.security.user.core.UserRealm;
import org.wso2.micro.integrator.security.user.core.UserStoreException;
import org.wso2.micro.integrator.security.user.core.claim.ClaimManager;
import org.wso2.micro.integrator.security.user.core.common.UserRolesCache;
import org.wso2.micro.integrator.security.user.core.hybrid.HybridRoleManager;
import org.wso2.micro.integrator.security.user.core.listener.UserStoreManagerConfigurationListener;
import org.wso2.micro.integrator.security.user.core.system.SystemUserRoleManager;
import org.wso2.micro.integrator.security.user.core.util.UserCoreUtil;

import javax.sql.DataSource;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;

public abstract class AbstractUserStoreManager implements UserStoreManager {
    private static final Log log = LogFactory.getLog(AbstractUserStoreManager.class);
    protected static final String TRUE_VALUE = "true";
    protected static final String FALSE_VALUE = "false";
    private static final String MAX_LIST_LENGTH = "100";
    private static final int MAX_ITEM_LIMIT_UNLIMITED = -1;
    private static final String MULIPLE_ATTRIBUTE_ENABLE = "MultipleAttributeEnable";
    private static final String DISAPLAY_NAME_CLAIM = "http://wso2.org/claims/displayName";
    private static final String SCIM_USERNAME_CLAIM_URI = "urn:scim:schemas:core:1.0:userName";
    private static final String SCIM2_USERNAME_CLAIM_URI = "urn:ietf:params:scim:schemas:core:2.0:User:userName";
    private static final String USERNAME_CLAIM_URI = "http://wso2.org/claims/username";
    private static final String APPLICATION_DOMAIN = "Application";
    private static final String WORKFLOW_DOMAIN = "Workflow";
    private static final String INVALID_CLAIM_URL = "InvalidClaimUrl";
    private static final String INVALID_USER_NAME = "InvalidUserName";
    private static final String READ_ONLY_STORE = "ReadOnlyUserStoreManager";
    private static final String READ_ONLY_PRIMARY_STORE = "ReadOnlyPrimaryUserStoreManager";
    private static final String ADMIN_USER = "AdminUser";
    private static final String PROPERTY_PASSWORD_ERROR_MSG = "PasswordJavaRegExViolationErrorMsg";
    private static final String MULTI_ATTRIBUTE_SEPARATOR = "MultiAttributeSeparator";
    protected int tenantId;
    protected DataSource dataSource = null;
    protected RealmConfiguration realmConfig = null;
    protected ClaimManager claimManager = null;
    protected UserRealm userRealm = null;
    protected HybridRoleManager hybridRoleManager = null;
    // User roles cache
    protected UserRolesCache userRolesCache = null;
    protected SystemUserRoleManager systemUserRoleManager = null;
    protected boolean readGroupsEnabled = false;
    protected boolean writeGroupsEnabled = false;
    private org.wso2.micro.integrator.security.user.core.UserStoreManager secondaryUserStoreManager;
    private boolean userRolesCacheEnabled = true;
    private String cacheIdentifier;
    private boolean replaceEscapeCharactersAtUserLogin = true;
    private Map<String, org.wso2.micro.integrator.security.user.core.UserStoreManager> userStoreManagerHolder = new HashMap<String, org.wso2.micro.integrator.security.user.core.UserStoreManager>();
    private Map<String, Integer> maxUserListCount = null;
    private Map<String, Integer> maxRoleListCount = null;
    private List<UserStoreManagerConfigurationListener> listener = new ArrayList<UserStoreManagerConfigurationListener>();
    private static final ThreadLocal<Boolean> isSecureCall = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    public boolean authenticate(String username, String credential) throws UserStoreException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws Exception {
                    if (!validateUserNameAndCredential(username, credential)) {
                        return  false;
                    }
                    int index = username.indexOf(UserStoreConstants.DOMAIN_SEPARATOR);
                    boolean domainProvided = index > 0;
                    return authenticateInternal(username, credential, domainProvided);
                }
            });
        } catch (PrivilegedActionException e) {
            if (!(e.getException() instanceof UserStoreException)) {
                log.error("Error occurred while authenticating user", e);
            }
            throw (UserStoreException) e.getException();
        }
    }

    /**
     * @param userName
     * @param credential
     * @param domainProvided
     * @return
     * @throws UserStoreException
     */
    private boolean authenticateInternal(String userName, String credential, boolean domainProvided)
            throws UserStoreException {

        AbstractUserStoreManager abstractUserStoreManager = this;

        boolean authenticated = false;

        UserStore userStore = abstractUserStoreManager.getUserStore(userName);
        int tenantId = abstractUserStoreManager.getTenantId();
        try {
            // Let's authenticate with the primary UserStoreManager.
            authenticated = abstractUserStoreManager.doAuthenticate(userName, credential);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Error occurred while authenticating user: " + userName, e);
            } else {
                log.error(e);
            }
            authenticated = false;
        }
        return authenticated;
    }

    /**
     * To validate username and credential that is given for authentication.
     *
     * @param userName   Name of the user.
     * @param credential Credential of the user.
     * @return false if the validation fails.
     * @throws UserStoreException UserStore Exception.
     */
    private boolean validateUserNameAndCredential(String userName, String credential) throws UserStoreException {

        boolean isValid = true;
        if (userName == null || credential == null) {
            String message = "Authentication failure. Either Username or Password is null";
            log.error(message);
            isValid = false;
        }
        return isValid;
    }

    @Override
    public String[] getRoleListOfUser(String userName) throws UserStoreException {
        String[] roleNames = null;

//        // Check whether roles exist in cache
//        roleNames = getRoleListOfUserFromCache(this.tenantId, usernameWithDomain);
//        if (roleNames != null && roleNames.length > 0) {
//            return roleNames;
//        }
        UserStore userStore = getUserStore(userName);
        if (userStore.isRecurssive()) {
            return userStore.getUserStoreManager().getRoleListOfUser(userStore.getDomainFreeName());
        }

        if (userStore.isSystemStore()) {
            return systemUserRoleManager.getSystemRoleListOfUser(userStore.getDomainFreeName());
        }

        // #################### Domain Name Free Zone Starts Here ################################

        roleNames = doGetRoleListOfUser(userName, "*");

        return roleNames;
    }

    /**
     * @param userName
     * @param filter
     * @return
     * @throws UserStoreException
     */
    public final String[] doGetRoleListOfUser(String userName, String filter)
            throws UserStoreException {

        String[] modifiedExternalRoleList = new String[0];

        if (readGroupsEnabled) {
            List<String> roles = new ArrayList<String>();
            String[] externalRoles = doGetExternalRoleListOfUser(userName, "*");
            roles.addAll(Arrays.asList(externalRoles));
            if (isSharedGroupEnabled()) {
                String[] sharedRoles = doGetSharedRoleListOfUser(userName, null, "*");
                if (sharedRoles != null) {
                    roles.addAll(Arrays.asList(sharedRoles));
                }
            }
            modifiedExternalRoleList =
                    UserStoreManagerUtils.addDomainToNames(roles.toArray(new String[roles.size()]),
                            getMyDomainName());
        }
        return modifiedExternalRoleList;
    }

    /**
     *
     * @return
     */
    public boolean isSharedGroupEnabled() {
        String value = realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.SHARED_GROUPS_ENABLED);
        try {
            return realmConfig.isPrimary() && !isReadOnly() && TRUE_VALUE.equalsIgnoreCase(value);
        } catch (UserStoreException e) {
            log.error(e);
        }
        return false;
    }

    private UserStore getUserStore(final String user) throws UserStoreException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<UserStore>() {
                @Override
                public UserStore run() throws Exception {
                    return getUserStoreInternal(user);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (UserStoreException) e.getException();
        }
    }

    /**
     * @return
     * @throws UserStoreException
     */
    private UserStore getUserStoreInternal(String user) throws UserStoreException {

        int index;
        index = user.indexOf(UserStoreConstants.DOMAIN_SEPARATOR);
        UserStore userStore = new UserStore();
        String domainFreeName = null;
        String domain = getMyDomainName();
        userStore.setUserStoreManager(this);
        if (index > 0) {
            userStore.setDomainAwareName(user);
            userStore.setDomainFreeName(domainFreeName);
        } else {
            userStore.setDomainAwareName(domain + UserStoreConstants.DOMAIN_SEPARATOR + user);
            userStore.setDomainFreeName(user);
        }
        userStore.setRecurssive(false);
        userStore.setDomainName(domain);

        return userStore;
    }

    /**
     * @return
     */
    protected String getMyDomainName() {
        return UserStoreManagerUtils.getDomainName(realmConfig);
    }

    /**
     * Only gets the external roles of the user.
     *
     * @param userName Name of the user - who we need to find roles.
     * @return
     * @throws UserStoreException
     */
    protected abstract String[] doGetExternalRoleListOfUser(String userName, String filter)
            throws UserStoreException;

    /**
     * Returns the shared roles list of the user
     *
     * @param userName
     * @return
     * @throws UserStoreException
     */
    protected abstract String[] doGetSharedRoleListOfUser(String userName,
                                                          String tenantDomain, String filter) throws UserStoreException;

    /**
     * Given the user name and a credential object, the implementation code must validate whether
     * the user is authenticated.
     *
     * @param userName   The user name
     * @param credential The credential of a user
     * @return If the value is true the provided credential match with the user name. False is
     * returned for invalid credential, invalid user name and mismatching credential with
     * user name.
     * @throws UserStoreException An unexpected exception has occurred
     */
    protected abstract boolean doAuthenticate(String userName, String credential)
            throws UserStoreException;

}
