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
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.ArtifactDelegate;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.ArtifactDetails;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.Artifacts;
import org.wso2.ei.dashboard.core.rest.model.ArtifactsInner;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Delegate class to handle requests from message stores page.
 */
public class MessageStoresDelegate implements ArtifactDelegate {
    private static final Log log = LogFactory.getLog(MessageStoresDelegate.class);
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    @Override
    public Artifacts getArtifactsList(String groupId, List<String> nodeList) throws ManagementApiException {
        // message stores will be fetched from MI to fetch live message store count
        log.debug("Fetching message stores from MI.");
        return getMessageStoresFromMI(groupId, nodeList);
    }

    private Artifacts getMessageStoresFromMI(String groupId, List<String> nodeList) throws ManagementApiException {
        Artifacts artifacts = new Artifacts();
        for (String nodeId: nodeList) {
            JsonArray messageStoreList = getMessageStoreList(groupId, nodeId);
            for (JsonElement ms : messageStoreList) {
                JsonObject messageStore = (JsonObject) ms;

                ArtifactDetails artifactDetail = new ArtifactDetails();
                artifactDetail.setNodeId(nodeId);
                artifactDetail.setDetails(messageStore.toString());

                String messageStoreName = messageStore.get("name").getAsString();

                AtomicBoolean isRecordExist = new AtomicBoolean(false);
                artifacts.stream().filter(o -> o.getName().equals(messageStoreName)).forEach(
                        o -> {
                            o.getNodes().add(artifactDetail);
                            isRecordExist.set(true);
                        });
                if (!isRecordExist.get()) {
                    ArtifactsInner artifactsInner = new ArtifactsInner();
                    artifactsInner.setName(messageStoreName);

                    List<ArtifactDetails> artifactDetailsList = new ArrayList<>();
                    artifactDetailsList.add(artifactDetail);
                    artifactsInner.setNodes(artifactDetailsList);

                    artifacts.add(artifactsInner);
                }
            }
        }
        return artifacts;
    }

    @Override
    public Ack updateArtifact(String groupId, ArtifactUpdateRequest request) {

        return null;
    }

    private JsonArray getMessageStoreList(String groupId, String nodeId) throws ManagementApiException {

        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = databaseManager.getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("message-stores/");
        CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url);
        return HttpUtils.getJsonResponse(httpResponse).get("list").getAsJsonArray();
    }
}
