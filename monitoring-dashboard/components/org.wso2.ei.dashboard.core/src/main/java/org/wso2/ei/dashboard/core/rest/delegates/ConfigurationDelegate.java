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
import org.apache.http.client.methods.HttpGet;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.model.ModelConfiguration;

/**
 * Delegate class to manage fetch configuration using management api.
 */
public class ConfigurationDelegate {
    private String groupId;
    private String nodeId;
    private String artifactType;
    private String artifactName;

    public ConfigurationDelegate(String groupId, String nodeId, String artifactType, String artifactName) {
        this.groupId = groupId;
        this.nodeId = nodeId;
        this.artifactType = artifactType;
        this.artifactName = artifactName;
    }

    private static final Log log = LogFactory.getLog(ConfigurationDelegate.class);

    public ModelConfiguration getConfiguration() {
        log.debug("Fetching configuration of " + artifactName + " in node " + nodeId + " of group " + groupId);
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        String queryParamName = getQueryParam(artifactType);
        String url = mgtApiUrl.concat(artifactType).concat("?").concat(queryParamName).concat("=").concat(artifactName);
        CloseableHttpResponse httpResponse = doGet(accessToken, url);
        JsonObject jsonResponse = HttpUtils.getJsonResponse(httpResponse);
        String configuration = jsonResponse.get("configuration").getAsString();
        ModelConfiguration modelConfiguration = new ModelConfiguration();
        modelConfiguration.setConfiguration(configuration);
        return modelConfiguration;
    }

    private String getQueryParam(String artifactType) {
        switch (artifactType) {
            case Constants.PROXY_SERVICES:
                return "proxyServiceName";
            case Constants.APIS:
                return "apiName";
            default:
                throw new DashboardServerException("Artifact type " + artifactType + " is invalid.");        }
    }

    private CloseableHttpResponse doGet(String accessToken, String url) {
        String authHeader = "Bearer " + accessToken;
        final HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Authorization", authHeader);

        return HttpUtils.doGet(httpGet);
    }
}
