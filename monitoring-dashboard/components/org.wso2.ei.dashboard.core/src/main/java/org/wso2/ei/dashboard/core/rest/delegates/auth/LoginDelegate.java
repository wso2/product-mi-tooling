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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.auth.TokenCache;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.groups.GroupDelegate;
import org.wso2.ei.dashboard.core.rest.delegates.nodes.NodesDelegate;
import org.wso2.ei.dashboard.core.rest.model.GroupList;
import org.wso2.ei.dashboard.core.rest.model.NodeList;

import javax.ws.rs.core.Response;

/**
 * Manages login received to the dashboard.
 */
public class LoginDelegate {

    private static final Logger logger = LogManager.getLogger(LoginDelegate.class);

    public Response authenticateUser(String username, String password) {
        try {
            String accessToken = getTokenFromMI(username, password);
            storeTokenInCache(accessToken);
            return Response.ok(accessToken).build();
        } catch (ManagementApiException e) {
            logger.error("Error logging into dashboard server due to {} ", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Error logging into dashboard server", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getTokenFromMI(String username, String password) throws ManagementApiException {
        logger.info("Request token from MI...");
        GroupDelegate groupDelegate = new GroupDelegate();
        GroupList groupList = groupDelegate.getGroupList();
        if (groupList.isEmpty()) {
            logger.error("No running micro integrator instances found. Please start a server and login.");
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
}
