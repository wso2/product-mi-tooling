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
import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.ArtifactsManager;
import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.HeartbeatObject;
import org.wso2.ei.dashboard.core.rest.model.UpdatedArtifact;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fetch, store, update and delete artifact information of registered micro integrator nodes.
 */
public class MiArtifactsManager implements ArtifactsManager {
    private static final Log log = LogFactory.getLog(MiArtifactsManager.class);
    private static final String SERVER = "server";
    private static final String PROXY_SERVICES = "proxy-services";
    private static final String APIS = "apis";
    private static final Set<String> ALL_ARTIFACTS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(PROXY_SERVICES, APIS)));
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();
    private final HeartbeatObject heartbeat;

    public MiArtifactsManager(HeartbeatObject heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Override
    public void runFetchAllExecutorService() {
        ExecutorService fetchExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Runnable runnable = this::fetchAllArtifactsAndStore;
        fetchExecutor.execute(runnable);
    }

    @Override
    public void runUpdateExecutorService() {
        ExecutorService updateExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Runnable runnable = () -> {
           List<UpdatedArtifact> undeployedArtifacts = heartbeat.getUndeployedArtifacts();
           for (UpdatedArtifact artifact : undeployedArtifacts) {
               deleteArtifact(artifact.getType(), artifact.getName());
           }

           List<UpdatedArtifact> deployedArtifacts = heartbeat.getDeployedArtifacts();
           for (UpdatedArtifact info : deployedArtifacts) {
               fetchAndStoreArtifact(info);
           }
        };
        updateExecutor.execute(runnable);
    }

    @Override
    public void runDeleteAllExecutorService() {
        ExecutorService deleteExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Runnable runnable = this::deleteAllArtifacts;
        deleteExecutor.execute(runnable);
    }

    private void fetchAllArtifactsAndStore() {
        String accessToken = getAccessToken(heartbeat);
        for (String artifact : ALL_ARTIFACTS) {
            fetchAndStore(artifact, accessToken);
        }
        fetchAndStoreServers(accessToken);
    }

    private void fetchAndStore(String artifact, String accessToken) {
        switch (artifact) {
            case PROXY_SERVICES:
                fetchAndStoreAllProxyServices(accessToken);
                break;
            case APIS:
                fetchAndStoreAllApis(accessToken);
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

    private void fetchAndStoreAllProxyServices(String accessToken) {
        final String url = heartbeat.getMgtApiUrl() + PROXY_SERVICES;
        CloseableHttpResponse response = doGet(accessToken, url);
        JsonObject proxyServices = HttpUtils.getJsonResponse(response);
        JsonArray serviceList = proxyServices.get("list").getAsJsonArray();
        for (int i = 0; i < serviceList.size(); i++) {
            final String serviceName = serviceList.get(i).getAsJsonObject().get("name").getAsString();
            JsonObject proxyDetails = getArtifactDetails(PROXY_SERVICES, serviceName, accessToken);
            boolean isSuccess = databaseManager.insertArtifact(heartbeat.getGroupId(), heartbeat.getNodeId(),
                                                               PROXY_SERVICES, serviceName, proxyDetails.toString());
            if (!isSuccess) {
                log.error("Error occurred while adding " + serviceName + " proxy details");
                addToDelayedQueue();
            }
        }
    }

    private void fetchAndStoreAllApis(String accessToken) {
        String url = heartbeat.getMgtApiUrl() + APIS;
        CloseableHttpResponse response = doGet(accessToken, url);
        JsonObject apis = HttpUtils.getJsonResponse(response);
        int apiCount = apis.get("count").getAsInt();
        if (apiCount > 0) {
            JsonArray apiList = apis.get("list").getAsJsonArray();
            for (int i = 0; i < apiCount; i++) {
                String apiName = apiList.get(i).getAsJsonObject().get("name").getAsString();
                JsonObject apiDetails = getArtifactDetails(APIS, apiName, accessToken);
                boolean isSuccess = databaseManager.insertArtifact(heartbeat.getGroupId(), heartbeat.getNodeId(), APIS,
                                                                   apiName, apiDetails.toString());
                if (!isSuccess) {
                    log.error("Error occurred while adding " + apiName + " api details");
                    addToDelayedQueue();
                }
            }
        }
    }

    private void fetchAndStoreArtifact(UpdatedArtifact info) {
        String accessToken = getAccessToken(heartbeat);
        String artifactType = info.getType();
        String artifactName = info.getName();
        JsonObject artifactDetails = getArtifactDetails(artifactType, artifactName, accessToken);
        databaseManager.insertArtifact(heartbeat.getGroupId(), heartbeat.getNodeId(), artifactType, artifactName,
                                       artifactDetails.toString());
    }

    private JsonObject getArtifactDetails(String artifactType, String artifactName, String accessToken) {
        final String mgtApiUrl = heartbeat.getMgtApiUrl();
        String getArtifactDetailsUrl;
        switch (artifactType) {
            case PROXY_SERVICES:
                getArtifactDetailsUrl = mgtApiUrl.concat(PROXY_SERVICES).concat("?proxyServiceName=")
                                                 .concat(artifactName);
                break;
            case APIS:
                getArtifactDetailsUrl = mgtApiUrl.concat(APIS).concat("?apiName=").concat(artifactName);
                break;
            default:
                throw new DashboardServerException("Artifact type " + artifactType + " is invalid.");
        }
        CloseableHttpResponse artifactDetails = doGet(accessToken, getArtifactDetailsUrl);
        return removeConfigurationFromResponse(artifactDetails);
    }

    private JsonObject removeConfigurationFromResponse(CloseableHttpResponse proxyDetails) {
        JsonObject jsonResponse = HttpUtils.getJsonResponse(proxyDetails);
        jsonResponse.remove("configuration");
        return jsonResponse;
    }

    private void deleteArtifact(String artifactType, String name) {
        databaseManager.deleteArtifact(artifactType, name, heartbeat.getGroupId(), heartbeat.getNodeId());
    }

    private void deleteAllArtifacts() {
        String groupId = heartbeat.getGroupId();
        String nodeId = heartbeat.getNodeId();
        databaseManager.deleteServerInformation(groupId, nodeId);
        for (String artifact : ALL_ARTIFACTS) {
            databaseManager.deleteAllArtifacts(artifact, groupId, nodeId);
        }
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

    private void addToDelayedQueue() {
        // todo
    }
}
