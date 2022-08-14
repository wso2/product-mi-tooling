/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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

package org.wso2.ei.dashboard.core.data.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.HeartbeatObject;
import org.wso2.ei.dashboard.core.rest.model.GroupList;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.NodeListInner;
import java.util.HashMap;

/**
 * Performs in-memory map operations.
 */
public final class InMemoryDataManager implements DataManager {

    private static final Logger logger = LogManager.getLogger(InMemoryDataManager.class);
    public static HashMap<String, HashMap> heartBeatStore;
    public static HashMap<String, HashMap> serviceInfoStore;

    public InMemoryDataManager() {
        heartBeatStore = new HashMap<>();
        serviceInfoStore = new HashMap<>();
        logger.debug("heartBeatStore and serviceInfoStore created ");
    }

    @Override
    public boolean insertHeartbeat(HeartbeatObject heartbeat, String accessToken) {
        try {
            HashMap map = new HashMap<>();
            map.put("groupId", heartbeat.getGroupId());
            map.put("nodeId", heartbeat.getNodeId());
            map.put("interval", heartbeat.getInterval());
            map.put("mgtUrl", heartbeat.getMgtApiUrl());
            map.put("timeStamp", heartbeat.getTimestamp());
            map.put("accessToken", accessToken);
            String keyString = heartbeat.getGroupId() + heartbeat.getNodeId();
            heartBeatStore.put(keyString, map);
            logger.info("Inserted heartbeat to node " + heartbeat.getNodeId() +  " of group " + heartbeat.getGroupId());
            return heartBeatStore.size() > 0;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while inserting heartbeat information.", e);
        }
    }

    @Override
    public boolean insertServerInformation(HeartbeatObject heartbeat, String serverInfo) {
        try {
            HashMap map = new HashMap();
            map.put("groupId", heartbeat.getGroupId());
            map.put("nodeId", heartbeat.getNodeId());
            map.put("serviceInfo", serverInfo);
            String keyString = heartbeat.getGroupId() + heartbeat.getNodeId();
            serviceInfoStore.put(keyString, map);
            logger.info("Added serverInfo to node " + heartbeat.getNodeId() +  " of group " + heartbeat.getGroupId());
            return serviceInfoStore.size() > 0;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while inserting server information of node : "
                                               + heartbeat.getNodeId() + " in group: " + heartbeat.getGroupId(), e);
        }
    }

    @Override
    public GroupList fetchGroups() {
        String groupId;
        try {
            GroupList groupList = new GroupList();
            for (HashMap entry: heartBeatStore.values()) {
                groupId = entry.get("groupId").toString();
                if (!groupList.contains(groupId)) {
                    groupList.add(groupId);
                }
            }
            return groupList;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred fetching groups.", e);
        }
    }

    @Override
    public NodeList fetchNodes(String groupId) {
        try {
            NodeList nodeList = new NodeList();
            for (HashMap entry: serviceInfoStore.values()) {
                if (entry.get("groupId").toString().equals(groupId)) {
                    NodeListInner nodeListInner = new NodeListInner();
                    nodeListInner.setNodeId(entry.get("nodeId").toString());
                    nodeListInner.setDetails(entry.get("serviceInfo").toString());
                    nodeList.add(nodeListInner);
                }
            }
            return nodeList;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred fetching servers.", e);
        }
    }

    @Override
    public String getMgtApiUrl(String groupId, String nodeId) {
        String mgtApiUrl = "";
        try  {
            HashMap valueMap = heartBeatStore.get(groupId + nodeId);
            mgtApiUrl = valueMap.get("mgtUrl").toString();
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while retrieveTimestampOfRegisteredNode results.", e);
        }
        return mgtApiUrl;
    }

    @Override
    public String getAccessToken(String groupId, String nodeId) {
        String accessToken = "";
        try {
            HashMap valueMap = heartBeatStore.get(groupId + nodeId);
            accessToken = valueMap.get("accessToken").toString();
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while retrieving access token of node: " + nodeId
                                               + " in group " + groupId, e);
        }
        return accessToken;
    }

    @Override
    public String getHeartbeatInterval(String groupId, String nodeId) {
        try  {
            HashMap valueMap = heartBeatStore.get(groupId + nodeId);
            String heartBeatInterval = valueMap.get("interval").toString();
            return heartBeatInterval;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while fetching heartbeat interval of group " + groupId
                                               + " node " + nodeId, e);
        }
    }

    @Override
    public boolean checkIfTimestampExceedsInitial(HeartbeatObject heartbeat, String initialTimestamp) {
        boolean isExists = false;
        try  {
            HashMap valueMap = heartBeatStore.get(heartbeat.getGroupId() + heartbeat.getNodeId());
            String timeStamp = valueMap.get("timeStamp").toString();

            if (Integer.parseInt(timeStamp) > Integer.parseInt(initialTimestamp)) {
                isExists =  true;
            }
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while retrieving next row.", e);
        }
        return isExists;
    }

    @Override
    public String retrieveTimestampOfLastHeartbeat(String groupId, String nodeId) {
        String keyString = groupId + nodeId;
        String timeStamp = null;
        try {
            if (heartBeatStore.containsKey(keyString)) {
                HashMap valueMap = heartBeatStore.get(groupId + nodeId);
                timeStamp = valueMap.get("timeStamp").toString();
            }
            return timeStamp;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while retrieveTimestampOfRegisteredNode results.", e);
        }
    }

    @Override
    public boolean updateHeartbeat(HeartbeatObject heartbeat) {
        String keyString = heartbeat.getGroupId() + heartbeat.getNodeId();
        try  {
            HashMap valueMap = heartBeatStore.get(keyString);
            valueMap.put("timeStamp", String.valueOf(heartbeat.getTimestamp()));
            heartBeatStore.put(keyString, valueMap);
            logger.debug("Updated Access token for " + heartbeat.getNodeId() + " in group " + heartbeat.getGroupId());
            return true;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while updating heartbeat information.", e);
        }
    }

    @Override
    public boolean updateAccessToken(String groupId, String nodeId, String accessToken) {
        String keyString = groupId + nodeId;
        try  {
            HashMap valueMap = heartBeatStore.get(keyString);
            valueMap.put("accessToken", accessToken);
            heartBeatStore.put(keyString, valueMap);
            logger.debug("Access token Updated of node " + nodeId + " in group " + groupId);
            return true;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while updating access token.", e);
        }
    }

    @Override
    public int deleteHeartbeat(HeartbeatObject heartbeat) {
        String keyString = heartbeat.getGroupId() + heartbeat.getNodeId();
        try  {
            heartBeatStore.remove(keyString);
            logger.info("Heartbeat deleted of node " + heartbeat.getNodeId() + " in group " + heartbeat.getGroupId());
            return heartBeatStore.containsKey(keyString) ? 0 : 1;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while deleting heartbeat information.", e);
        }
    }

    @Override
    public boolean deleteServerInformation(String groupId, String nodeId) {
        String keyString = groupId + nodeId;
        try  {
            serviceInfoStore.remove(keyString);
            logger.info("Server info  deleted of node " + nodeId + " in group " + groupId);
            return !serviceInfoStore.containsKey(keyString);
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while deleting server information.", e);
        }
    }

}
