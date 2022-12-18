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

package org.wso2.ei.dashboard.micro.integrator.delegates;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.model.LogDetail;
import org.wso2.ei.dashboard.core.rest.model.LogList;
import org.wso2.ei.dashboard.core.rest.model.LogListInner;
import org.wso2.ei.dashboard.core.rest.model.LogsResourceResponse;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Delegate class to handle requests from log files page.
 */
public class LogsDelegate {
    private static final Log logger = LogFactory.getLog(LogsDelegate.class);
    private static final DataManager dataManager = DataManagerSingleton.getDataManager();
    private static List<LogListInner>  searchedList;
    private static String prevSearchKey = null;
    private static int count;

    public LogsResourceResponse getPaginatedLogsListResponse(String groupId, List<String> nodeList, String searchKey, 
        String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) 
        throws ManagementApiException {
        
        logger.debug("Fetching Searched Endpoints from MI.");
        logger.debug("group id :" + groupId + ", lowerlimit :" + lowerLimit + ", upperlimit: " + upperLimit);
        logger.debug("Order:" + order + ", OrderBy:" + orderBy + ", isUpdate:" + isUpdate);
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        boolean isUpdatedContent = Boolean.parseBoolean(isUpdate);

        logger.debug("prevSearch key :" + prevSearchKey + ", currentSearch key:" + searchKey);

        if (isUpdatedContent || prevSearchKey == null || !(prevSearchKey.equals(searchKey))) {
            searchedList = getSearchedLogsListFromMI(groupId, nodeList, searchKey, order, orderBy);
            count = getLogsCount(searchedList);
        }
        LogsResourceResponse logsResourceResponse = new LogsResourceResponse();
        LogList paginatedList = getPaginationResults(searchedList, fromIndex, toIndex);
        logsResourceResponse.setResourceList(paginatedList);
        logsResourceResponse.setCount(count);
        prevSearchKey = searchKey;
        return logsResourceResponse;
    }

    private int getLogsCount(List<LogListInner> loglist) {
        int count = 0;
        List<String> logsNames = new ArrayList<>();
        String name;
        for (LogListInner log : loglist) {
            name = log.getName();
            if (!logsNames.contains(name)) {
                count++;
                logsNames.add(name);
            }
        }
        return count;
    }

    public static List<LogListInner> getSearchedLogsListFromMI(String groupId, List<String> nodeList,
        String searchKey, String order, String orderBy) throws ManagementApiException {
            

        logger.debug("Fetching logs via management api.");
        LogList logList = new LogList();
        for (String nodeId : nodeList) {
            String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
            String accessToken = dataManager.getAccessToken(groupId, nodeId);
            JsonArray logsArray = DelegatesUtil.getResourceResultList(groupId, nodeId, 
                Constants.LOGS, mgtApiUrl, accessToken, searchKey);

            for (JsonElement jsonElement : logsArray) {
                JsonObject logObject =  (JsonObject) jsonElement;
                String fileName = logObject.get("FileName").getAsString();
                String fileSize = logObject.get("Size").getAsString();
                AtomicBoolean isRecordExist = new AtomicBoolean(false);
                logList.stream().filter(o -> o.getName().equals(fileName)).forEach(
                        o -> {
                            LogDetail logDetail = new LogDetail();
                            logDetail.setNodeId(nodeId);
                            logDetail.setLogSize(fileSize);
                            o.getNodes().add(logDetail);
                            isRecordExist.set(true);
                        });
                if (!isRecordExist.get()) {
                    LogListInner logListInner = new LogListInner();
                    logListInner.setName(fileName);
                    List<LogDetail> logDetailList = new ArrayList<>();
                    LogDetail logDetail = new LogDetail();
                    logDetail.setNodeId(nodeId);
                    logDetail.setLogSize(fileSize);
                    logDetailList.add(logDetail);
                    logListInner.setNodes(logDetailList);
                    logList.add(logListInner);
                }
            }
        }
        //ordering   
        Comparator<LogListInner> comparatorObject;
        switch (orderBy) {
            //add if any other parms
            default: comparatorObject = Comparator.comparing(LogListInner::getNameIgnoreCase); break;
        }
        if ("desc".equalsIgnoreCase(order)) {
            Collections.sort(logList, comparatorObject.reversed());
        } else {
            Collections.sort(logList, comparatorObject);
        }
        
        return logList;
    }

    
         /**
     * Returns the results list items within the given range
     *
     * @param itemsList the list containing all the items of a specific type
     * @param lowerLimit from index of the required range
     * @param upperLimit to index of the required range
     * @return the List if no error. Else return null
     */
    public static LogList getPaginationResults(List<LogListInner> itemsList, int lowerLimit, int upperLimit) {
        
        LogList resultList = new LogList();
        try {
            if (itemsList.size() < upperLimit) {
                upperLimit = itemsList.size();
            }
            if (upperLimit < lowerLimit) {
                lowerLimit = upperLimit;
            }
            List<LogListInner> paginatedList = itemsList.subList(lowerLimit, upperLimit);
        
            for (LogListInner artifact : paginatedList) {
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

    public String getLogByName(String groupId, String nodeId, String fileName) throws ManagementApiException {
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("logs?file=").concat(fileName);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url);
        HttpEntity responseEntity = httpResponse.getEntity();
        String response = "";
        if (responseEntity != null) {
            try {
                response = EntityUtils.toString(responseEntity);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

        }
        return response;
    }
}
