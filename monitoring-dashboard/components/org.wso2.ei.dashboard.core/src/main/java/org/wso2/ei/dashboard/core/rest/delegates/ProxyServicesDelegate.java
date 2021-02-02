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

package org.wso2.ei.dashboard.core.rest.delegates;

import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.ProxyList;
import org.wso2.ei.dashboard.core.rest.model.ProxyUpdateRequestBody;
import org.wso2.ei.dashboard.micro.integrator.MiArtifactsManager;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Delegate class to handle requests from proxy-services page.
 */
public class ProxyServicesDelegate {
    private static final Log log = LogFactory.getLog(ProxyServicesDelegate.class);
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    public ProxyList getProxyServices(String groupId, List<String> nodeList) {
        log.debug("Fetching proxy services from database.");
        return databaseManager.fetchProxyServices(groupId, nodeList);
    }

    public Ack updateProxyService(String groupId, ProxyUpdateRequestBody payload) {
        log.debug("Updating proxy " + payload.getServiceName() + " in node " + payload.getNodeId() + " in group "
                  + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, payload.getNodeId());
        if (null != mgtApiUrl && !mgtApiUrl.isEmpty()) {
            String url = mgtApiUrl.concat(Constants.PROXY_SERVICES);
            String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
            boolean isSuccess = updateProxyService(mgtApiUrl, url, accessToken, groupId, payload);
            if (isSuccess) {
                ack.setStatus(Constants.SUCCESS_STATUS);
            }
        }
        return ack;
    }

    private boolean updateProxyService(String mgtApiUrl, String url, String accessToken, String groupId,
                                    ProxyUpdateRequestBody payload) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", payload.getServiceName());
        if (payload.getType().equals("isRunning")) {
            String status = payload.isValue() ? "active" : "inactive";
            jsonObject.addProperty("status", status);
        } else if (payload.getType().equals("tracing")) {
            String trace = payload.isValue() ? "enable" : "disable";
            jsonObject.addProperty("trace", trace);
        }
        CloseableHttpResponse response = doPost(url, accessToken, jsonObject);
        if (response.getStatusLine().getStatusCode() == 200) {
            return updateDatabase(mgtApiUrl, groupId, payload);
        } else {
            return false;
        }
    }

    private boolean updateDatabase(String mgtApiUrl, String groupId, ProxyUpdateRequestBody payload) {
        UpdateArtifactObject updateArtifactObject = new UpdateArtifactObject(mgtApiUrl, Constants.PROXY_SERVICES,
                                                                             payload.getServiceName(), groupId,
                                                                             payload.getNodeId());
        MiArtifactsManager miArtifactsManager = new MiArtifactsManager(updateArtifactObject);
        return miArtifactsManager.updateArtifactDetails();
    }

    private CloseableHttpResponse doPost(String url, String accessToken, JsonObject jsonObject) {
        String authHeader = "Bearer " + accessToken;
        final HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Authorization", authHeader);
        httpPost.setHeader("content-type", "application/json");
        try {
            StringEntity entity = new StringEntity(jsonObject.toString());
            httpPost.setEntity(entity);
            return HttpUtils.doPost(httpPost);
        } catch (UnsupportedEncodingException e) {
            throw new DashboardServerException("Error occurred while creating http post request.", e);
        }
    }
}
