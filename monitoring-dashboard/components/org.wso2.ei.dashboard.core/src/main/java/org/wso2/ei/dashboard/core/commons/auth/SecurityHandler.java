/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.ei.dashboard.core.commons.auth;

import org.wso2.micro.integrator.dashboard.utils.SSOConfig;

/**
 * This class provides an interface for all security handlers.
 */
public interface SecurityHandler {

    /**
     * Executes the authentication logic relevant to the handler.
     *
     * @param ssoConfig SSOConfig
     * @param token     authorization token
     * @return Boolean authenticated
     */
    boolean isAuthenticated(SSOConfig ssoConfig, String token);

    /**
     * Executes the authorization logic relevant to the handler.
     *
     * @param ssoConfig SSOConfig
     * @param token     authorization token
     * @return Boolean authenticated
     */
    boolean isAuthorized(SSOConfig ssoConfig, String token);

}
