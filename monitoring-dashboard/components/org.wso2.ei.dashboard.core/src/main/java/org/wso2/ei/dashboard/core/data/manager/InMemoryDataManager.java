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
import org.wso2.ei.dashboard.core.commons.Constants.Product;
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
    private static final String GROUP_ID = "groupId";
    private static final String NODE_ID = "nodeId";
    private static final String SERVICE_INFO = "serviceInfo";
    private static final String PRODUCT = "product";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String TIMESTAMP = "timeStamp";
    private static final String MGT_URL = "mgtUrl";
    private static final String INTERVAL = "interval";
    public static HashMap<String, HashMap> heartBeatStore;
    public static HashMap<String, HashMap> serviceInfoStore;

    public InMemoryDataManager() {
        heartBeatStore = new HashMap<>();
        serviceInfoStore = new HashMap<>();
        logger.debug("heartBeatStore and serviceInfoStore created ");
    }

    @Override
    public boolean insertHeartbeat(HeartbeatObject heartbeat, String accessToken) {
        if (heartbeat == null) {
            logger.debug("Received heartbeat object is null");
            return false;
        }
        try {
            HashMap map = new HashMap<>();
            map.put(GROUP_ID, heartbeat.getGroupId());
            map.put(NODE_ID, heartbeat.getNodeId());
            map.put(INTERVAL, heartbeat.getInterval());
            map.put(MGT_URL, heartbeat.getMgtApiUrl());
            map.put(TIMESTAMP, heartbeat.getTimestamp());
            map.put(ACCESS_TOKEN, accessToken);
            String keyString = heartbeat.getGroupId() + heartbeat.getNodeId();
            heartBeatStore.put(keyString, map);
            logger.info("Inserting heartbeat details of node " + heartbeat.getNodeId() +
                        " in group " + heartbeat.getGroupId());
            return heartBeatStore.size() > 0;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while inserting heartbeat information.", e);
        }
    }

    @Override
    public boolean insertServerInformation(HeartbeatObject heartbeat, String serverInfo) {
        try {
            HashMap map = new HashMap();
            map.put(GROUP_ID, heartbeat.getGroupId());
            map.put(NODE_ID, heartbeat.getNodeId());
            map.put(SERVICE_INFO, serverInfo);
            map.put(PRODUCT, heartbeat.getProduct());
            String keyString = heartbeat.getGroupId() + heartbeat.getNodeId();
            serviceInfoStore.put(keyString, map);
            logger.info("Adding serverInfo of node " + heartbeat.getNodeId() +  " in group " + heartbeat.getGroupId());
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
                groupId = entry.get(GROUP_ID).toString();
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
                if (entry.get(GROUP_ID).toString().equals(groupId)) {
                    NodeListInner nodeListInner = new NodeListInner();
                    nodeListInner.setNodeId(entry.get(NODE_ID).toString());
                    nodeListInner.setDetails(entry.get(SERVICE_INFO).toString());
                    nodeListInner.setType(entry.get(PRODUCT).toString());
                    nodeList.add(nodeListInner);
                }
            }
            return nodeList;
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred fetching servers.", e);
        }
    }

    @Override
    public NodeList fetchNodes(String groupId, Product productId) {
        try {
            NodeList nodeList = new NodeList();
            for (HashMap entry: serviceInfoStore.values()) {
                if (entry.get(GROUP_ID).toString().equals(groupId) &&
                        entry.get(PRODUCT).toString().equalsIgnoreCase(productId.toString())) {
                    NodeListInner nodeListInner = new NodeListInner();
                    nodeListInner.setNodeId(entry.get(NODE_ID).toString());
                    nodeListInner.setDetails(entry.get(SERVICE_INFO).toString());
                    nodeListInner.setType(entry.get(PRODUCT).toString());
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
            mgtApiUrl = valueMap.get(MGT_URL).toString();
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
            accessToken = valueMap.get(ACCESS_TOKEN).toString();
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
            String heartBeatInterval = valueMap.get(INTERVAL).toString();
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
            String timeStamp = valueMap.get(TIMESTAMP).toString();

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
                timeStamp = valueMap.get(TIMESTAMP).toString();
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
            valueMap.put(TIMESTAMP, String.valueOf(heartbeat.getTimestamp()));
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
            valueMap.put(ACCESS_TOKEN, accessToken);
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
            logger.info("Successfully deleted server information of node " + nodeId + " in group " + groupId);

            return !serviceInfoStore.containsKey(keyString);
        } catch (DashboardServerException e) {
            throw new DashboardServerException("Error occurred while deleting server information.", e);
        }
    }

}
