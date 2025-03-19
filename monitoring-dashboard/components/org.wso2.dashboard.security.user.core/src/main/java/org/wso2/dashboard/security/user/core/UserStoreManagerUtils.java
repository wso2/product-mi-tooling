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

import org.wso2.dashboard.security.user.core.common.DataHolder;
import org.wso2.dashboard.security.user.core.file.FileBasedUserStoreManager;
import org.wso2.dashboard.security.user.core.jdbc.JDBCUserStoreManager;
import org.wso2.dashboard.security.user.core.ldap.ReadOnlyLDAPUserStoreManager;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.api.UserStoreException;
import org.wso2.micro.integrator.security.user.api.UserStoreManager;

import java.util.Arrays;
import java.util.Hashtable;

import static org.wso2.dashboard.security.user.core.UserStoreConstants.DEFAULT_JDBC_USERSTORE_MANAGER;
import static org.wso2.dashboard.security.user.core.UserStoreConstants.DEFAULT_LDAP_USERSTORE_MANAGER;
import static org.wso2.dashboard.security.user.core.UserStoreConstants.SUPER_TENANT_ID;
import static org.wso2.micro.integrator.security.MicroIntegratorSecurityUtils.createObjectWithOptions;

public class UserStoreManagerUtils {
    private static final String FILE_BASED_USER_STORE_PROPERTY = "is.user.store.file.based";

    public static boolean isAdminUser(String username) throws UserStoreException {
        if (isFileBasedUserStoreEnabled()) {
            return FileBasedUserStoreManager.getUserStoreManager().isAdmin(username);
        }
        String[] roles = getUserStoreManager().getRoleListOfUser(username);
        return hasAdminRole(roles);
    }

    public static boolean isFileBasedUserStoreEnabled() {
        return Boolean.parseBoolean(System.getProperty(FILE_BASED_USER_STORE_PROPERTY));
    }

    public static UserStoreManager getUserStoreManager() throws UserStoreException {
        DataHolder dataHolder = DataHolder.getInstance();
        if (dataHolder.getUserStoreManager() == null) {
            initializeUserStore();
        }
        return dataHolder.getUserStoreManager();
    }

    private static void initializeUserStore() throws UserStoreException {
        DataHolder dataHolder = DataHolder.getInstance();
        if (isFileBasedUserStoreEnabled()) {
            dataHolder.setUserStoreManager(FileBasedUserStoreManager.getUserStoreManager());
            return;
        }
        RealmConfiguration config = RealmConfigXMLProcessor.createRealmConfig();
        if (config == null) {
            throw new UserStoreException("Unable to create Realm Configuration");
        }
        dataHolder.setRealmConfig(config);

        String userStoreMgtClassName = config.getUserStoreClass();
        UserStoreManager userStoreManager = createUserStoreManager(userStoreMgtClassName, config);
        dataHolder.setUserStoreManager(userStoreManager);
    }

    private static UserStoreManager createUserStoreManager(String userStoreManagerName, RealmConfiguration config)
            throws UserStoreException {
        switch (userStoreManagerName) {
            case DEFAULT_LDAP_USERSTORE_MANAGER: {
                return new ReadOnlyLDAPUserStoreManager(config, null, null);
            }
            case DEFAULT_JDBC_USERSTORE_MANAGER: {
                return new JDBCUserStoreManager(config, new Hashtable<>(), SUPER_TENANT_ID);
            }
            default: {
                return (UserStoreManager) createObjectWithOptions(userStoreManagerName, config);
            }
        }
    }

    private static boolean hasAdminRole(String[] roles) {
        return Arrays.stream(roles).anyMatch(UserStoreManagerUtils::isAdminRole);
    }

    public static boolean isAdminRole(String roleName) {
        RealmConfiguration realmConfig = DataHolder.getInstance().getRealmConfig();
        String adminRoleName = realmConfig.getAdminRoleName();
        return roleName.equalsIgnoreCase(adminRoleName);
    }
}
