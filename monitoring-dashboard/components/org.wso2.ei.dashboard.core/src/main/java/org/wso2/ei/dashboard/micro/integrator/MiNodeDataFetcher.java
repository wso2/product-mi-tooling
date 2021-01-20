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

package org.wso2.ei.dashboard.micro.integrator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.HeartbeatObject;
import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.NodeDataFetcher;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fetch artifact information from registered micro integrator nodes and store.
 */
public class MiNodeDataFetcher implements NodeDataFetcher {
    private static final Log log = LogFactory.getLog(MiNodeDataFetcher.class);
    private static final String SERVER = "server";
    private static final String PROXY_SERVICES = "proxy-services";
    private static final String APIS = "apis";
    private static final Set<String> ALL_ARTIFACTS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(SERVER, PROXY_SERVICES, APIS)));
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();
    private final HeartbeatObject heartbeat;

    public MiNodeDataFetcher(HeartbeatObject heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Override
    public void runFetchExecutorService() {
        ExecutorService fetchExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Runnable runnable = () -> fetchData(ALL_ARTIFACTS);
        fetchExecutor.execute(runnable);
    }

    @Override
    public void fetchData(Set<String> artifactList) {
        String accessToken = getAccessToken(heartbeat);
        for (String artifact : artifactList) {
            fetchAndStore(artifact, accessToken);
        }
    }

    private void fetchAndStore(String artifact, String accessToken) {
        switch (artifact) {
            case SERVER:
                fetchAndStoreServers(accessToken);
                break;
            case PROXY_SERVICES:
                fetchAndStoreProxyServices(accessToken);
                break;
            case APIS:
                fetchAndStoreApis(accessToken);
                break;
            default:
                throw new DashboardServerException("Artifact type " + artifact + " is invalid.");
        }
    }

    private void fetchAndStoreServers(String accessToken) {
        String url = heartbeat.getMgtApiUrl() + SERVER;
        CloseableHttpResponse response = doGet(accessToken, url);
        String stringResponse = HttpUtils.getStringResponse(response);
        storeServerInfo(stringResponse, heartbeat);
    }

    private void storeServerInfo(String stringResponse, HeartbeatObject heartbeat) {
        boolean isSuccess = databaseManager.insertServerInformation(heartbeat, stringResponse);
        if (!isSuccess) {
            log.error("Error occurred while adding server details of node: " + heartbeat.getNodeId() + " in group "
                      + heartbeat.getGroupId());
            addToDelayedQueue();
        }
    }

    private void addToDelayedQueue() {
        // todo
    }

    private void fetchAndStoreProxyServices(String accessToken) {
        final String url = heartbeat.getMgtApiUrl() + PROXY_SERVICES;
        CloseableHttpResponse response = doGet(accessToken, url);
        JsonObject proxyServices = HttpUtils.getJsonResponse(response);
        JsonArray serviceList = proxyServices.get("list").getAsJsonArray();
        for (int i = 0; i < serviceList.size(); i++) {
            final String serviceName = serviceList.get(i).getAsJsonObject().get("name").getAsString();
            final String proxyInfoUrl = url + "?proxyServiceName=" + serviceName;
            CloseableHttpResponse proxyDetails = doGet(accessToken, proxyInfoUrl);
            JsonObject jsonProxyDetails = removeConfigurationFromResponse(proxyDetails);
            boolean isSuccess = storeProxyServices(serviceName, jsonProxyDetails.toString());
            if (!isSuccess) {
                log.error("Error occurred while adding " + serviceName + " proxy details");
                addToDelayedQueue();
            }
        }
    }

    private boolean storeProxyServices(String serviceName, String details) {
        return databaseManager.insertProxyServices(heartbeat, serviceName, details);
    }

    private void fetchAndStoreApis(String accessToken) {
        String url = heartbeat.getMgtApiUrl() + APIS;
        CloseableHttpResponse response = doGet(accessToken, url);
        JsonObject apis = HttpUtils.getJsonResponse(response);
        int apiCount = apis.get("count").getAsInt();
        if (apiCount > 0) {
            JsonArray apiList = apis.get("list").getAsJsonArray();
            for (int i = 0; i < apiCount; i++) {
                String apiName = apiList.get(i).getAsJsonObject().get("name").getAsString();
                String apiInfoUrl = url + "?apiName=" + apiName;
                CloseableHttpResponse apiDetails = doGet(accessToken, apiInfoUrl);
                JsonObject jsonProxyDetails = removeConfigurationFromResponse(apiDetails);
                boolean isSuccess = storeApis(apiName, jsonProxyDetails.toString());
                if (!isSuccess) {
                    log.error("Error occurred while adding " + apiName + " api details");
                    addToDelayedQueue();
                }
            }
        }
    }

    private boolean storeApis(String apiName, String details) {
        return databaseManager.insertApis(heartbeat, apiName, details);
    }

    private JsonObject removeConfigurationFromResponse(CloseableHttpResponse proxyDetails) {
        JsonObject jsonResponse = HttpUtils.getJsonResponse(proxyDetails);
        jsonResponse.remove("configuration");
        return jsonResponse;
    }
    
    private String getAccessToken(HeartbeatObject heartbeat) {
        String username = System.getProperty("mi_username");
        String password = System.getProperty("mi_password");
        String usernamePassword = username + ":" + password;
        String encodedUsernamePassword = Base64.getEncoder().encodeToString(usernamePassword.getBytes());
        String loginUrl = heartbeat.getMgtApiUrl() + "login";

        final HttpGet httpGet = new HttpGet(loginUrl);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Authorization", "Basic " + encodedUsernamePassword);
        CloseableHttpResponse response = HttpUtils.doGet(httpGet);
        JsonObject jsonResponse = HttpUtils.getJsonResponse(response);

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
}
