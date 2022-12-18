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
 *
 */

package org.wso2.ei.dashboard.micro.integrator.delegates;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.LogConfigAddRequest;
import org.wso2.ei.dashboard.core.rest.model.LogConfigUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.LogConfigs;
import org.wso2.ei.dashboard.core.rest.model.LogConfigsInner;
import org.wso2.ei.dashboard.core.rest.model.LogConfigsResourceResponse;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.NodeListInner;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Delegate class to handle requests from log-configs page.
 */
public class LogConfigDelegate {
    private static final Logger logger = LogManager.getLogger(LogConfigDelegate.class);
    private static final DataManager dataManager = DataManagerSingleton.getDataManager();
    private static List<LogConfigsInner>  searchedList;
    private static String prevSearchKey = null;
    private static int count;

    public LogConfigsResourceResponse fetchPaginatedLogConfigsResponse(String groupId,
        List<String> nodeList, String searchKey, String lowerLimit, String upperLimit, String order,
        String orderBy, String isUpdate) throws ManagementApiException {

        logger.debug("group id :" + groupId + ", lowerlimit :" + lowerLimit + ", upperlimit: " + upperLimit);
        logger.debug("Order:" + order + ", OrderBy:" + orderBy + ", isUpdate:" + isUpdate);
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        boolean isUpdatedContent = Boolean.parseBoolean(isUpdate);

        LogConfigsResourceResponse logsResourceResponse = new LogConfigsResourceResponse();
        logger.debug("prevSearch key :" + prevSearchKey + ", currentSearch key:" + searchKey);

        if (isUpdatedContent || prevSearchKey == null || !(prevSearchKey.equals(searchKey))) {
            searchedList = getSearchedLogConfigsResultsFromMI(groupId,
                    nodeList, searchKey, order, orderBy);
            count = searchedList.size();
        }
        LogConfigs paginatedList = getPaginationResults(searchedList, fromIndex, toIndex);
        logsResourceResponse.setResourceList(paginatedList);
        logsResourceResponse.setCount(count);
        prevSearchKey = searchKey;
        return logsResourceResponse;
    }   

    public List<LogConfigsInner> getSearchedLogConfigsResultsFromMI(String groupId, List<String> nodeList, 
        String searchKey, String order, String orderBy) throws ManagementApiException {
        
        if (nodeList.contains("all")) {
            NodeList nodes = dataManager.fetchNodes(groupId);
            nodeList = new ArrayList<>();
            for (NodeListInner nodeListInner : nodes) {
                nodeList.add(nodeListInner.getNodeId());
            }
        }

        LogConfigs logConfigs = new LogConfigs();
        
        for (String nodeId: nodeList) {
            String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
            String accessToken = dataManager.getAccessToken(groupId, nodeId);

            JsonArray logConfigsArray = DelegatesUtil.getResourceResultList(groupId, nodeId, "logging",
                mgtApiUrl, accessToken, searchKey);
            
            for (JsonElement element : logConfigsArray) {
                LogConfigsInner logConfigsInner = createLogConfig(element);
                logConfigs.add(logConfigsInner);
            }

        }
        //ordering   
        Comparator<LogConfigsInner> comparatorObject;
        switch (orderBy) {
            case "level":comparatorObject = Comparator.comparing(LogConfigsInner::getLevelIgnoreCase); break;
            case "componentName":comparatorObject = Comparator.comparing
                (LogConfigsInner::getComponentNameIgnoreCase); break;
            default: comparatorObject = Comparator.comparing(LogConfigsInner::getNameIgnoreCase); break;
        }
        if ("desc".equalsIgnoreCase(order)) {
            Collections.sort(logConfigs, comparatorObject.reversed());
        } else {
            Collections.sort(logConfigs, comparatorObject);
        }
        return logConfigs;
    }

    private LogConfigsInner createLogConfig(JsonElement element) {
        JsonObject logConfig = element.getAsJsonObject();
        LogConfigsInner logConfigsInner = new LogConfigsInner();
        logConfigsInner.setName(logConfig.get("loggerName").getAsString());
        logConfigsInner.setComponentName(logConfig.get("componentName").getAsString());
        logConfigsInner.setLevel(logConfig.get("level").getAsString());
        return logConfigsInner;
    }

