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

package org.wso2.ei.dashboard.micro.integrator.commons;

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
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;

import static org.wso2.ei.dashboard.core.commons.Constants.APIS;
import static org.wso2.ei.dashboard.core.commons.Constants.CARBON_APPLICATIONS;
import static org.wso2.ei.dashboard.core.commons.Constants.CONNECTORS;
import static org.wso2.ei.dashboard.core.commons.Constants.DATA_SERVICES;
import static org.wso2.ei.dashboard.core.commons.Constants.DATA_SOURCES;
import static org.wso2.ei.dashboard.core.commons.Constants.ENDPOINTS;
import static org.wso2.ei.dashboard.core.commons.Constants.INBOUND_ENDPOINTS;
import static org.wso2.ei.dashboard.core.commons.Constants.LOCAL_ENTRIES;
import static org.wso2.ei.dashboard.core.commons.Constants.MESSAGE_PROCESSORS;
import static org.wso2.ei.dashboard.core.commons.Constants.MESSAGE_STORES;
import static org.wso2.ei.dashboard.core.commons.Constants.PROXY_SERVICES;
import static org.wso2.ei.dashboard.core.commons.Constants.SEQUENCES;
import static org.wso2.ei.dashboard.core.commons.Constants.TASKS;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Util class for micro integrator dashboard.
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static final DataManager DATA_MANAGER = DataManagerSingleton.getDataManager();
    private static final int HTTP_SC_UNAUTHORIZED = 401;

    public static CloseableHttpResponse doGet(String groupId, String nodeId, String accessToken, String url)
            throws ManagementApiException {
        CloseableHttpResponse response = HttpUtils.doGet(accessToken, url);
        int httpSc = response.getStatusLine().getStatusCode();
        if (response.getStatusLine().getStatusCode() == HTTP_SC_UNAUTHORIZED) {
            accessToken = retrieveNewAccessToken(groupId, nodeId);
            response = HttpUtils.doGet(accessToken, url);
        } else if (isNotSuccessCode(httpSc)) {
            JsonElement error = HttpUtils.getJsonResponse(response).get("Error");
            String errorMessage = "Error occurred. Please check server logs.";
            if (error != null) {
                String message = error.getAsString();
                if (null != message && !message.isEmpty()) {
                    errorMessage = message;
                }
            }
            throw new ManagementApiException(errorMessage, httpSc);
        }
        return response;
    }

    public static CloseableHttpResponse doGet(String groupId, String nodeId, String accessToken, 
        String url, Map<String, String> params)
            throws ManagementApiException {
        CloseableHttpResponse response = HttpUtils.doGet(accessToken, url, params);
        int httpSc = response.getStatusLine().getStatusCode();
        if (response.getStatusLine().getStatusCode() == HTTP_SC_UNAUTHORIZED) {
            accessToken = retrieveNewAccessToken(groupId, nodeId);
            response = HttpUtils.doGet(accessToken, url, params);
        } else if (isNotSuccessCode(httpSc)) {
            JsonElement error = HttpUtils.getJsonResponse(response).get("Error");
            String errorMessage = "Error occurred. Please check server logs.";
            if (error != null) {
                String message = error.getAsString();
                if (null != message && !message.isEmpty()) {
                    errorMessage = message;
                }
            }
            throw new ManagementApiException(errorMessage, httpSc);
        }
        return response;
    }
    public static CloseableHttpResponse doPost(String groupId, String nodeId, String accessToken, String url,
                                               JsonObject payload) throws ManagementApiException {
        CloseableHttpResponse response = HttpUtils.doPost(accessToken, url, payload);
        int httpSc = response.getStatusLine().getStatusCode();
        if (response.getStatusLine().getStatusCode() == HTTP_SC_UNAUTHORIZED) {
            accessToken = retrieveNewAccessToken(groupId, nodeId);
            response = HttpUtils.doPost(accessToken, url, payload);
        } else if (isNotSuccessCode(httpSc)) {
            JsonElement error = HttpUtils.getJsonResponse(response).get("Error");
            String errorMessage = "Error occurred. Please check server logs.";
            if (error != null) {
                String message = error.getAsString();
                if (null != message && !message.isEmpty()) {
                    errorMessage = message;
                }
            }
            throw new ManagementApiException(errorMessage, httpSc);
        }
        return response;
    }

    public static CloseableHttpResponse doPatch(String groupId, String nodeId, String accessToken, String url,
                                                JsonObject payload) throws ManagementApiException {
        CloseableHttpResponse response = HttpUtils.doPatch(accessToken, url, payload);
        int httpSc = response.getStatusLine().getStatusCode();
        if (response.getStatusLine().getStatusCode() == HTTP_SC_UNAUTHORIZED) {
            accessToken = retrieveNewAccessToken(groupId, nodeId);
            response = HttpUtils.doPatch(accessToken, url, payload);
        } else if (isNotSuccessCode(httpSc)) {
            throw new ManagementApiException(response.getStatusLine().getReasonPhrase(), httpSc);
        }
        return response;
    }

    public static CloseableHttpResponse doPut(String groupId, String nodeId, String accessToken, String url,
                                                JsonObject payload) throws ManagementApiException {
        CloseableHttpResponse response = HttpUtils.doPut(accessToken, url, payload);
        int httpSc = response.getStatusLine().getStatusCode();
        if (response.getStatusLine().getStatusCode() == HTTP_SC_UNAUTHORIZED) {
            accessToken = retrieveNewAccessToken(groupId, nodeId);
            response = HttpUtils.doPut(accessToken, url, payload);
        } else if (isNotSuccessCode(httpSc)) {
            throw new ManagementApiException(response.getStatusLine().getReasonPhrase(), httpSc);
        }
        return response;
    }

    public static CloseableHttpResponse doDelete(String groupId, String nodeId, String accessToken, String url)
            throws ManagementApiException {
        CloseableHttpResponse response = HttpUtils.doDelete(accessToken, url);
        int httpSc = response.getStatusLine().getStatusCode();
        if (response.getStatusLine().getStatusCode() == HTTP_SC_UNAUTHORIZED) {
            accessToken = retrieveNewAccessToken(groupId, nodeId);
            response = HttpUtils.doDelete(accessToken, url);
        } else if (isNotSuccessCode(httpSc)) {
            throw new ManagementApiException(response.getStatusLine().getReasonPhrase(), httpSc);
        }
        return response;
    }

    public static JsonObject getArtifactDetails(String groupId, String nodeId, String mgtApiUrl, String artifactType,
                                                String artifactName, String accessToken) throws ManagementApiException {
        String getArtifactDetailsUrl = getArtifactDetailsUrl(mgtApiUrl, artifactType, artifactName);
        CloseableHttpResponse artifactDetails = Utils.doGet(groupId, nodeId, accessToken,
                                                            getArtifactDetailsUrl);
        JsonObject jsonResponse = HttpUtils.getJsonResponse(artifactDetails);
        return removeValueAndConfiguration(artifactType, jsonResponse);
    }

    private static JsonObject removeValueAndConfiguration(String artifactType, JsonObject jsonResponse) {
        if (artifactType.equals(CONNECTORS) || artifactType.equals(CARBON_APPLICATIONS)) {
            return jsonResponse;
        } else if (artifactType.equals(LOCAL_ENTRIES)) {
            return removeValueFromResponse(jsonResponse);
        } else {
            return removeConfigurationFromResponse(jsonResponse);
        }
    }

    private static JsonObject removeConfigurationFromResponse(JsonObject artifact) {
        artifact.remove("configuration");
        return artifact;
    }

    private static JsonObject removeValueFromResponse(JsonObject artifact) {
        artifact.remove("value");
        return artifact;
    }

    private static String getArtifactDetailsUrl(String mgtApiUrl, String artifactType, String artifactName) {

        String getArtifactDetailsUrl;
        String getArtifactsUrl = mgtApiUrl.concat(artifactType);
        if (artifactName.contains(" ")) {
            artifactName = encode(artifactName);
        }
        switch (artifactType) {
            case PROXY_SERVICES:
                getArtifactDetailsUrl = getArtifactsUrl.concat("?proxyServiceName=").concat(artifactName);
                break;
            case ENDPOINTS:
                getArtifactDetailsUrl = getArtifactsUrl.concat("?endpointName=").concat(artifactName);
                break;
            case INBOUND_ENDPOINTS:
                getArtifactDetailsUrl = getArtifactsUrl.concat("?inboundEndpointName=").concat(artifactName);
                break;
            case MESSAGE_STORES:
            case MESSAGE_PROCESSORS:
            case LOCAL_ENTRIES:
            case DATA_SOURCES:
                getArtifactDetailsUrl = getArtifactsUrl.concat("?name=").concat(artifactName);
                break;
            case APIS:
                getArtifactDetailsUrl = getArtifactsUrl.concat("?apiName=").concat(artifactName);
                break;
            case SEQUENCES:
                getArtifactDetailsUrl = getArtifactsUrl.concat("?sequenceName=").concat(artifactName);
                break;
            case TASKS:
                getArtifactDetailsUrl = getArtifactsUrl.concat("?taskName=").concat(artifactName);
                break;
            case CONNECTORS:
                getArtifactDetailsUrl = getArtifactsUrl.concat("?connectorName=").concat(artifactName);
                break;
            case CARBON_APPLICATIONS:
                getArtifactDetailsUrl = getArtifactsUrl.concat("?carbonAppName=").concat(artifactName);
                break;
            case DATA_SERVICES:
                getArtifactDetailsUrl = mgtApiUrl.concat(DATA_SERVICES).concat("?dataServiceName=")
                                                 .concat(artifactName);
                break;
            default:
                throw new DashboardServerException("Artifact type " + artifactType + " is invalid.");
        }
        return getArtifactDetailsUrl;
    }

    /**
     * Translates a string into application/x-www-form-urlencoded format using UTF 8 encoding scheme.
     * @param text String to be encoded
     * @return the translated String
     */
    public static String encode(String text) {
        try {
            return URLEncoder.encode(text, Constants.UTF_8_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new DashboardServerException("Error occurred while encoding the artifact name: " + e.getMessage(),
                                               e.getCause());
        }
    }

    private static String retrieveNewAccessToken(String groupId, String nodeId) throws ManagementApiException {
        logger.debug("Retrieving new access-token from node " + nodeId + " in group " + groupId);
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        DATA_MANAGER.updateAccessToken(groupId, nodeId, accessToken);
        return accessToken;
    }

    private static boolean isNotSuccessCode(int httpStatusCode) {
        return httpStatusCode / 100 != 2;
    }
}
