/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.RegistryArtifacts;
import org.wso2.ei.dashboard.core.rest.model.RegistryArtifactsInner;
import org.wso2.ei.dashboard.core.rest.model.RegistryProperty;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Delegate class to handle requests from registry resources page.
 */
public class RegistryResourceDelegate {
    private static final Logger logger = LogManager.getLogger(RegistryResourceDelegate.class);
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    /**
     * Returns a list of registry artifacts with their name, metadata and properties.
     * @param groupId Group ID
     * @param filePath Parent directory to fetch registry artifacts
     * @return A list of registry artifacts
     * @throws ManagementApiException Error response from the MI Management API
     */
    public RegistryArtifacts getRegistryList(String groupId, String filePath) throws ManagementApiException {

        logger.debug("Fetching registry resources via management api.");
        RegistryArtifacts registryList = new RegistryArtifacts();
        JsonArray registryArray = getRegistryInPath(groupId, filePath);
        for (JsonElement jsonElement : registryArray) {
            JsonObject registryObject = jsonElement.getAsJsonObject();
            String childName = registryObject.get("name").getAsString();
            String mediaType = registryObject.get("mediaType").getAsString();
            JsonArray propertiesJson = registryObject.get("properties").getAsJsonArray();
            List<RegistryProperty> properties = new ArrayList<>();
            for (JsonElement propertyElement : propertiesJson) {
                RegistryProperty registryProperty = new RegistryProperty();
                JsonObject propertyObject = propertyElement.getAsJsonObject();
                String propertyName = propertyObject.get("name").getAsString();
                String propertyValue = propertyObject.get("value").getAsString();
                registryProperty.setPropertyName(propertyName);
                registryProperty.setPropertyValue(propertyValue);
                properties.add(registryProperty);
            }
            RegistryArtifactsInner registryArtifactsInner = new RegistryArtifactsInner();
            registryArtifactsInner.setChildName(childName);
            registryArtifactsInner.setMediaType(mediaType);
            registryArtifactsInner.setProperties(properties);
            registryList.add(registryArtifactsInner);
        }
        return registryList;
    }

    /**
     * Returns a JSONArray containing registry artifacts from the Management API invocation.
     * @param groupId Group ID
     * @param filePath Parent directory to fetch registry artifacts
     * @return A JSONArray containing registry artifacts, metadata and properties
     * @throws ManagementApiException Error response from the MI Management API
     */
    public JsonArray getRegistryInPath(String groupId, String filePath) throws ManagementApiException {

        NodeList nodeList = databaseManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared registry directory.
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("registry-resources?path=").concat(filePath).concat("&expand=false");
        String accessToken = databaseManager.getAccessToken(groupId, nodeId);
        JsonArray registryArray = new JsonArray();
        try (CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url)) {
            registryArray = HttpUtils.getJsonResponse(httpResponse).getAsJsonArray("list");
            } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return registryArray;
    }

    /**
     * Returns content of a specified registry artifact as a string.
     * @param groupId Group ID
     * @param filePath File path of the registry artifact
     * @return A string containing the content of the registry artifact
     * @throws ManagementApiException Error response from the MI Management API
     */
    public String getRegistryContent(String groupId, String filePath) throws ManagementApiException {

        NodeList nodeList = databaseManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared registry directory.
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("registry-resources/content?path=").concat(filePath);
        String accessToken = databaseManager.getAccessToken(groupId, nodeId);
        String response = "";
        try (CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url)) {
            HttpEntity responseEntity = httpResponse.getEntity();
            if (responseEntity != null) {
                try {
                    response = EntityUtils.toString(responseEntity);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return response;
    }
}
