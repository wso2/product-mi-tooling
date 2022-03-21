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

package org.wso2.ei.dashboard.core.rest.delegates.heartbeat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.ArtifactsManager;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.HeartbeatRequest;
import org.wso2.ei.dashboard.micro.integrator.MiArtifactsManager;
import org.wso2.ei.dashboard.streaming.integrator.SiArtifactsFetcher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages heartbeats received to the dashboard.
 */
public class HeartBeatDelegate {
    private static final Logger logger = LogManager.getLogger(HeartBeatDelegate.class);
    private static final String PRODUCT_MI = "mi";
    private static final String PRODUCT_SI = "si";
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();
    private final ScheduledExecutorService heartbeatScheduledExecutorService =
            Executors.newScheduledThreadPool(0);

    public Ack processHeartbeat(HeartbeatRequest heartbeatRequest) throws ManagementApiException {
        long currentTimestamp = System.currentTimeMillis();
        Ack ack = new Ack(Constants.FAIL_STATUS);
        HeartbeatObject heartbeat = new HeartbeatObject(
                heartbeatRequest.getProduct(), heartbeatRequest.getGroupId(), heartbeatRequest.getNodeId(),
                heartbeatRequest.getInterval(), heartbeatRequest.getMgtApiUrl(), currentTimestamp,
                heartbeatRequest.getChangeNotification().getDeployedArtifacts(),
                heartbeatRequest.getChangeNotification().getUndeployedArtifacts(),
                heartbeatRequest.getChangeNotification().getStateChangedArtifacts());
        boolean isSuccess;
        String productName = heartbeat.getProduct();
        ArtifactsManager artifactsManager = getArtifactManager(productName, heartbeat);

        if (isNodeRegistered(heartbeat)) {
            isSuccess = updateHeartbeat(heartbeat);
            artifactsManager.runUpdateExecutorService();
        } else {
            isSuccess = registerNode(heartbeat);
            if (isSuccess) {
                artifactsManager.runFetchAllExecutorService();
            }
        }
        runHeartbeatExecutorService(productName, heartbeat);

        if (isSuccess) {
            ack.setStatus(Constants.SUCCESS_STATUS);
        }
        return ack;
    }

    private boolean isNodeRegistered(HeartbeatObject heartbeat) {
        String timestamp =
                databaseManager.retrieveTimestampOfLastHeartbeat(heartbeat.getGroupId(), heartbeat.getNodeId());
        return (null != timestamp && !timestamp.isEmpty());
    }

    private boolean updateHeartbeat(HeartbeatObject heartbeat) {
        if (logger.isDebugEnabled()) {
            logger.debug("Updating heartbeat information of node " + heartbeat.getNodeId() + " in group : " +
                      heartbeat.getGroupId());
        }
        return databaseManager.updateHeartbeat(heartbeat);
    }

    private boolean registerNode(HeartbeatObject heartbeat) throws ManagementApiException {
        logger.info("New node " + heartbeat.getNodeId() + " in group : " + heartbeat.getGroupId() + " is registered." +
                 " Inserting heartbeat information");
        String accessToken = ManagementApiUtils.getAccessToken(heartbeat.getMgtApiUrl());
        return databaseManager.insertHeartbeat(heartbeat, accessToken);
    }

    private void runHeartbeatExecutorService(String productName, HeartbeatObject heartbeat) {
        long heartbeatInterval = heartbeat.getInterval();
        String timestampOfRegisteredNode =
                databaseManager.retrieveTimestampOfLastHeartbeat(heartbeat.getGroupId(), heartbeat.getNodeId());
        Runnable runnableTask = () -> {
            boolean isNodeDeregistered = isNodeShutDown(heartbeat, timestampOfRegisteredNode);
            if (isNodeDeregistered) {
                logger.info("Node : " + heartbeat.getNodeId() + " of group : " + heartbeat.getGroupId() + " has " +
                         "de-registered. Hence deleting node information");
                deleteNode(productName, heartbeat);
            }
        };
        heartbeatScheduledExecutorService.schedule(runnableTask, 3 * heartbeatInterval, TimeUnit.SECONDS);
        heartbeatScheduledExecutorService.shutdown();
    }

    private boolean isNodeShutDown(HeartbeatObject heartbeat, String initialTimestamp) {
        return !databaseManager.checkIfTimestampExceedsInitial(heartbeat, initialTimestamp);
    }

    private void deleteNode(String productName, HeartbeatObject heartbeat) {
        int rowCount = databaseManager.deleteHeartbeat(heartbeat);
        if (rowCount > 0) {
            logger.info("Successfully deleted node where group_id : " + heartbeat.getGroupId() + " and node_id : "
                     + heartbeat.getNodeId() + ".");
            deleteAllNodeData(productName, heartbeat);
        } else {
            throw new DashboardServerException("Error occurred while deleting node where group_id : "
                                               + heartbeat.getGroupId() + " and node_id : " + heartbeat.getNodeId()
                                               + ".");
        }
    }

    private void deleteAllNodeData(String productName, HeartbeatObject heartbeat) {
        logger.info("Deleting all artifacts and server information in node : " + heartbeat.getNodeId() + " in group: "
                 + heartbeat.getGroupId());
        ArtifactsManager artifactsManager = getArtifactManager(productName, heartbeat);
        artifactsManager.runDeleteAllExecutorService();
    }

    private ArtifactsManager getArtifactManager(String productName, HeartbeatObject heartbeat) {
        if (productName.equals(PRODUCT_MI)) {
            return new MiArtifactsManager(heartbeat);
        } else if (productName.equals(PRODUCT_SI)) {
            return new SiArtifactsFetcher(heartbeat);
        } else {
            throw new DashboardServerException("Unsupported product : " + productName);
        }
    }

}
