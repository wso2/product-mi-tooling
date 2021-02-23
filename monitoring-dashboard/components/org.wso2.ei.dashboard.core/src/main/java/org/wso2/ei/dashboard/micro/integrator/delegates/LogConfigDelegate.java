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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ByteArrayEntity;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.LogConfigAddRequest;
import org.wso2.ei.dashboard.core.rest.model.LogConfigUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.LogConfigs;
import org.wso2.ei.dashboard.core.rest.model.LogConfigsInner;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.NodeListInner;

import java.nio.charset.StandardCharsets;

/**
 * Delegate class to handle requests from log-configs page.
 */
public class LogConfigDelegate {
    private static final Log log = LogFactory.getLog(LogConfigDelegate.class);
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    public LogConfigs fetchLogConfigs(String groupId) {
        log.debug("Fetching log configs via management api.");
        JsonArray logConfigsArray = getLogConfigs(groupId);
        return createLogConfigsObject(logConfigsArray);
    }

    public LogConfigs fetchLogConfigsByNodeId(String groupId, String nodeId) {
        log.info("Fetching log configs in node " + nodeId + " in group " + groupId);
        JsonArray logConfigsArray = getLogConfigByNodeId(groupId, nodeId);
        return createLogConfigsObject(logConfigsArray);
    }

    public Ack updateLogLevel(String groupId, LogConfigUpdateRequest request) {
        log.debug("Updating logger " + request.getName() + " for all nodes in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createUpdateLoggerPayload(request);

        NodeList nodeList = databaseManager.fetchNodes(groupId);
        for (NodeListInner node : nodeList) {
            String nodeId = node.getNodeId();
            CloseableHttpResponse httpResponse = updateLogLevelByNodeId(groupId, nodeId, payload);

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                log.error("Error occurred while updating logger on node " + nodeId + " in group " + groupId);
                String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
                ack.setMessage(message);
                return ack;
            }
        }
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    public Ack updateLogLevelByNodeId(String groupId, String nodeId, LogConfigUpdateRequest request) {
        log.debug("Updating logger " + request.getName() + " in node " + nodeId + " in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createUpdateLoggerPayload(request);
        CloseableHttpResponse httpResponse = updateLogLevelByNodeId(groupId, nodeId, payload);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            log.error("Error occurred while updating logger on node " + nodeId + " in group " + groupId);
            String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
            ack.setMessage(message);
        } else {
            ack.setStatus(Constants.SUCCESS_STATUS);
        }
        return ack;
    }

    public Ack addLogger(String groupId, LogConfigAddRequest request) {
        log.debug("Adding new Logger " + request.getName() + " for all nodes in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createAddLoggerPayload(request);

        NodeList nodeList = databaseManager.fetchNodes(groupId);

        for (NodeListInner node : nodeList) {
            String nodeId = node.getNodeId();
            String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
            String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
            String addLoggerUrl = mgtApiUrl.concat("logging");
            log.debug("Adding new logger on node " + nodeId);
            CloseableHttpResponse httpResponse = doPatch(addLoggerUrl, accessToken, payload);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                log.error("Error occurred while adding logger on node " + nodeId + " in group " + groupId);
                String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
                ack.setMessage(message);
                return ack;
            }
        }
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    private JsonArray getLogConfigs(String groupId) {
        NodeList nodeList = databaseManager.fetchNodes(groupId);
        // assumption - In a group, log configs of all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        return getLogConfigByNodeId(groupId, nodeId);
    }

    private JsonArray getLogConfigByNodeId(String groupId, String nodeId) {
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        String url = mgtApiUrl.concat("logging");
        CloseableHttpResponse httpResponse = doGet(accessToken, url);
        return HttpUtils.getJsonArray(httpResponse);
    }

    private LogConfigs createLogConfigsObject(JsonArray logConfigsArray) {
        LogConfigs logConfigs = new LogConfigs();
        for (JsonElement element : logConfigsArray) {
            LogConfigsInner logConfigsInner = createLogConfig(element);
            logConfigs.add(logConfigsInner);
        }
        return logConfigs;
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

    private LogConfigsInner createLogConfig(JsonElement element) {
        JsonObject logConfig = element.getAsJsonObject();
        LogConfigsInner logConfigsInner = new LogConfigsInner();
        logConfigsInner.setName(logConfig.get("loggerName").getAsString());
        logConfigsInner.setComponentName(logConfig.get("componentName").getAsString());
        logConfigsInner.setLevel(logConfig.get("level").getAsString());
        return logConfigsInner;
    }

    private CloseableHttpResponse updateLogLevelByNodeId(String groupId, String nodeId, JsonObject payload) {
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        String updateLoggerUrl = mgtApiUrl.concat("logging");
        log.debug("Updating logger on node " + nodeId);
        return doPatch(updateLoggerUrl, accessToken, payload);
    }

    private CloseableHttpResponse doGet(String accessToken, String url) {
        String authHeader = "Bearer " + accessToken;
        final HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpGet.setHeader("Authorization", authHeader);

        return HttpUtils.doGet(httpGet);
    }

    private CloseableHttpResponse doPatch(String url, String accessToken, JsonObject payload) {
        String authHeader = "Bearer " + accessToken;
        final HttpPatch httpPatch = new HttpPatch(url);

        httpPatch.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPatch.setHeader("Content-Type", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPatch.setHeader("Authorization", authHeader);

        HttpEntity httpEntity = new ByteArrayEntity(payload.toString().getBytes(StandardCharsets.UTF_8));
        httpPatch.setEntity(httpEntity);
        return HttpUtils.doPatch(httpPatch);
    }
}

