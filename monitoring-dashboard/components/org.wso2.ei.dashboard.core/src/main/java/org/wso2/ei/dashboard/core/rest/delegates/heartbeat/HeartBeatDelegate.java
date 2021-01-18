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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.HeartbeatRequest;
import org.wso2.ei.dashboard.micro.integrator.MiNodeDataFetcher;
import org.wso2.ei.dashboard.streaming.integrator.SiNodeDataFetcher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages heartbeats received to the dashboard.
 */
public class HeartBeatDelegate {
    private static final Log log = LogFactory.getLog(HeartBeatDelegate.class);
    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();
    private final int heartbeatPoolSize = Integer.parseInt(Constants.HEARTBEAT_POOL_SIZE);
    private ScheduledExecutorService heartbeatScheduledExecutorService =
            Executors.newScheduledThreadPool(heartbeatPoolSize);

    public Ack processHeartbeat(HeartbeatRequest heartbeatRequest) {
        HeartbeatObject heartbeat = new HeartbeatObject(heartbeatRequest.getProduct(), heartbeatRequest.getGroupId(),
                                                        heartbeatRequest.getNodeId(), heartbeatRequest.getInterval(),
                                                        heartbeatRequest.getMgtApiUrl());
        Ack ack = new Ack(FAIL_STATUS);
        NodeDataFetcher nodeDataFetcher;

        boolean isSuccess;
        if (isNodeRegistered(heartbeat)) {
            isSuccess = updateHeartbeat(heartbeat);
        } else {
            isSuccess = registerNode(heartbeat);
            String productName = heartbeat.getProduct();
            if (productName.equals("mi")) {
                nodeDataFetcher = new MiNodeDataFetcher(heartbeat);
            } else if (productName.equals("si")) {
                nodeDataFetcher = new SiNodeDataFetcher(heartbeat);
            } else {
                throw new DashboardServerException("Unsupported product : " + productName);
            }
            nodeDataFetcher.runFetchExecutorService();
        }
        runHeartbeatExecutorService(heartbeat);
        if (isSuccess) {
            ack.setStatus(SUCCESS_STATUS);
        }
        return ack;
    }

    private boolean isNodeRegistered(HeartbeatObject heartbeat) {
        String timestamp = databaseManager.retrieveTimestampOfHeartBeat(heartbeat);
        return (null != timestamp && !timestamp.isEmpty());
    }

    private boolean updateHeartbeat(HeartbeatObject heartbeat) {
        if (log.isDebugEnabled()) {
            log.debug("Updating heartbeat information of node " + heartbeat.getNodeId() + " in group : " +
                      heartbeat.getGroupId());
        }
        return databaseManager.updateHeartbeat(heartbeat);
    }

    private boolean registerNode(HeartbeatObject heartbeat) {
        log.info("New node " + heartbeat.getNodeId() + " in group : " + heartbeat.getGroupId() + " is registered." +
                 " Inserting heartbeat information");
        return databaseManager.insertHeartbeat(heartbeat);
    }

    private void runHeartbeatExecutorService(HeartbeatObject heartbeat) {
        long heartbeatInterval = heartbeat.getInterval();
        String timestampOfRegisteredNode = databaseManager.retrieveTimestampOfHeartBeat(heartbeat);
        Runnable runnableTask = () -> {
            boolean isNodeDeregistered = isNodeShutDown(heartbeat, timestampOfRegisteredNode);
            if (isNodeDeregistered) {
                log.info("Node : " + heartbeat.getNodeId() + " of group : " + heartbeat.getGroupId() + " has " +
                         "de-registered. Hence deleting node information");
                deleteNode(heartbeat);
            }
        };
        heartbeatScheduledExecutorService.schedule(runnableTask, 3 * heartbeatInterval, TimeUnit.SECONDS);
        heartbeatScheduledExecutorService.shutdown();
    }

    private boolean isNodeShutDown(HeartbeatObject heartbeat, String initialTimestamp) {
        return !databaseManager.checkIfTimestampExceedsInitial(heartbeat, initialTimestamp);
    }

    private void deleteNode(HeartbeatObject heartbeat) {
        int rowCount = databaseManager.deleteHeartbeat(heartbeat);
        if (rowCount > 0) {
            log.info("Successfully deleted node where group_id : " + heartbeat.getGroupId() + " and node_id : "
                     + heartbeat.getNodeId() + ".");
            deleteAllData();
        } else {
            throw new DashboardServerException("Error occurred while deleting node where group_id : "
                                               + heartbeat.getGroupId() + " and node_id : " + heartbeat.getNodeId()
                                               + ".");
        }
    }

    private void deleteAllData() {
        // todo to be implemented once fetching mechanism completed
        log.info("Deleting all node info....");
    }

}
