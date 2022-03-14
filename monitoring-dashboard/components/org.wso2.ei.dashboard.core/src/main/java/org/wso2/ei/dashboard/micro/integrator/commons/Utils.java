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
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;

/**
 * Util class for micro integrator dashboard.
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();
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

    private static String retrieveNewAccessToken(String groupId, String nodeId) throws ManagementApiException {
        logger.debug("Retrieving new access-token from node " + nodeId + " in group " + groupId);
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        databaseManager.updateAccessToken(groupId, nodeId, accessToken);
        return accessToken;
    }

    private static boolean isNotSuccessCode(int httpStatusCode) {
        return httpStatusCode / 100 != 2;
    }
}
