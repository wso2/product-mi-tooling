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

import org.wso2.dashboard.security.user.core.common.DashboardUserStoreException;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.core.UserStoreException;

public interface UserStoreManager {

    /**
     * Authenticates a user with the given credentials.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return true if authentication is successful, false otherwise.
     */
    boolean authenticate(String username, Object password) throws DashboardUserStoreException;

    /**
     * Get roles of a user.
     *
     * @param userName The user name
     * @return An array of role names that user belongs.
     * @throws UserStoreException
     */
    String[] getRoleListOfUser(String userName) throws DashboardUserStoreException;

    /**
     * @return
     * @throws UserStoreException
     */
    boolean isReadOnly() throws DashboardUserStoreException;

    /**
     * this will get the tenant id associated with the user store manager
     *
     * @return the tenant id of the authorization manager
     * @throws UserStoreException if the operation failed
     */
    int getTenantId() throws DashboardUserStoreException;

    /**
     * Get the RealmConfiguration belonging to this user store
     *
     * @return RealmConfiguration
     */
    RealmConfiguration getRealmConfiguration();
}