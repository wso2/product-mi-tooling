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

package org.wso2.ei.dashboard.core.commons.utils;

import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;

import java.util.Base64;

/**
 * Util class for micro integrator management api.
 */
public class ManagementApiUtils {

    private static final Logger logger = LogManager.getLogger(ManagementApiUtils.class);


    private ManagementApiUtils() {

    }

    private static final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    public static String getMgtApiUrl(String groupId, String nodeId) {
        return databaseManager.getMgtApiUrl(groupId, nodeId);
    }

    public static String getAccessToken(String mgtApiUrl) throws ManagementApiException {
        String username = System.getProperty("mi_username");
        String password = System.getProperty("mi_password");
        logger.info("mi_username: " + username);
        logger.info("mi_password: " + password);
        return getToken(mgtApiUrl, username, password);
    }

    public static String getToken(String mgtApiUrl, String username, String password) throws ManagementApiException {
        String usernamePassword = username + ":" + password;
        String encodedUsernamePassword = Base64.getEncoder().encodeToString(usernamePassword.getBytes());
        String loginUrl = mgtApiUrl + "login";
        logger.info("login url: " + loginUrl);

        final HttpGet httpGet = new HttpGet(loginUrl);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Authorization", "Basic " + encodedUsernamePassword);
        logger.info("Authorization header: " + encodedUsernamePassword);
        CloseableHttpResponse response = HttpUtils.doGet(httpGet);

        logger.info("status code received from when requesting token: " + response.getStatusLine().getStatusCode());
        logger.info("reason phrase when requesting token: " + response.getStatusLine().getReasonPhrase());

        int responseCode = response.getStatusLine().getStatusCode();g

        if (responseCode / 100 != 2) {
            logger.error("throwing ManagementApiException: " + response.getStatusLine().getReasonPhrase());
            throw new ManagementApiException(response.getStatusLine().getReasonPhrase(), responseCode);
        }

        JsonObject jsonResponse = HttpUtils.getJsonResponse(response);

        if (jsonResponse.has("AccessToken")) {
            logger.info("jsonResponse received access token: " + jsonResponse.toString());
            return jsonResponse.get("AccessToken").getAsString();
        } else {
            logger.error("jsonResponse when taking access token: " + jsonResponse.getAsString());
            throw new ManagementApiException("Error occurred while retrieving access token from management api.", 500);
        }
    }
}
