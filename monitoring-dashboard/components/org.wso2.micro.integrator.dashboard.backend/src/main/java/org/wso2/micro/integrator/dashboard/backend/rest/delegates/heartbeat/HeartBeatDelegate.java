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

package org.wso2.micro.integrator.dashboard.backend.rest.delegates.heartbeat;

import org.wso2.micro.integrator.dashboard.backend.commons.Constants;
import org.wso2.micro.integrator.dashboard.backend.commons.utils.DatabaseConnection;
import org.wso2.micro.integrator.dashboard.backend.db.manager.DatabaseManager;
import org.wso2.micro.integrator.dashboard.backend.exception.DashboardServerException;
import org.wso2.micro.integrator.dashboard.backend.rest.model.Ack;
import org.wso2.micro.integrator.dashboard.backend.rest.model.HeatbeatSignalRequestBody;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HeartBeatDelegate {
    private static final Log log = LogFactory.getLog(HeartBeatDelegate.class);
    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";
    private final DatabaseManager databaseManager = DatabaseConnection.getDbManager();
    private final int heartbeatPoolSize = Integer.parseInt(System.getProperty(Constants.HEARTBEAT_POOL_SIZE));
    private ScheduledExecutorService heartbeatScheduledExecutorService = Executors.newScheduledThreadPool(heartbeatPoolSize);

    public Ack processHeartbeat(HeatbeatSignalRequestBody heartbeat) {
        Ack ack = new Ack(FAIL_STATUS);
        boolean isNodeRegistered = isNodeRegistered(heartbeat);
        int rowCount;
        if (!isNodeRegistered) {
            log.info("New node " + heartbeat.getNodeId() + " in group : " + heartbeat.getGroupId() + " is registered." +
                     " Inserting heartbeat information");
            rowCount = databaseManager.insertHeartbeat(heartbeat);
            fetchData();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Updating heartbeat information of node " + heartbeat.getNodeId() + " in group : " +
                          heartbeat.getGroupId());
            }
            rowCount = databaseManager.updateHeartbeat(heartbeat);
        }
        if (rowCount > 0) {
            ack.setStatus(SUCCESS_STATUS);
        }
        runHeartbeatExecutorService(heartbeat);
        return ack;
    }

    private boolean isNodeRegistered(HeatbeatSignalRequestBody heartbeat) {
        String timestamp = databaseManager.retrieveTimestampOfHeartBeat(heartbeat);
        return (null != timestamp && !timestamp.isEmpty());
    }

    private boolean isNodeShutDown(HeatbeatSignalRequestBody heartbeat, String initialTimestamp) {
        return !databaseManager.checkIfTimestampExceedsInitial(heartbeat, initialTimestamp);
    }

    private void runHeartbeatExecutorService(HeatbeatSignalRequestBody heartbeat) {
        int heartbeatInterval = heartbeat.getInterval();
        String timestampOfRegisteredNode = databaseManager.retrieveTimestampOfHeartBeat(heartbeat);
        Runnable runnableTask = () -> {
            boolean isNodeDeregistered = isNodeShutDown(heartbeat, timestampOfRegisteredNode);
            if (isNodeDeregistered) {
                log.info("Node : " + heartbeat.getNodeId() + " of group : " + heartbeat.getGroupId() + " has " +
                         "de-registered. Hence deleting node information");
                deleteNode(heartbeat);
            }
        };
        heartbeatScheduledExecutorService.schedule(runnableTask, 3*heartbeatInterval, TimeUnit.SECONDS);
        // todo this should be triggered during shutdown
        heartbeatScheduledExecutorService.shutdown();
    }

    private void deleteNode(HeatbeatSignalRequestBody heartbeat) {
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

    private void fetchData() {
        // todo to be implemented
        log.info("Fetching Data...");
    }
}