     /**
     * Returns the results list items within the given range
     *
     * @param itemsList the list containing all the items of a specific type
     * @param lowerLimit from index of the required range
     * @param upperLimit to index of the required range
     * @return the List if no error. Else return null
     */
    public static LogConfigs getPaginationResults(List<LogConfigsInner> itemsList, 
        int lowerLimit, int upperLimit) {
        
        LogConfigs resultList = new LogConfigs();
        try {
            if (itemsList.size() < upperLimit) {
                upperLimit = itemsList.size();
            }
            if (upperLimit < lowerLimit) {
                lowerLimit = upperLimit;
            }
            List<LogConfigsInner> paginatedList = itemsList.subList(lowerLimit, upperLimit);
        
            for (LogConfigsInner artifact : paginatedList) {
                resultList.add(artifact);
            }
            
            return resultList;

        } catch (IndexOutOfBoundsException e) {
            logger.error("Index values are out of bound", e);
        } catch (IllegalArgumentException e) {
            logger.error("Illegal arguments for index values", e);
        }
        return null;
    }

    public Ack updateLogLevel(String groupId, LogConfigUpdateRequest request) throws ManagementApiException {
        logger.debug("Updating logger " + request.getName() + " for all nodes in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createUpdateLoggerPayload(request);

        NodeList nodeList = dataManager.fetchNodes(groupId);
        for (NodeListInner node : nodeList) {
            String nodeId = node.getNodeId();
            CloseableHttpResponse httpResponse = updateLogLevelByNodeId(groupId, nodeId, payload);

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                logger.error("Error occurred while updating logger on node " + nodeId + " in group " + groupId);
                String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
                ack.setMessage(message);
                return ack;
            }
        }
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    public Ack updateLogLevelByNodeId(String groupId, String nodeId, LogConfigUpdateRequest request)
            throws ManagementApiException {
        logger.debug("Updating logger " + request.getName() + " in node " + nodeId + " in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createUpdateLoggerPayload(request);
        CloseableHttpResponse httpResponse = updateLogLevelByNodeId(groupId, nodeId, payload);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            logger.error("Error occurred while updating logger on node " + nodeId + " in group " + groupId);
            String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
            ack.setMessage(message);
        } else {
            ack.setStatus(Constants.SUCCESS_STATUS);
        }
        return ack;
    }

    public Ack addLogger(String groupId, LogConfigAddRequest request) throws ManagementApiException {
        logger.debug("Adding new Logger " + request.getName() + " for all nodes in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createAddLoggerPayload(request);

        NodeList nodeList = dataManager.fetchNodes(groupId);

        for (NodeListInner node : nodeList) {
            String nodeId = node.getNodeId();
            String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
            String accessToken = dataManager.getAccessToken(groupId, nodeId);
            String addLoggerUrl = mgtApiUrl.concat("logging");
            logger.debug("Adding new logger on node " + nodeId);
            CloseableHttpResponse httpResponse = Utils.doPatch(groupId, nodeId, accessToken, addLoggerUrl, payload);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                logger.error("Error occurred while adding logger on node " + nodeId + " in group " + groupId);
                String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
                ack.setMessage(message);
                return ack;
            }
        }
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    private JsonObject createUpdateLoggerPayload(LogConfigUpdateRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("loggerName", request.getName());
        payload.addProperty("loggingLevel", request.getLevel());
        return payload;
    }

    private JsonObject createAddLoggerPayload(LogConfigAddRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("loggerName", request.getName());
        payload.addProperty("loggerClass", request.getLoggerClass());
        payload.addProperty("loggingLevel", request.getLevel());
        return payload;
    }

    private CloseableHttpResponse updateLogLevelByNodeId(String groupId, String nodeId, JsonObject payload)
            throws ManagementApiException {
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        String updateLoggerUrl = mgtApiUrl.concat("logging");
        logger.debug("Updating logger on node " + nodeId);
        return Utils.doPatch(groupId, nodeId, accessToken, updateLoggerUrl, payload);
    }
}

