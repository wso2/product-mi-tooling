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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.wso2.ei.dashboard.core.commons.Constants.Product;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.NodeListInner;
import org.wso2.ei.dashboard.core.rest.model.NodesResourceResponse;

import java.util.List;
/**
 * Delegate class to handle requests from Nodes page (Home page).
 */
public class NodesDelegate {
    private final DataManager dataManager = DataManagerSingleton.getDataManager();
    private static final Logger logger = LogManager.getLogger(NodesDelegate.class);

    public NodeList getNodes(String groupId) {
        logger.debug("Fetching node list in " + groupId + " from MI.");
        NodeList nodeList = dataManager.fetchNodes(groupId);
        for (NodeListInner nodeListInner : nodeList) {
            String nodeId = nodeListInner.getNodeId();
            long heartbeatInterval = Long.parseLong(dataManager.getHeartbeatInterval(groupId, nodeId));
            long lastTimestamp = Long.parseLong(dataManager.retrieveTimestampOfLastHeartbeat(groupId, nodeId));
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

    public NodeList getNodesByProductID(String groupId, String productId) {
        logger.debug("Fetching node list in " + groupId + " in product " + productId);
        //productId can be either "mi" or "bal"
        return dataManager.fetchNodes(groupId, Product.valueOf(productId.toUpperCase()));
    }

    public NodeList getPaginatedNodesList(String groupId, int lowerLimit, int upperLimit) {  
        NodeList nodeList = getNodes(groupId);
        NodeList resultList = new NodeList();
        try {
            if (nodeList.size() < upperLimit) {
                upperLimit = nodeList.size();
            }
            if (upperLimit < lowerLimit) {
                lowerLimit = upperLimit;
            }
            List<NodeListInner> paginatedList = nodeList.subList(lowerLimit, upperLimit);
            for (NodeListInner node : paginatedList) {
                resultList.add(node);
            }
            return resultList;
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Index values are out of bound : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Illegal arguments for index values", e);
        }
    }

    private int getCount(String groupId) {
        return getNodes(groupId).size();
    }

    public NodesResourceResponse getNodesResponse(String groupId, String lowerLimit, String upperLimit) {
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        NodesResourceResponse nodesResourceResponse = new NodesResourceResponse();
        NodeList nodeList = getPaginatedNodesList(groupId, fromIndex, toIndex);
        int count = getCount(groupId);
        nodesResourceResponse.setResourceList(nodeList);
        nodesResourceResponse.setCount(count);
        return nodesResourceResponse;
    }    
}
