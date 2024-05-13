/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.ei.dashboard.core.rest.delegates.auth;

import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.dashboard.security.user.core.UserStoreManagerUtils;
import org.wso2.dashboard.security.user.core.common.Secret;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.auth.TokenCache;
import org.wso2.ei.dashboard.core.commons.auth.TokenGenerator;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static org.wso2.ei.dashboard.core.commons.Constants.JWT_COOKIE;

/**
 * Manages login received to the dashboard.
 */
public class LoginDelegate {

    private static final Logger logger = LogManager.getLogger(LoginDelegate.class);

    public Response authenticateUser(String username, String password) {
        try {
            boolean isAuthenticated = UserStoreManagerUtils.getUserStoreManager().authenticate(username, password);
            if (isAuthenticated) {
                logger.info(String.format("User %s logged in successfully", username));
                String scope = UserStoreManagerUtils.isAdmin(username) ? "admin" : "default";
                String accessToken = TokenGenerator.generateToken(username, scope);
                storeTokenInCache(accessToken);
                return Response.ok(getUserInfo(username, scope)).header(Constants.COOKIE_HEADER,
                                       getTokenCookieHeader(accessToken, NewCookie.DEFAULT_MAX_AGE)).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                       Constants.LOGIN_ERROR).build();
            }
        } catch (Exception e) {
            logger.error("Error logging into dashboard server", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private JSONObject getUserInfo(String username, String scope) {
        JSONObject userInfoJSON = new JSONObject();
        userInfoJSON.put("username", username);
        userInfoJSON.put("scope", scope);
        userInfoJSON.put("sso", false);
        userInfoJSON.put("isFileBasedUserStoreEnabled", UserStoreManagerUtils.isFileBasedUserStoreEnabled());
        return userInfoJSON;
    }

    private void storeTokenInCache(String accessToken) {
        TokenCache.getInstance().putToken(accessToken, accessToken);
    }

    /**
     * This method returns a HTTP Cookie which contains the access token.
     *
     * @param accessToken Access token received upon successfully login.
     * @param age         Age of the cookie.
     * @return String String value representing the cookies.
     */
    public static String getTokenCookieHeader(String accessToken, int age) {

        NewCookie jwtCookie =
                new NewCookie(JWT_COOKIE, accessToken, "/", "", "", age, true, true);
        return jwtCookie + ";SameSite=Strict";
    }
}
