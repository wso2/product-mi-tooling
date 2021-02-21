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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import static org.wso2.ei.dashboard.core.commons.Constants.CARBON_APPLICATIONS;
import static org.wso2.ei.dashboard.core.commons.Constants.CONNECTORS;
import static org.wso2.ei.dashboard.core.commons.Constants.DATA_SERVICES;
import static org.wso2.ei.dashboard.core.commons.Constants.ENDPOINTS;
import static org.wso2.ei.dashboard.core.commons.Constants.INBOUND_ENDPOINTS;
import static org.wso2.ei.dashboard.core.commons.Constants.LOCAL_ENTRIES;
import static org.wso2.ei.dashboard.core.commons.Constants.MESSAGE_PROCESSORS;
import static org.wso2.ei.dashboard.core.commons.Constants.MESSAGE_STORES;
import static org.wso2.ei.dashboard.core.commons.Constants.PROXY_SERVICES;
import static org.wso2.ei.dashboard.core.commons.Constants.SEQUENCES;
import static org.wso2.ei.dashboard.core.commons.Constants.TASKS;
import static org.wso2.ei.dashboard.core.commons.Constants.TEMPLATES;

/**
 * Fetch, store, update and delete artifact information of registered micro integrator nodes.
 */
public class MiArtifactsManager implements ArtifactsManager {
    private static final Logger logger = LogManager.getLogger(MiArtifactsManager.class);
    private static final String SERVER = "server";
    private static final Set<String> ALL_ARTIFACTS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(PROXY_SERVICES, ENDPOINTS, INBOUND_ENDPOINTS, MESSAGE_PROCESSORS,
                                        MESSAGE_STORES, APIS, TEMPLATES, SEQUENCES, TASKS, LOCAL_ENTRIES, CONNECTORS,
                                        CARBON_APPLICATIONS, DATA_SERVICES)));
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
            switch (artifactType) {
                case TEMPLATES:
                    processTemplates(artifactType, artifacts);
                    break;
                default:
                    processArtifacts(accessToken, artifactType, artifacts);
                    break;
            }
        }
        fetchAndStoreServers(accessToken);
    }

    private void processArtifacts(String accessToken, String artifactType, JsonObject artifacts) {
        JsonArray list = artifacts.get("list").getAsJsonArray();
        for (JsonElement element : list) {
            final String artifactName = element.getAsJsonObject().get("name").getAsString();
            JsonObject artifactDetails = new JsonObject();
            if (artifactType.equals(MESSAGE_STORES)) {
                artifactDetails.addProperty("name", artifactName);
                artifactDetails.addProperty("type", element.getAsJsonObject().get("type").getAsString());
                artifactDetails.addProperty("size", element.getAsJsonObject().get("size").getAsString());
            } else {
                artifactDetails = getArtifactDetails(artifactType, artifactName, accessToken);
            }
            insertArtifact(artifactType, artifactName, artifactDetails);
        }
    }

    private void processTemplates(String artifactType, JsonObject artifacts) {
        JsonArray sequences = artifacts.get("sequenceTemplateList").getAsJsonArray();
        JsonArray endpoints = artifacts.get("endpointTemplateList").getAsJsonArray();

        processTemplates(artifactType, sequences, "Sequence Template");
        processTemplates(artifactType, endpoints, "Endpoint Template");
    }

    private void processTemplates(String artifactType, JsonArray templates, String templateType) {
        for (JsonElement template : templates) {
            final String artifactName = template.getAsJsonObject().get("name").getAsString();
            JsonObject artifactDetails = new JsonObject();
            artifactDetails.addProperty("name", artifactName);
            artifactDetails.addProperty("type", templateType);
            insertArtifact(artifactType, artifactName, artifactDetails);
        }
    }

    private void insertArtifact(String artifactType, String artifactName, JsonObject artifactDetails) {
        boolean isSuccess = databaseManager.insertArtifact(heartbeat.getGroupId(), heartbeat.getNodeId(),
                                                           artifactType, artifactName,
                                                           artifactDetails.toString());
        if (!isSuccess) {
            logger.error("Error occurred while adding " + artifactName);
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
            logger.error("Error occurred while adding server details of node: " + heartbeat.getNodeId() + " in group "
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
            case INBOUND_ENDPOINTS:
                getArtifactDetailsUrl = mgtApiUrl.concat(INBOUND_ENDPOINTS).concat("?inboundEndpointName=")
                                                 .concat(artifactName);
                break;
            case MESSAGE_STORES:
                getArtifactDetailsUrl = mgtApiUrl.concat(MESSAGE_STORES).concat("?name=").concat(artifactName);
                break;
            case MESSAGE_PROCESSORS:
                getArtifactDetailsUrl = mgtApiUrl.concat(MESSAGE_PROCESSORS).concat("?name=").concat(artifactName);
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
            case TASKS:
                getArtifactDetailsUrl = mgtApiUrl.concat(TASKS).concat("?taskName=").concat(artifactName);
                break;
            case LOCAL_ENTRIES:
                getArtifactDetailsUrl = mgtApiUrl.concat(LOCAL_ENTRIES).concat("?name=").concat(artifactName);
                break;
            case CONNECTORS:
                getArtifactDetailsUrl = mgtApiUrl.concat(CONNECTORS).concat("?connectorName=").concat(artifactName);
                break;
            case CARBON_APPLICATIONS:
                getArtifactDetailsUrl = mgtApiUrl.concat(CARBON_APPLICATIONS).concat("?carbonAppName=")
                                                 .concat(artifactName);
                break;
            case DATA_SERVICES:
                getArtifactDetailsUrl = mgtApiUrl.concat(DATA_SERVICES).concat("?dataServiceName=")
                                                 .concat(artifactName);
                break;
            default:
                throw new DashboardServerException("Artifact type " + artifactType + " is invalid.");
        }
        CloseableHttpResponse artifactDetails = doGet(accessToken, getArtifactDetailsUrl);
        JsonObject jsonResponse = HttpUtils.getJsonResponse(artifactDetails);
        if (artifactType.equals(CONNECTORS) || artifactType.equals(CARBON_APPLICATIONS)) {
            return jsonResponse;
        } else {
            return removeConfigurationFromResponse(jsonResponse);
        }
    }

    private JsonObject removeConfigurationFromResponse(JsonObject artifact) {
        logger.debug("Removing config from artifact " + artifact.get("name").getAsString());
        artifact.remove("configuration");
        return artifact;
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
