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
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.delegates.ArtifactsManager;
import org.wso2.ei.dashboard.core.rest.delegates.UpdateArtifactObject;
import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.HeartbeatObject;
import org.wso2.ei.dashboard.core.rest.model.UpdatedArtifact;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.wso2.ei.dashboard.core.commons.Constants.APIS;
import static org.wso2.ei.dashboard.core.commons.Constants.ENDPOINTS;
import static org.wso2.ei.dashboard.core.commons.Constants.PROXY_SERVICES;
import static org.wso2.ei.dashboard.core.commons.Constants.SEQUENCES;
import static org.wso2.ei.dashboard.core.commons.Constants.TEMPLATES;

/**
 * Fetch, store, update and delete artifact information of registered micro integrator nodes.
 */
public class MiArtifactsManager implements ArtifactsManager {
    private static final Log log = LogFactory.getLog(MiArtifactsManager.class);
    private static final String SERVER = "server";
    private static final Set<String> ALL_ARTIFACTS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(PROXY_SERVICES, ENDPOINTS, APIS, TEMPLATES, SEQUENCES)));
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();
    private HeartbeatObject heartbeat = null;
    private UpdateArtifactObject updateArtifactObject = null;

    public MiArtifactsManager(UpdateArtifactObject updateArtifactObject) {
        this.updateArtifactObject = updateArtifactObject;
    }

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
        String accessToken = ManagementApiUtils.getAccessToken(heartbeat.getMgtApiUrl());
        for (String artifactType : ALL_ARTIFACTS) {
            final String url = heartbeat.getMgtApiUrl().concat(artifactType);
            CloseableHttpResponse response = doGet(accessToken, url);
            JsonObject artifacts = HttpUtils.getJsonResponse(response);
            if (artifactType.equals(TEMPLATES)) {
                JsonArray sequences = artifacts.get("sequenceTemplateList").getAsJsonArray();
                JsonArray endpoints = artifacts.get("endpointTemplateList").getAsJsonArray();

                for (int i = 0; i < sequences.size(); i++) {
                    final String artifactName = sequences.get(i).getAsJsonObject().get("name").getAsString();
                    JsonObject artifactDetails = new JsonObject();
                    artifactDetails.addProperty("name", artifactName);
                    artifactDetails.addProperty("type", "Sequence Template");
                    insertArtifact(artifactType, artifactName, artifactDetails);
                }

                for (int i = 0; i < endpoints.size(); i++) {
                    final String artifactName = endpoints.get(i).getAsJsonObject().get("name").getAsString();
                    JsonObject artifactDetails = new JsonObject();
                    artifactDetails.addProperty("name", artifactName);
                    artifactDetails.addProperty("type", "Endpoint Template");
                    insertArtifact(artifactType, artifactName, artifactDetails);
                }

            } else {
                JsonArray list = artifacts.get("list").getAsJsonArray();
                for (int i = 0; i < list.size(); i++) {
                    final String artifactName = list.get(i).getAsJsonObject().get("name").getAsString();
                    JsonObject artifactDetails = getArtifactDetails(artifactType, artifactName, accessToken);
                    insertArtifact(artifactType, artifactName, artifactDetails);
                }
            }
        }
        fetchAndStoreServers(accessToken);
    }

    private void insertArtifact(String artifactType, String artifactName, JsonObject artifactDetails) {
        boolean isSuccess = databaseManager.insertArtifact(heartbeat.getGroupId(), heartbeat.getNodeId(),
                                                           artifactType, artifactName,
                                                           artifactDetails.toString());
        if (!isSuccess) {
            log.error("Error occurred while adding " + artifactName);
            addToDelayedQueue();
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

    public boolean updateArtifactDetails() {
        if (updateArtifactObject != null) {
            String mgtApiUrl = updateArtifactObject.getMgtApiUrl();
            String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
            String artifactType = updateArtifactObject.getType();
            String artifactName = updateArtifactObject.getName();
            JsonObject details = getArtifactDetails(mgtApiUrl, artifactType, artifactName,
                                                    accessToken);
            return databaseManager.updateDetails(artifactType, artifactName, updateArtifactObject.getGroupId(),
                                          updateArtifactObject.getNodeId(), details.toString());
        } else {
            throw new DashboardServerException("Artifact details are invalid");
        }
    }

    private void fetchAndStoreArtifact(UpdatedArtifact info) {
        String accessToken = ManagementApiUtils.getAccessToken(heartbeat.getMgtApiUrl());
        String artifactType = info.getType();
        String artifactName = info.getName();
        JsonObject artifactDetails = getArtifactDetails(artifactType, artifactName, accessToken);
        databaseManager.insertArtifact(heartbeat.getGroupId(), heartbeat.getNodeId(), artifactType, artifactName,
                                       artifactDetails.toString());
    }

    private JsonObject getArtifactDetails(String artifactType, String artifactName, String accessToken) {
        final String mgtApiUrl = heartbeat.getMgtApiUrl();
        return getArtifactDetails(mgtApiUrl, artifactType, artifactName, accessToken);
    }

    private JsonObject getArtifactDetails(String mgtApiUrl, String artifactType, String artifactName,
                                          String accessToken) {
        String getArtifactDetailsUrl;
        switch (artifactType) {
            case PROXY_SERVICES:
                getArtifactDetailsUrl = mgtApiUrl.concat(PROXY_SERVICES).concat("?proxyServiceName=")
                                                 .concat(artifactName);
                break;
            case ENDPOINTS:
                getArtifactDetailsUrl = mgtApiUrl.concat(ENDPOINTS).concat("?endpointName=").concat(artifactName);
                break;
            case APIS:
                getArtifactDetailsUrl = mgtApiUrl.concat(APIS).concat("?apiName=").concat(artifactName);
                break;
            case TEMPLATES:
                getArtifactDetailsUrl = mgtApiUrl.concat(TEMPLATES).concat("?templateName=").concat(artifactName);
                break;
            case SEQUENCES:
                getArtifactDetailsUrl = mgtApiUrl.concat(SEQUENCES).concat("?sequenceName=").concat(artifactName);
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
