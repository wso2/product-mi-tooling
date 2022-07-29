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
import org.wso2.ei.dashboard.core.rest.model.ModelConfiguration;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

/**
 * Delegate class to manage fetch configuration using management api.
 */
public class ConfigurationDelegate {
    private String groupId;
    private String nodeId;
    private String artifactType;
    private String artifactName;

    private static final Logger logger = LogManager.getLogger(ConfigurationDelegate.class);
    private final DataManager dataManager = DataManagerSingleton.getDataManager();

    public ConfigurationDelegate(String groupId, String nodeId, String artifactType, String artifactName) {
        this.groupId = groupId;
        this.nodeId = nodeId;
        this.artifactType = artifactType;
        this.artifactName = artifactName;
    }

    public ModelConfiguration getConfiguration() throws ManagementApiException {
        logger.debug("Fetching configuration of " + artifactName + " in node " + nodeId + " of group " + groupId);
        String type = artifactType.split("_")[0];
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String queryParamName = getQueryParam(artifactType);
        if (artifactName.contains(" ")) {
            artifactName = Utils.encode(artifactName);
        }
        String url = mgtApiUrl.concat(type).concat("?").concat(queryParamName).concat("=").concat(artifactName);

        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url);

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
            case Constants.ENDPOINTS:
                return "endpointName";
            case Constants.INBOUND_ENDPOINTS:
                return "inboundEndpointName";
            case Constants.MESSAGE_STORES:
            case Constants.MESSAGE_PROCESSORS:
            case Constants.DATA_SOURCES:
                return "name";
            case Constants.APIS:
                return "apiName";
            case Constants.ENDPOINT_TEMPLATE:
                return "type=endpoint&name";
            case Constants.SEQUENCE_TEMPLATE:
                return "type=sequence&name";
            case Constants.SEQUENCES:
                return "sequenceName";
            case Constants.TASKS:
                return "taskName";
            case Constants.DATA_SERVICES:
                return "dataServiceName";
            default:
                throw new DashboardServerException("Artifact type " + artifactType + " is invalid.");        }
    }
}
