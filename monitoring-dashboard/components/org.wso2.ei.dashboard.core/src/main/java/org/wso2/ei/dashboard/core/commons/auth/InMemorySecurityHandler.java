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

import com.google.gson.JsonElement;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.TokenUtils;
import org.wso2.micro.integrator.dashboard.utils.SSOConfig;

/**
 * This class implements SecurityHandler to implement the authentication logic for a In memory user store for
 * dashboard api.
 */
public class InMemorySecurityHandler implements SecurityHandler {

    @Override
    public boolean isAuthenticated(SSOConfig ssoConfig, String token) {

        return TokenCache.getInstance().getToken(token) != null;
    }

    @Override
    public boolean isAuthorized(SSOConfig ssoConfig, String token) {

        JsonElement jsonElementPayload = TokenUtils.getParsedToken(token);
        JsonElement scopeElement = jsonElementPayload.getAsJsonObject().get(Constants.SCOPE);
        if (scopeElement != null) {
            String scope = scopeElement.getAsString();
            return scope.equals(Constants.ADMIN);
        }
        return false;
    }
}
