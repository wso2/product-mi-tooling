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
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.RegistryArtifacts;
import org.wso2.ei.dashboard.core.rest.model.RegistryArtifactsInner;
import org.wso2.ei.dashboard.core.rest.model.RegistryProperty;
import org.wso2.ei.dashboard.core.rest.model.RegistryResourceResponse;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Delegate class to handle requests from registry resources page.
 */
public class RegistryResourceDelegate {
    private static final Logger logger = LogManager.getLogger(RegistryResourceDelegate.class);
    private static final DataManager dataManager = DataManagerSingleton.getDataManager();
    
    /**
     * Returns a list of registry artifacts with their name, metadata and properties.
     * @param groupId Group ID
     * @param searchKey String
     * @param lowerLimit String
     * @param upperLimit String
     * @param order String
     * @param orderBy String
     * @param filePath Parent directory to fetch registry artifacts
     * @return RegistryResourceResponse object containing count and the list of registry artifacts
     * @throws ManagementApiException Error response from the MI Management API
     */
    public RegistryResourceResponse getPaginatedRegistryResponse(String groupId, String searchKey, String lowerLimit, 
        String upperLimit, String order, String orderBy, String filePath) throws ManagementApiException {

        if (searchKey == null) {
            searchKey = "";
        }
        if (order == null) {
            order = "asc";
        }
        if (orderBy == null) {
            orderBy = "name";
        }
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        RegistryArtifacts searchedList = getSearchedRegistryList(groupId, searchKey, order, orderBy, filePath);
        RegistryArtifacts paginatedList = getPaginationResults(searchedList, fromIndex, toIndex);
        RegistryResourceResponse registryResourceResponse = new RegistryResourceResponse();
        registryResourceResponse.setResourceList(paginatedList);
        registryResourceResponse.setCount(searchedList.size());
        return registryResourceResponse;
    }

    public RegistryArtifacts getSearchedRegistryList(String groupId, String searchKey, String order, 
        String orderBy, String filePath) throws ManagementApiException {

        logger.debug("Fetching searched registry resources via management api.");
        
        RegistryArtifacts registryList = new RegistryArtifacts();
        JsonArray registryArray = getSearchedRegistryInPath(groupId, searchKey, filePath);
        if (searchKey.length() == 0) {
            for (JsonElement jsonElement : registryArray) {
                JsonObject registryObject = jsonElement.getAsJsonObject();
                String childName = registryObject.get("name").getAsString();
                String mediaType = registryObject.get("mediaType").getAsString();
                
                RegistryArtifactsInner registryArtifactsInner = new RegistryArtifactsInner();  
                registryArtifactsInner.setChildName(childName);
                registryArtifactsInner.setChildPath("");
                registryArtifactsInner.setMediaType(mediaType);
                registryList.add(registryArtifactsInner);
            }
        } else {
            for (JsonElement jsonElement : registryArray) {
                JsonObject registryObject = jsonElement.getAsJsonObject();
                String mediaType = registryObject.get("type").getAsString();
                String childName = registryObject.get("name").getAsString();
                String path = registryObject.get("path").getAsString();
                RegistryArtifactsInner registryArtifactsInner = new RegistryArtifactsInner();
                
                registryArtifactsInner.setChildName(childName);
                registryArtifactsInner.setChildPath(path); //for clarity of search results
                registryArtifactsInner.setMediaType(mediaType);
                registryList.add(registryArtifactsInner);
            }
        }
        //ordering   
        Comparator<RegistryArtifactsInner> comparatorObject;
        switch (orderBy.toLowerCase()) {
            //for any other ordering options
            default: comparatorObject = Comparator.comparing(RegistryArtifactsInner::getChildNameIgnoreCase); break;
        }
        if ("desc".equalsIgnoreCase(order)) {
            Collections.sort(registryList, comparatorObject.reversed());
        } else {
            Collections.sort(registryList, comparatorObject);
        }
        
        return registryList;
    }

