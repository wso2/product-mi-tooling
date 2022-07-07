/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *
 */

package org.wso2.ei.dashboard.core.rest.delegates.nodes;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.rest.model.ManageServerRuntimeRequest;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.NodeListInner;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

/**
 * Delegate class to handle requests from Nodes page (Home page).
 */
public class NodesDelegate {
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();
    private static final Logger logger = LogManager.getLogger(NodesDelegate.class);

    public NodeList getNodes(String groupId) {
        logger.debug("Fetching node list in " + groupId + " from database.");
        NodeList nodeList = databaseManager.fetchNodes(groupId);
        for (NodeListInner nodeListInner : nodeList) {
            String nodeId = nodeListInner.getNodeId();
            long heartbeatInterval = Long.parseLong(databaseManager.getHeartbeatInterval(groupId, nodeId));
            long lastTimestamp = Long.parseLong(databaseManager.retrieveTimestampOfLastHeartbeat(groupId, nodeId));
            long currentTimestamp = System.currentTimeMillis();
            // check if the node is unhealthy. If the server does not receive a heartbeat by
            // at least 1.5 * heartbeat_interval, the node will be denoted as unhealthy.
            if ((currentTimestamp - lastTimestamp) > heartbeatInterval * 1500) {
                nodeListInner.setStatus("unhealthy");
            } else {
                nodeListInner.setStatus("healthy");
            }
        }
        return nodeList;
    }

    public JsonObject manageNode(String groupId, String nodeId, ManageServerRuntimeRequest request) throws ManagementApiException {
        logger.debug("Managing the server runtime from management console");
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("server");

        String accessToken = databaseManager.getAccessToken(groupId, nodeId);
        JsonObject payload = createManageNodePayload(request);
        CloseableHttpResponse httpResponse = Utils.doPatch(groupId, nodeId, accessToken, url, payload);
        JsonObject jsonResponse = HttpUtils.getJsonResponse(httpResponse);
        return jsonResponse;
    }
    
    private JsonObject createManageNodePayload(ManageServerRuntimeRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("status", request.getStatus());
        return payload;
    }
}
