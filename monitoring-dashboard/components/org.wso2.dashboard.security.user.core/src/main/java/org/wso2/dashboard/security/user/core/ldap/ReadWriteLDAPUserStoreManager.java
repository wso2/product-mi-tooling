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

package org.wso2.dashboard.security.user.core.ldap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.security.user.core.common.DashboardUserStoreException;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.core.UserCoreConstants;
import org.wso2.micro.integrator.security.user.core.UserStoreException;
import org.wso2.micro.integrator.security.user.core.claim.ClaimManager;
import org.wso2.micro.integrator.security.user.core.ldap.LDAPConstants;
import org.wso2.micro.integrator.security.user.core.profile.ProfileConfigurationManager;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class ReadWriteLDAPUserStoreManager extends ReadOnlyLDAPUserStoreManager {
    private static final Log log = LogFactory.getLog(ReadWriteLDAPUserStoreManager.class);

    public ReadWriteLDAPUserStoreManager() {

    }

    /**
     * This constructor is not used. So not applying the changes done to above constructor.
     *
     * @param realmConfig
     * @param claimManager
     * @param profileManager
     * @throws UserStoreException
     */
    public ReadWriteLDAPUserStoreManager(RealmConfiguration realmConfig, ClaimManager claimManager,
                                          ProfileConfigurationManager profileManager) throws UserStoreException {
        super(realmConfig, claimManager, profileManager);
    }

    /**
     * This is to read and validate the required user store configuration for this user store
     * manager to take decisions.
     *
     * @throws UserStoreException
     */
    @Override
    protected void checkRequiredUserStoreConfigurations() throws UserStoreException {

        super.checkRequiredUserStoreConfigurations();

        String userObjectClass = realmConfig
                .getUserStoreProperty(LDAPConstants.USER_ENTRY_OBJECT_CLASS);
        if (userObjectClass == null || userObjectClass.isEmpty()) {
            throw new UserStoreException(
                    "Required UserEntryObjectClass property is not set at the LDAP configurations");
        }

        if (realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.WRITE_GROUPS_ENABLED) != null) {
            writeGroupsEnabled =
                    Boolean.parseBoolean(realmConfig
                            .getUserStoreProperty(UserCoreConstants.RealmConfig.WRITE_GROUPS_ENABLED));
        }

        if (log.isDebugEnabled()) {
            if (writeGroupsEnabled) {
                log.debug("WriteGroups is enabled");
            } else {
                log.debug("WriteGroups is disabled");
            }
        }

        if (!writeGroupsEnabled) {
            if (realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.READ_GROUPS_ENABLED) != null) {
                readGroupsEnabled =
                        Boolean.parseBoolean(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.READ_GROUPS_ENABLED));
                log.debug("Read LDAP groups enabled: " + readGroupsEnabled);
            }
        } else {
            // Write overwrites Read
            readGroupsEnabled = true;
            log.debug("Read LDAP groups enabled: true");
        }

        emptyRolesAllowed =
                Boolean.parseBoolean(realmConfig.getUserStoreProperty(LDAPConstants.EMPTY_ROLES_ALLOWED));

        String groupEntryObjectClass = realmConfig
                .getUserStoreProperty(LDAPConstants.GROUP_ENTRY_OBJECT_CLASS);
        if (groupEntryObjectClass == null || groupEntryObjectClass.isEmpty()) {
            throw new UserStoreException(
                    "Required GroupEntryObjectClass property is not set at the LDAP configurations");
        }

        userSearchBase = realmConfig.getUserStoreProperty(LDAPConstants.USER_SEARCH_BASE);
        groupSearchBase = realmConfig.getUserStoreProperty(LDAPConstants.GROUP_SEARCH_BASE);

    }

    protected boolean authenticate(final String userName, final Object credential, final boolean domainProvided)
            throws DashboardUserStoreException {

        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>) () -> authenticateInternalIteration(userName, credential, domainProvided));
        } catch (PrivilegedActionException e) {
            throw new DashboardUserStoreException("Error while authenticating user: " + e.getMessage(), e);
        }

    }

    private boolean authenticateInternalIteration(String userName, Object credential, boolean domainProvided) {
        return authenticateInternal(userName, credential, domainProvided);
    }

    /**
     * @param userName
     * @param credential
     * @param domainProvided
     * @return
     */
    private boolean authenticateInternal(String userName, Object credential, boolean domainProvided) {
        ReadWriteLDAPUserStoreManager readWriteLDAPUserStoreManager = this;
        boolean authenticated = false;

            try {
                // Let's authenticate with the primary UserStoreManager.
                authenticated = readWriteLDAPUserStoreManager.doAuthenticate(userName, credential);
            } catch (Exception e) {
                log.error("Error occurred while authenticating user: " + userName, e);

                if (log.isDebugEnabled()) {
                    log.debug("Error occurred while authenticating user: " + userName, e);
                } else {
                    log.error(e);
                }
            }

        if (log.isDebugEnabled()) {
            if (!authenticated) {
                log.debug("Authentication failure. Wrong username or password is provided.");
            }
        }

        return authenticated;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
