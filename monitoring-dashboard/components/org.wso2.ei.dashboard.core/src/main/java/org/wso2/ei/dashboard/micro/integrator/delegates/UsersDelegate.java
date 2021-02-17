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
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.UserDetails;
import org.wso2.ei.dashboard.core.rest.model.Users;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Delegate class to handle requests from users page.
 */
public class UsersDelegate {
    private static final Log log = LogFactory.getLog(UsersDelegate.class);
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    public Users fetchUsers(String groupId) {
        log.debug("Fetching users via management api.");
        JsonArray usersList = getUsers(groupId);
        Users users = new Users();
        for (JsonElement user : usersList) {
            users.add(user.getAsJsonObject().get("userId").getAsString());
        }
        return users;
    }

    private JsonArray getUsers(String groupId) {
        NodeList nodeList = databaseManager.fetchNodes(groupId);
        // assumption - In a group, users of all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        String url = mgtApiUrl.concat("users");
        CloseableHttpResponse httpResponse = doGet(accessToken, url);
        return HttpUtils.getJsonResponse(httpResponse).get("list").getAsJsonArray();
    }

    private CloseableHttpResponse doGet(String accessToken, String url) {
        String authHeader = "Bearer " + accessToken;
        final HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpGet.setHeader("Authorization", authHeader);

        return HttpUtils.doGet(httpGet);
    }
}

