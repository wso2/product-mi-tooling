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

import com.google.gson.JsonElement;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.auth.TokenCache;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.commons.utils.TokenUtils;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.groups.GroupDelegate;
import org.wso2.ei.dashboard.core.rest.delegates.nodes.NodesDelegate;
import org.wso2.ei.dashboard.core.rest.model.GroupList;
import org.wso2.ei.dashboard.core.rest.model.NodeList;

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
            String accessToken = getTokenFromMI(username, password);
            if (StringUtils.isEmpty(accessToken)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                       Constants.NO_SERVER_FOUND_ERROR).build();
            } else {
                storeTokenInCache(accessToken);
                return Response.ok(getUserInfo(username, accessToken))
                               .header(Constants.COOKIE_HEADER,
                                       getTokenCookieHeader(accessToken, NewCookie.DEFAULT_MAX_AGE)).build();
            }
        } catch (ManagementApiException e) {
            logger.error("Error logging into dashboard server due to {} ", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Error logging into dashboard server", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getTokenFromMI(String username, String password) throws ManagementApiException {
        GroupDelegate groupDelegate = new GroupDelegate();
        GroupList groupList = groupDelegate.getGroupList();
        if (groupList.isEmpty()) {
            logger.error(Constants.NO_SERVER_FOUND_ERROR);
            return "";
        } else {
            NodesDelegate nodesDelegate = new NodesDelegate();
            NodeList nodes = nodesDelegate.getNodes(groupList.get(0));
            return ManagementApiUtils.getToken(ManagementApiUtils.getMgtApiUrl(
                    groupList.get(0), nodes.get(0).getNodeId()), username, password);
        }
    }

    private void storeTokenInCache(String accessToken) {
        TokenCache.getInstance().putToken(accessToken, accessToken);
    }

    /**
     * This method returns a JSON object which contains user information.
     *
     * @param username    Username of the logged in user.
     * @param accessToken Access token received upon successfully login.
     * @return JSONObject JSONObject containing user information.
     */
    private JSONObject getUserInfo(String username, String accessToken) {

        JsonElement jsonElementPayload = TokenUtils.getParsedToken(accessToken);
        JsonElement scopeElement = jsonElementPayload.getAsJsonObject().get(Constants.SCOPE);
        String scope = "default";
        if (scopeElement != null) {
            scope = scopeElement.getAsString();
        }
        JSONObject userInfoJSON = new JSONObject();
        userInfoJSON.put("username", username);
        userInfoJSON.put("scope", scope);
        userInfoJSON.put("sso", false);
        return userInfoJSON;
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
