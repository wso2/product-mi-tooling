/*
 * Copyright (c) 2024, WSO2 LLC. (http://wso2.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.ei.dashboard.micro.integrator;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.ArtifactsManager;
import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.HeartbeatObject;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;
import org.wso2.micro.integrator.dashboard.utils.ExecutorServiceHolder;

import java.io.IOException;

/**
 * Fetch, store, update and delete artifact information of registered Ballerina nodes.
 */
public class BalArtifactsManager implements ArtifactsManager {

    private static final Logger logger = LogManager.getLogger(BalArtifactsManager.class);
    private final DataManager dataManager = DataManagerSingleton.getDataManager();
    private HeartbeatObject heartbeat = null;

    public BalArtifactsManager(HeartbeatObject heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Override
    public void runFetchAllExecutorService() {
        Runnable runnable = () -> {
            String nodeId = heartbeat.getNodeId();
            String groupId = heartbeat.getGroupId();
            logger.info("Fetching artifacts from node " + nodeId + " in group " + groupId);
            String accessToken = dataManager.getAccessToken(groupId, nodeId);
            try {
                fetchAndStoreServers(accessToken);
            } catch (ManagementApiException e) {
                logger.error("Unable to fetch artifacts/details from node: {} of group: {} due to {} ", nodeId,
                        groupId, e.getMessage(), e);
            }
        };
        ExecutorServiceHolder.getBalArtifactsManagerExecutorService().execute(runnable);
    }

    @Override
    public void runDeleteAllExecutorService() {
        Runnable runnable = this::deleteAllArtifacts;
        ExecutorServiceHolder.getBalArtifactsManagerExecutorService().execute(runnable);
    }

    @Override
    public String getAccessToken(String mgtApiUrl) throws ManagementApiException {
        return ManagementApiUtils.getToken(mgtApiUrl, System.getProperty("bal_username"),
                System.getProperty("bal_password"));
    }

    private void fetchAndStoreServers(String accessToken) throws ManagementApiException {
        String url = heartbeat.getMgtApiUrl();
        try (CloseableHttpResponse response = Utils.doGet(heartbeat.getGroupId(), heartbeat.getNodeId(),
                accessToken, url)) {
            String stringResponse = HttpUtils.getStringResponse(response);
            storeServerInfo(stringResponse, heartbeat);
        } catch (IOException e) {
            logger.error("Unable to fetch server information from node: {} of group: {} due to {} "
                    , heartbeat.getNodeId(),
                    heartbeat.getGroupId(), e.getMessage(), e);
        }
    }

    private void storeServerInfo(String stringResponse, HeartbeatObject heartbeat) {
        boolean isSuccess = dataManager.insertServerInformation(heartbeat, stringResponse);
        if (!isSuccess) {
            logger.error("Error occurred while adding server details of node: " + heartbeat.getNodeId() + " in group "
                    + heartbeat.getGroupId());
        }
    }

    private void deleteAllArtifacts() {
        String groupId = heartbeat.getGroupId();
        String nodeId = heartbeat.getNodeId();
        dataManager.deleteServerInformation(groupId, nodeId);
    }
}