    /* Returns the results list items within the given range
    *
    * @param messageContext the message context to extract the property from
    * @param itemsList the list containing all the items of a specific type
    * @param lowerLimit from index of the required range
    * @param upperLimit to index of the required range
    * @return the List if no error. Else return null
    */
   public static RegistryArtifacts getPaginationResults(RegistryArtifacts itemsList, int lowerLimit, int upperLimit) {
       
       RegistryArtifacts resultList = new RegistryArtifacts();
       try {
           if (itemsList.size() < upperLimit) {
               upperLimit = itemsList.size();
           }
           if (upperLimit < lowerLimit) {
               lowerLimit = upperLimit;
           }
           List<RegistryArtifactsInner> paginatedList = itemsList.subList(lowerLimit, upperLimit);
       
           for (RegistryArtifactsInner artifact : paginatedList) {
               resultList.add(artifact);
           }
           
           return resultList;

       } catch (IndexOutOfBoundsException e) {
           logger.error("Index values are out of bound", e);
       } catch (IllegalArgumentException e) {
           logger.error("Illegal arguments for index values", e);
       }
       return null;
   }

    /**
     * Returns a JSONArray containing registry artifacts match with searchKey from the Management API invocation.
     * @param groupId Group ID
     * @param searchKey String
     * @param filePath Parent directory to fetch registry artifacts
     * @return A JSONArray containing registry artifacts, metadata and properties
     * @throws ManagementApiException Error response from the MI Management API
     */
    public static JsonArray getSearchedRegistryInPath(String groupId, String searchKey, String filePath) 
        throws ManagementApiException {

        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared registry directory.
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("registry-resources");
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("searchKey", searchKey);
        paramMap.put("path", filePath);

        JsonElement responseElement;
        JsonArray registryArray = new JsonArray();
        
        try (CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url, paramMap)) {
            responseElement = HttpUtils.getJsonResponse(httpResponse).get("list");
            if (responseElement.isJsonObject()) { //if true search results, 
                JsonObject responseObject = responseElement.getAsJsonObject();
                if (responseObject.size() > 0) { //else mismatch comes as empty json
                    String path = "";
                    getRegistryArrayFromResponse(responseObject, path, registryArray);
                }
            } else { //empty search key
                registryArray = responseElement.getAsJsonArray();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return registryArray;
    }

    private static void getRegistryArrayFromResponse(JsonObject jsonObject, String path, JsonArray registryArray) {
        String name = jsonObject.get("name").getAsString();
        path += "/" + name;
        JsonArray filesArray = jsonObject.get("files").getAsJsonArray();
        if (filesArray != null) {
            if (filesArray.size() == 0) {
                JsonObject childJsonObject = new JsonObject();
                childJsonObject.addProperty("name", name);
                childJsonObject.addProperty("path", path);
                childJsonObject.addProperty("type", jsonObject.get("type").getAsString());
                registryArray.add(childJsonObject);
            } else {
                for (JsonElement filObject : filesArray) {
                    getRegistryArrayFromResponse(filObject.getAsJsonObject(), path, registryArray);
                }
            }
        }
    }

    /**
     * Returns content of a specified registry artifact as a string.
     * @param groupId Group ID
     * @param filePath File path of the registry artifact
     * @return A string containing the content of the registry artifact
     * @throws ManagementApiException Error response from the MI Management API
     */
    public String getRegistryContent(String groupId, String filePath) throws ManagementApiException {

        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared registry directory.
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("registry-resources/content?path=").concat(filePath);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
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

    /**
     * Returns content of a specified registry artifact as a string.
     * @param groupId Group ID
     * @param filePath File path of the registry artifact
     * @return A string containing the content of the registry artifact
     * @throws ManagementApiException Error response from the MI Management API
     */
    public List<RegistryProperty> getRegistryProperties(String groupId, String filePath) throws ManagementApiException {

        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared registry directory.
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("registry-resources/properties?path=").concat(filePath);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        JsonArray responseArray = new JsonArray();
        List<RegistryProperty> propertyList = new ArrayList<>();

        try (CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url)) {
            JsonElement response = HttpUtils.getJsonResponse(httpResponse).get("list");

            if (response.isJsonArray()) {
                responseArray = response.getAsJsonArray();

                JsonObject jsonObject;
                for (JsonElement jsonElement : responseArray) {
                    RegistryProperty property = new RegistryProperty();
                    jsonObject = jsonElement.getAsJsonObject();
                    property.setPropertyName(jsonObject.get("name").getAsString());
                    property.setPropertyValue(jsonObject.get("value").getAsString());
                    propertyList.add(property);
                }
            }
        } catch (IOException e) {
            logger.error("Unable to get Registry properties", e);
        }

        return propertyList;
    }
}
