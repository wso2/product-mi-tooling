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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.AddUserRequest;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.Users;
import org.wso2.ei.dashboard.core.rest.model.UsersInner;

import java.io.UnsupportedEncodingException;

/**
 * Delegate class to handle requests from users page.
 */
public class UsersDelegate {
    private static final Log log = LogFactory.getLog(UsersDelegate.class);
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    public Users fetchUsers(String groupId) {
        log.debug("Fetching users via management api.");
        return getUsers(groupId);
    }

    public Ack addUser(String groupId, AddUserRequest request) {
        log.debug("Adding user " + request.getUserId() + " in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createAddUserPayload(request);

        NodeList nodeList = databaseManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        String url = mgtApiUrl.concat("users");
        CloseableHttpResponse httpResponse = doPost(accessToken, url, payload);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            log.error("Error occurred while adding user in group " + groupId);
            String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
            ack.setMessage(message);
            return ack;
        }
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    public Ack deleteUser(String groupId, String userId) {
        log.debug("Deleting user " + userId + " in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        NodeList nodeList = databaseManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        String url = mgtApiUrl.concat("users/").concat(userId);
        CloseableHttpResponse httpResponse = doDelete(accessToken, url);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            log.error("Error occurred while deleting user " + userId + " in group " + groupId);
            String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
            ack.setMessage(message);
            return ack;
        }
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    private JsonObject createAddUserPayload(AddUserRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("userId", request.getUserId());
        payload.addProperty("password", request.getPassword());
        payload.addProperty("isAdmin", request.isIsAdmin().toString());
        return payload;
    }

    private Users getUsers(String groupId) {
        Users users = new Users();
        NodeList nodeList = databaseManager.fetchNodes(groupId);
        // assumption - In a group, users of all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        String url = mgtApiUrl.concat("users/");
        CloseableHttpResponse httpResponse = doGet(accessToken, url);
        JsonArray userList = HttpUtils.getJsonResponse(httpResponse).get("list").getAsJsonArray();
        for (JsonElement user : userList) {
            UsersInner usersInner = getUserDetails(accessToken, url, user);
            users.add(usersInner);
        }
        return users;
    }

    private UsersInner getUserDetails(String accessToken, String url, JsonElement user) {
        String userId = user.getAsJsonObject().get("userId").getAsString();
        UsersInner usersInner = new UsersInner();
        usersInner.setUserId(userId);
        String getUsersDetails = url.concat(userId);
        CloseableHttpResponse userDetailResponse = doGet(accessToken, getUsersDetails);
        String userDetail = HttpUtils.getStringResponse(userDetailResponse);
        usersInner.setDetails(userDetail);
        return usersInner;
    }

    private CloseableHttpResponse doGet(String accessToken, String url) {
        String authHeader = "Bearer " + accessToken;
        final HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpGet.setHeader("Authorization", authHeader);

        return HttpUtils.doGet(httpGet);
    }

    private CloseableHttpResponse doPost(String accessToken, String url, JsonObject payload) {
        String authHeader = "Bearer " + accessToken;
        final HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Authorization", authHeader);
        httpPost.setHeader("content-type", Constants.HEADER_VALUE_APPLICATION_JSON);

        try {
            StringEntity entity = new StringEntity(payload.toString());
            httpPost.setEntity(entity);
            return HttpUtils.doPost(httpPost);
        } catch (UnsupportedEncodingException e) {
            throw new DashboardServerException("Error occurred while creating http post request.", e);
        }
    }

    private CloseableHttpResponse doDelete(String accessToken, String url) {
        String authHeader = "Bearer " + accessToken;
        final HttpDelete httpDelete = new HttpDelete(url);

        httpDelete.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpDelete.setHeader("Authorization", authHeader);

        return HttpUtils.doDelete(httpDelete);
    }
}
