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

import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.delegates.UpdateArtifactObject;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;

import java.io.UnsupportedEncodingException;

/**
 * Util class to update artifacts deployed in micro integrator and update the database of the dashboard server.
 */
public class DelegatesUtil {

    private DelegatesUtil() {

    }

    public static boolean updateArtifact(String artifactType, String groupId, ArtifactUpdateRequest request,
                                  JsonObject payload) {
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, request.getNodeId());

        if (null != mgtApiUrl && !mgtApiUrl.isEmpty()) {
            CloseableHttpResponse response = doPost(artifactType, mgtApiUrl, payload);
            if (response.getStatusLine().getStatusCode() == 200) {
                return updateDatabase(artifactType, mgtApiUrl, groupId, request);
            }
        }
        return false;
    }

    private static boolean updateDatabase(String artifactType, String mgtApiUrl, String groupId,
                                          ArtifactUpdateRequest request) {
        UpdateArtifactObject updateArtifactObject = new UpdateArtifactObject(mgtApiUrl, artifactType,
                                                                             request.getArtifactName(), groupId,
                                                                             request.getNodeId());
        MiArtifactsManager miArtifactsManager = new MiArtifactsManager(updateArtifactObject);
        return miArtifactsManager.updateArtifactDetails();
    }

    private static CloseableHttpResponse doPost(String artifactType, String mgtApiUrl, JsonObject payload) {
        String url = mgtApiUrl.concat(artifactType);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        String authHeader = "Bearer " + accessToken;
        final HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Authorization", authHeader);
        httpPost.setHeader("content-type", "application/json");
        try {
            StringEntity entity = new StringEntity(payload.toString());
            httpPost.setEntity(entity);
            return HttpUtils.doPost(httpPost);
        } catch (UnsupportedEncodingException e) {
            throw new DashboardServerException("Error occurred while creating http post request.", e);
        }
    }

}
