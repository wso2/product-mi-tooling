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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.ArtifactDelegate;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.ArtifactDetails;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.Artifacts;
import org.wso2.ei.dashboard.core.rest.model.ArtifactsInner;
import org.wso2.ei.dashboard.core.rest.model.ArtifactsResourceResponse;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Delegate class to handle requests from templates page.
 */
public class TemplatesDelegate implements ArtifactDelegate {
    private static final Logger logger = LogManager.getLogger(TemplatesDelegate.class);
    private static final DataManager DATA_MANAGER = DataManagerSingleton.getDataManager();
    private static List<ArtifactsInner> searchedList;
    private static String prevSearchKey = null;
    private static int count;

    @Override
    public ArtifactsResourceResponse getPaginatedArtifactsResponse(String groupId, List<String> nodeList, 
        String searchKey, String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) 
        throws ManagementApiException {
        logger.debug("Fetching Searched Templates from MI.");
        logger.debug("group id :" + groupId + ", lowerlimit :" + lowerLimit + ", upperlimit: " + upperLimit);
        logger.debug("Order:" + order + ", OrderBy:" + orderBy + ", isUpdate:" + isUpdate);
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        boolean isUpdatedContent = Boolean.parseBoolean(isUpdate);
        if (logger.isDebugEnabled()) {
            logger.debug("prevSearchkey :" + prevSearchKey + ", currentSearchkey:" + searchKey);
        }
        if (isUpdatedContent || prevSearchKey == null || !(prevSearchKey.equals(searchKey))) {
            searchedList = getSearchedTemplatesResultsFromMI(groupId, nodeList, 
                searchKey, order, orderBy);
            count = DelegatesUtil.getArtifactCount(searchedList);
        }
        Artifacts paginatedList = DelegatesUtil.getPaginationResults(searchedList, fromIndex, toIndex);
        ArtifactsResourceResponse artifactsResourceResponse = new ArtifactsResourceResponse();
        artifactsResourceResponse.setResourceList(paginatedList);
        artifactsResourceResponse.setCount(count);
        prevSearchKey = searchKey;
        return artifactsResourceResponse;
    }

     
    public static List<ArtifactsInner> getSearchedTemplatesResultsFromMI(String groupId, List<String> nodeList, 
       String searchKey, String order, String orderBy)
            throws ManagementApiException {
        
        Artifacts artifacts = new Artifacts();

        for (String nodeId: nodeList) {

            String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
            String accessToken = DATA_MANAGER.getAccessToken(groupId, nodeId);
            
            JsonArray artifactList = DelegatesUtil.getResourceResultList(groupId, nodeId, Constants.TEMPLATES,
                mgtApiUrl, accessToken, searchKey);
            

            for (JsonElement jsonElement : artifactList) {
                JsonObject artifact = (JsonObject) jsonElement;
                String templateName = artifact.get("name").getAsString();
                String templateType = artifact.get("type").getAsString();
                
                ArtifactDetails artifactDetails = getTemplateDetails(groupId, nodeId, templateType, templateName,
                                                                     mgtApiUrl, accessToken);
                                                                     
                AtomicBoolean isRecordExist = new AtomicBoolean(false);
                artifacts.stream().filter(o -> (o.getName()).equals(templateName)).forEach(
                        o -> {
                            o.getNodes().add(artifactDetails);
                            isRecordExist.set(true);
                        });
                if (!isRecordExist.get()) {
                    ArtifactsInner artifactsInner = new ArtifactsInner();
                    artifactsInner.setName(templateName);
                    List<ArtifactDetails> artifactDetailsList = new ArrayList<>();
                    artifactDetailsList.add(artifactDetails);
                    artifactsInner.setNodes(artifactDetailsList);
                    artifactsInner.setType(templateType);
                    artifacts.add(artifactsInner);
                }
            }
        }

        //ordering   
        Comparator<ArtifactsInner> comparatorObject;
        switch (orderBy.toLowerCase()) {
            //for any other ordering options
            default: comparatorObject = Comparator.comparing(ArtifactsInner::getNameIgnoreCase); break;
        }
        if ("desc".equalsIgnoreCase(order)) {
            Collections.sort(artifacts, comparatorObject.reversed());
        } else {
            Collections.sort(artifacts, comparatorObject);
        }
        return artifacts;
    }

    private static ArtifactDetails getTemplateDetails(String groupId, String nodeId, String type, String name,
            String mgtApiUrl, String accessToken)
        throws ManagementApiException {
        
        JsonObject details = getArtifactDetails(groupId, nodeId, mgtApiUrl, type, name, accessToken);
        ArtifactDetails artifactDetails = new ArtifactDetails();
        artifactDetails.setNodeId(nodeId);
        artifactDetails.setDetails(details.toString());
        return artifactDetails;
    }

    private static JsonObject getArtifactDetails(String groupId, String nodeId, String mgtApiUrl, String templateType,
                                                String templateName, String accessToken) throws ManagementApiException {
        
        String getArtifactDetailsUrl = mgtApiUrl.concat(Constants.TEMPLATES).concat("?type=").
            concat(templateType).concat("&name=").concat(templateName);
        CloseableHttpResponse artifactDetails = Utils.doGet(groupId, nodeId, accessToken,
                                                            getArtifactDetailsUrl);
        JsonObject jsonResponse = HttpUtils.getJsonResponse(artifactDetails);
        jsonResponse.remove("configuration");
        return jsonResponse;
    }

    @Override
    public Ack updateArtifact(String groupId, ArtifactUpdateRequest request) {

        return null;
    }
}
