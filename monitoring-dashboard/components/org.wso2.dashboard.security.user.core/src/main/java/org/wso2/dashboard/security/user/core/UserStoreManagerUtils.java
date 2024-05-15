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

package org.wso2.dashboard.security.user.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.security.user.core.common.DashboardUserStoreException;
import org.wso2.dashboard.security.user.core.common.DataHolder;
import org.wso2.dashboard.security.user.core.file.FileBasedUserStoreManager;
import org.wso2.dashboard.security.user.core.jdbc.JDBCUserStoreManager;
import org.wso2.dashboard.security.user.core.ldap.ReadOnlyLDAPUserStoreManager;
import org.wso2.micro.integrator.security.MicroIntegratorSecurityUtils;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.api.UserStoreException;
import org.wso2.micro.integrator.security.user.core.constants.UserCoreErrorConstants;

import java.util.*;

public class UserStoreManagerUtils {
    private static Log log = LogFactory.getLog(UserStoreManagerUtils.class);
    private static final String MULIPLE_ATTRIBUTE_ENABLE = "MultipleAttributeEnable";

    public static boolean validateUserNameAndCredential(String userName, String credential) {
        boolean isValid = true;
        if (userName == null || credential == null) {
            String message =
                    String.format(UserCoreErrorConstants.ErrorMessages.
                            ERROR_CODE_ERROR_WHILE_PRE_AUTHENTICATION.getMessage(),
                            "Authentication failure. Either Username or Password is null");
            log.error(message);
            isValid = false;
        }

        return isValid;
    }

    public static UserStoreManager getUserStoreManager() throws UserStoreException, DashboardUserStoreException {
        DataHolder dataHolder = DataHolder.getInstance();
        if (dataHolder.getUserStoreManager() == null) {
            initializeUserStore();
        }
        return dataHolder.getUserStoreManager();
    }

    public static void initializeUserStore() throws UserStoreException, DashboardUserStoreException {
        DataHolder dataHolder = DataHolder.getInstance();
        if (isFileBasedUserStoreEnabled()) {
            dataHolder.setUserStoreManager(FileBasedUserStoreManager.getUserStoreManager());
        } else {
            RealmConfiguration config = RealmConfigXMLProcessor.createRealmConfig();
            if (config == null) {
                throw new UserStoreException("Unable to create Realm Configuration");
            }
            dataHolder.setRealmConfig(config);

            UserStoreManager userStoreManager;
            String userStoreMgtClassStr = config.getUserStoreClass();
            switch (userStoreMgtClassStr) {
                case UserStoreConstants.DEFAULT_LDAP_USERSTORE_MANAGER:
                    userStoreManager = new ReadOnlyLDAPUserStoreManager(config, null, null);
                    break;
                case UserStoreConstants.DEFAULT_JDBC_USERSTORE_MANAGER:
                    userStoreManager = new JDBCUserStoreManager(config, new Hashtable<>(), null, null, null,
                            UserStoreConstants.SUPER_TENANT_ID, false);
                    break;
                default:
                    userStoreManager = (UserStoreManager) MicroIntegratorSecurityUtils.
                            createObjectWithOptions(userStoreMgtClassStr, config);
                    break;
            }
            dataHolder.setUserStoreManager(userStoreManager);
        }
    }

    public static boolean isFileBasedUserStoreEnabled() {
        return Boolean.parseBoolean(System.getProperty("is.user.store.file.based"));
    }

    public static String addDomainToName(String name, String domainName) {

        if (!name.contains(UserStoreConstants.DOMAIN_SEPARATOR) &&
                !UserStoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME.equalsIgnoreCase(domainName)) {
            // domain name is not already appended, and if exist in user-mgt.xml, append it..
            if (domainName != null) {
                // append domain name if exist
                domainName = domainName.toUpperCase() + UserStoreConstants.DOMAIN_SEPARATOR;
                name = domainName + name;
            }
        }
        return name;
    }

    /**
     * Domain name is not already appended, and if it is provided or if exist in user-mgt.xml,
     * append it
     *
     * @param names
     * @param domainName
     * @return
     */
    public static String[] addDomainToNames(String[] names, String domainName) {

        if (domainName != null) {
            domainName = domainName.toUpperCase();
        }

        List<String> namesList = new ArrayList<String>();
        if (names != null && names.length != 0) {
            for (String name : names) {
                if ((name.indexOf(UserStoreConstants.DOMAIN_SEPARATOR)) < 0 &&
                        !UserStoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME.equalsIgnoreCase(domainName)) {
                    if (domainName != null) {
                        name = UserStoreManagerUtils.addDomainToName(name, domainName);
                        namesList.add(name);
                        continue;
                    }
                }
                namesList.add(name);
            }
        }
        if (namesList.size() != 0) {
            return namesList.toArray(new String[namesList.size()]);
        } else {
            return names;
        }
    }

    public static boolean isAdmin(String user) throws UserStoreException, DashboardUserStoreException {
        if (isFileBasedUserStoreEnabled()) {
            return FileBasedUserStoreManager.getUserStoreManager().isAdmin(user);
        }
        String[] roles = getUserStoreManager().getRoleListOfUser(user);
        return containsAdminRole(roles);
    }

    /**
     * Method to assert if the admin role is contained within a list of roles
     *
     * @param rolesList the list of roles assigned to a user
     * @return true if the admin role is present in the list of roles provided
     * @throws UserStoreException if any error occurs while reading the realm configuration
     */
    public static boolean containsAdminRole(String[] rolesList) {
        return Arrays.asList(rolesList).contains(DataHolder.getInstance().getRealmConfig().getAdminRoleName());
    }

    /**
     * @param realmConfig
     * @return
     */
    public static String getDomainName(RealmConfiguration realmConfig) {
        String domainName = realmConfig.getUserStoreProperty(UserStoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);
        if(domainName != null) {
            domainName = domainName.toUpperCase();
        }
        return domainName;
    }
}
