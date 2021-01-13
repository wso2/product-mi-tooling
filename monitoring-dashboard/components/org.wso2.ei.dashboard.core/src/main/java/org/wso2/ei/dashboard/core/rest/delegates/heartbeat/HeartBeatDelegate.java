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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.HeatbeatSignalRequestBody;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
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
    private static final String SERVER = "server";
    private static final String PROXY_SERVICES = "proxy-services";
    private static final String APIS = "apis";
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();
    private final int heartbeatPoolSize = Integer.parseInt(Constants.HEARTBEAT_POOL_SIZE);
    private ScheduledExecutorService heartbeatScheduledExecutorService =
            Executors.newScheduledThreadPool(heartbeatPoolSize);

    public Ack processHeartbeat(HeatbeatSignalRequestBody heartbeat) {
        Ack ack = new Ack(FAIL_STATUS);
        boolean isSuccess;
        if (isNodeRegistered(heartbeat)) {
            isSuccess = updateHeartbeat(heartbeat);
        } else {
            isSuccess = registerAndFetchData(heartbeat);
        }
        if (isSuccess) {
            ack.setStatus(SUCCESS_STATUS);
        }
        runHeartbeatExecutorService(heartbeat);
        return ack;
    }

    private boolean updateHeartbeat(HeatbeatSignalRequestBody heartbeat) {
        if (log.isDebugEnabled()) {
            log.debug("Updating heartbeat information of node " + heartbeat.getNodeId() + " in group : " +
                      heartbeat.getGroupId());
        }
        return databaseManager.updateHeartbeat(heartbeat);
    }

    private boolean registerAndFetchData(HeatbeatSignalRequestBody heartbeat) {
        log.info("New node " + heartbeat.getNodeId() + " in group : " + heartbeat.getGroupId() + " is registered." +
                 " Inserting heartbeat information");
        boolean isSuccess = databaseManager.insertHeartbeat(heartbeat);
        Set<String> artifactSet = new HashSet<>(Arrays.asList(SERVER, PROXY_SERVICES, APIS));
        String accessToken = getAccessToken(heartbeat);
        fetchData(artifactSet, accessToken, heartbeat);
        return isSuccess;
    }

    private boolean isNodeRegistered(HeatbeatSignalRequestBody heartbeat) {
        String timestamp = databaseManager.retrieveTimestampOfHeartBeat(heartbeat);
        return (null != timestamp && !timestamp.isEmpty());
    }

    private boolean isNodeShutDown(HeatbeatSignalRequestBody heartbeat, String initialTimestamp) {
        return !databaseManager.checkIfTimestampExceedsInitial(heartbeat, initialTimestamp);
    }

    private void runHeartbeatExecutorService(HeatbeatSignalRequestBody heartbeat) {
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

    private void fetchData(Set artifactList, String accessToken, HeatbeatSignalRequestBody heartbeat) {
        if (!artifactList.isEmpty()) {
            String artifact = artifactList.iterator().next().toString();
            if (fetchAndStore(artifact, accessToken, heartbeat)) {
                artifactList.remove(artifact);
                fetchData(artifactList, accessToken, heartbeat);
            }
        }
    }

    private boolean fetchAndStore(String artifact, String accessToken, HeatbeatSignalRequestBody heartbeat) {
        switch (artifact) {
            case SERVER :
                return fetchAndStoreServers(accessToken, heartbeat);
            case PROXY_SERVICES :
                return fetchAndStoreProxyServices(accessToken, heartbeat);
            case APIS :
                return fetchAndStoreApis(accessToken, heartbeat);
            default:
                throw new DashboardServerException("Artifact type " + artifact + " is invalid.");
        }
    }

    private boolean fetchAndStoreServers(String accessToken, HeatbeatSignalRequestBody heartbeat) {
        String url = heartbeat.getMgtApiUrl() + SERVER;
        CloseableHttpResponse response = doGet(accessToken, url);
        String stringResponse = getStringResponse(response);
        return storeServerInfo(stringResponse, heartbeat);
    }

    private boolean storeServerInfo(String stringResponse, HeatbeatSignalRequestBody heartbeat) {
        return databaseManager.insertServerInformation(heartbeat, stringResponse);
    }

    private boolean fetchAndStoreProxyServices(String accessToken, HeatbeatSignalRequestBody heartbeat) {
        String url = heartbeat.getMgtApiUrl() + PROXY_SERVICES;
        CloseableHttpResponse response = doGet(accessToken, url);
        JsonObject proxyServices = getJsonResponse(response);
        int serviceCount = proxyServices.get("count").getAsInt();
        if (serviceCount > 0) {
            JsonArray serviceList = proxyServices.get("list").getAsJsonArray();
            for (int i = 0; i < serviceCount; i++) {
                String serviceName = serviceList.get(i).getAsJsonObject().get("name").getAsString();
                String proxyInfoUrl = url + "?proxyServiceName=" + serviceName;
                CloseableHttpResponse proxyDetails = doGet(accessToken, proxyInfoUrl);
                boolean isSuccess = storeProxyServices(heartbeat, serviceName, getStringResponse(proxyDetails));
                if (!isSuccess) {
                    throw new DashboardServerException("Error occurred while adding " + serviceName + " proxy details");
                }
            }
        }
        return true;
    }

    private boolean storeProxyServices(HeatbeatSignalRequestBody heartbeat, String serviceName, String details) {
        return databaseManager.insertProxyServices(heartbeat, serviceName, details);
    }

    private boolean fetchAndStoreApis(String accessToken, HeatbeatSignalRequestBody heartbeat) {
        String url = heartbeat.getMgtApiUrl() + APIS;
        CloseableHttpResponse response = doGet(accessToken, url);
        JsonObject apis = getJsonResponse(response);
        int apiCount = apis.get("count").getAsInt();
        if (apiCount > 0) {
            JsonArray apiList = apis.get("list").getAsJsonArray();
            for (int i = 0; i < apiCount; i++) {
                String apiName = apiList.get(i).getAsJsonObject().get("name").getAsString();
                String apiInfoUrl = url + "?apiName=" + apiName;
                CloseableHttpResponse apiDetails = doGet(accessToken, apiInfoUrl);
                boolean isSuccess = storeApis(heartbeat, apiName, getStringResponse(apiDetails));
                if (!isSuccess) {
                    throw new DashboardServerException("Error occurred while adding " + apiName + " api details");
                }
            }
        }
        return true;
    }
    private boolean storeApis(HeatbeatSignalRequestBody heartbeat, String apiName, String details) {
        return databaseManager.insertApis(heartbeat, apiName, details);
    }

    private String getAccessToken(HeatbeatSignalRequestBody heartbeat) {
        String username = System.getProperty("mi_username");
        String password = System.getProperty("mi_password");
        String usernamePassword = username + ":" + password;
        String encodedUsernamePassword = Base64.getEncoder().encodeToString(usernamePassword.getBytes());
        String loginUrl = heartbeat.getMgtApiUrl() + "login";

        final HttpGet httpGet = new HttpGet(loginUrl);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Authorization", "Basic " + encodedUsernamePassword);
        CloseableHttpResponse response = HttpUtils.doGet(httpGet);
        JsonObject jsonResponse = getJsonResponse(response);

        if (jsonResponse.has("AccessToken")) {
            return jsonResponse.get("AccessToken").getAsString();
        } else {
            throw new DashboardServerException("Error occurred while retrieving access token from management api.");
        }
    }

    private CloseableHttpResponse doGet(String accessToken, String url) {
        String authHeader = "Bearer " + accessToken;
        final HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Authorization", authHeader);

        return HttpUtils.doGet(httpGet);
    }

    private JsonObject getJsonResponse(CloseableHttpResponse response) {
        String stringResponse = getStringResponse(response);
        return JsonParser.parseString(stringResponse).getAsJsonObject();
    }

    private String getStringResponse(CloseableHttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            return EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            throw new DashboardServerException("Error occurred while converting Http response to string", e);
        }

    }

}
