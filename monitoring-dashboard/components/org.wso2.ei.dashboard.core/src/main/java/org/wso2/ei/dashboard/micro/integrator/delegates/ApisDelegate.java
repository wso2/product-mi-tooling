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

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.ArtifactDelegate;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.ArtifactsResourceResponse;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;

import java.util.List;

/**
 * Delegate class to handle requests from api page.
 */
public class ApisDelegate implements ArtifactDelegate {
    private static final Logger logger = LogManager.getLogger(ApisDelegate.class);

    @Override
    public ArtifactsResourceResponse getPaginatedArtifactsResponse(String groupId, List<String> nodeList,
        String searchKey, String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) 
        throws ManagementApiException {
        logger.debug("Fetching Searched Apis from MI.");
        logger.debug("group id :" + groupId + ", lowerlimit :" + lowerLimit + ", upperlimit: " + upperLimit);
        logger.debug("Order:" + order + ", OrderBy:" + orderBy + ", isUpdate:" + isUpdate);
        return DelegatesUtil.getPaginatedArtifactResponse(groupId, nodeList, Constants.APIS, searchKey, 
            lowerLimit, upperLimit, order, orderBy, isUpdate);
    }

    @Override
    public Ack updateArtifact(String groupId, ArtifactUpdateRequest request) throws ManagementApiException {
        logger.debug("Updating API " + request.getArtifactName() + " in node " + request.getNodeId() + " in group "
                  + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createPayload(request);
        boolean isSuccess = DelegatesUtil.updateArtifact(Constants.APIS, groupId, request, payload);
        if (isSuccess) {
            ack.setStatus(Constants.SUCCESS_STATUS);
        }
        return ack;
    }

    private JsonObject createPayload(ArtifactUpdateRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("name", request.getArtifactName());
        String trace = request.isValue() ? "enable" : "disable";
        payload.addProperty("trace", trace);
        return payload;
    }
}
