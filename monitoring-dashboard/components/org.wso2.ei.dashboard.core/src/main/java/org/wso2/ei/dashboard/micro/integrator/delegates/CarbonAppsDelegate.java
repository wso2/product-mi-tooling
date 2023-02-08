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

package org.wso2.ei.dashboard.micro.integrator.delegates;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.ArtifactDelegate;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.ArtifactsResourceResponse;
import org.wso2.ei.dashboard.core.rest.model.CAppArtifacts;
import org.wso2.ei.dashboard.core.rest.model.CAppArtifactsInner;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.util.List;

/**
 * Delegate class to handle requests from carbon application page.
 */
public class CarbonAppsDelegate implements ArtifactDelegate {
    private static final Log logger = LogFactory.getLog(CarbonAppsDelegate.class);
    private final DataManager dataManager = DataManagerSingleton.getDataManager();

    @Override
    public ArtifactsResourceResponse getPaginatedArtifactsResponse(String groupId, List<String> nodeList, 
        String searchKey, String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) 
        throws ManagementApiException {
        logger.debug("Fetching Searched carbon applications from MI.");
        logger.debug("group id :" + groupId + ", lowerlimit :" + lowerLimit + ", upperlimit: " + upperLimit);
        logger.debug("Order:" + order + ", OrderBy:" + orderBy + ", isUpdate:" + isUpdate);
        // changing the keyword before calling Management API and restoring to default value.
        DelegatesUtil.setListKeyName("activeList");
        ArtifactsResourceResponse response = DelegatesUtil.getPaginatedArtifactResponse(groupId, nodeList,
                Constants.CARBON_APPLICATIONS, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
        DelegatesUtil.setListKeyName(Constants.DEFAULT_LIST_KEY);
        return response;
    }

    public CAppArtifacts getCAppArtifactList(String groupId, String nodeId, String cAppName)
            throws ManagementApiException {
        logger.debug("Fetching artifacts in carbon applications from management console");
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("applications").concat("?").concat("carbonAppName").concat("=").concat(cAppName);

        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url);
        JsonObject jsonResponse = HttpUtils.getJsonResponse(httpResponse);
        JsonArray artifacts = jsonResponse.getAsJsonArray("artifacts");
        CAppArtifacts cAppArtifacts = new CAppArtifacts();
        for (JsonElement artifact : artifacts) {
            JsonObject jsonObject = artifact.getAsJsonObject();
            CAppArtifactsInner cAppArtifact = new CAppArtifactsInner();
            cAppArtifact.setName(jsonObject.get("name").getAsString());
            cAppArtifact.setType(jsonObject.get("type").getAsString());
            cAppArtifacts.add(cAppArtifact);
        }
        return cAppArtifacts;
    }

    @Override
    public Ack updateArtifact(String groupId, ArtifactUpdateRequest request) {

        return null;
    }
}
