package org.wso2.ei.dashboard.micro.integrator.commons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.model.ArtifactDetails;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.Artifacts;
import org.wso2.ei.dashboard.core.rest.model.ArtifactsInner;
import org.wso2.ei.dashboard.core.rest.model.ArtifactsResourceResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Util class to update artifacts deployed in micro integrator and update the database of the dashboard server.
 */
public class DelegatesUtil {
    private static final DataManager DATA_MANAGER = DataManagerSingleton.getDataManager();

    private static final Logger logger = LogManager.getLogger(DelegatesUtil.class);

    private static List<ArtifactsInner> searchedArtifacts;
    private static String prevSearchKey = null;
    private static String prevResourceType = null;
    private static int count;
    /*
        introducing this variable to dynamically change the keyword of the result list we get from the management API.
        keyword is different only when fetching CAPP details.
    */
    private static String listKeyName = "list";
    public static void setListKeyName(String listKeyName) {
        DelegatesUtil.listKeyName = listKeyName;
    }

    private DelegatesUtil() {

    }

    public static int getNodesCount (String groupId) {
        return DATA_MANAGER.fetchNodes(groupId).size();
    }

    public static int getArtifactCount(List<ArtifactsInner> artifacts) {
        int count = 0;
        List<String> artifactNames = new ArrayList<>();
        String name;
        for (ArtifactsInner artifact : artifacts) {
            name = artifact.getName();
            if (!artifactNames.contains(name)) {
                count++;
                artifactNames.add(name);
            }
        }
        return count;
    }
    

    public static List<ArtifactsInner> getSearchedArtifactsFromMI(String groupId, List<String> nodeList, 
        String artifactType, String searchKey, String order, String orderBy)
            throws ManagementApiException {
        
        Artifacts artifacts = new Artifacts();
     
        for (String nodeId: nodeList) {
            String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
            String accessToken = DATA_MANAGER.getAccessToken(groupId, nodeId);
            
            JsonArray artifactList = getResourceResultList(groupId, nodeId, artifactType, 
                mgtApiUrl, accessToken, searchKey);

            for (JsonElement jsonElement : artifactList) {
                JsonObject artifact = (JsonObject) jsonElement;
                String artifactName = artifact.get("name").getAsString();
                ArtifactDetails artifactDetails = getArtifactDetails(groupId, nodeId, artifactType, artifactName,
                                                                     mgtApiUrl, accessToken);
                AtomicBoolean isRecordExist = new AtomicBoolean(false);
                artifacts.stream().filter(o -> o.getName().equals(artifactName)).forEach(
                        o -> {
                            o.getNodes().add(artifactDetails);
                            isRecordExist.set(true);
                        });
                if (!isRecordExist.get()) {
                    ArtifactsInner artifactsInner = new ArtifactsInner();
                    artifactsInner.setName(artifactName);

                    List<ArtifactDetails> artifactDetailsList = new ArrayList<>();
                    artifactDetailsList.add(artifactDetails);
                    artifactsInner.setNodes(artifactDetailsList);

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
    
    public static ArtifactsResourceResponse getPaginatedArtifactResponse(String groupId, List<String> nodeList, 
        String artifactType, String searchKey, String lowerLimit, String upperLimit, String order, String orderBy,
        String isUpdate)
        throws ManagementApiException {
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        boolean isUpdatedContent = Boolean.parseBoolean(isUpdate);

        ArtifactsResourceResponse artifactsResourceResponse = new ArtifactsResourceResponse();
        logger.debug("prevSearchkey :" + prevSearchKey + ", currentSearchkey:" + searchKey);
        if (isUpdatedContent || prevSearchKey == null || !(prevSearchKey.equals(searchKey)) || 
            !(prevResourceType.equals(artifactType))) {
                
            searchedArtifacts = getSearchedArtifactsFromMI(groupId, nodeList, artifactType,
                    searchKey, order, orderBy);
            count = getArtifactCount(searchedArtifacts);
        }
        Artifacts paginatedList = getPaginationResults(searchedArtifacts, fromIndex, toIndex);
        artifactsResourceResponse.setResourceList(paginatedList);
        artifactsResourceResponse.setCount(count);
        prevSearchKey = searchKey;
        prevResourceType = artifactType;
        return artifactsResourceResponse;
    }
    
     /**
     * Returns the results list items within the given range
     *
     * @param itemsList the list containing all the items of a specific type
     * @param lowerLimit from index of the required range
     * @param upperLimit to index of the required range
     * @return the List if no error. Else return null
     */
    public static Artifacts getPaginationResults(List<ArtifactsInner> itemsList, int lowerLimit, int upperLimit) {
        
        Artifacts resultList = new Artifacts();
        try {
            if (itemsList.size() < upperLimit) {
                upperLimit = itemsList.size();
            }
            if (upperLimit < lowerLimit) {
                lowerLimit = upperLimit;
            }
            List<ArtifactsInner> paginatedList = itemsList.subList(lowerLimit, upperLimit);
        
            for (ArtifactsInner artifact : paginatedList) {
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

    private static ArtifactDetails getArtifactDetails(String groupId, String nodeId, String type, String name,
                                                      String mgtApiUrl, String accessToken)
            throws ManagementApiException {
        JsonObject details = Utils.getArtifactDetails(groupId, nodeId, mgtApiUrl, type, name, accessToken);

        ArtifactDetails artifactDetails = new ArtifactDetails();
        artifactDetails.setNodeId(nodeId);
        artifactDetails.setDetails(details.toString());
        return artifactDetails;
    }

    public static JsonArray getResourceResultList(String groupId, String nodeId, String type, 
        String mgtApiUrl, String accessToken, String searchKey) throws ManagementApiException {
        
        String url = mgtApiUrl.concat(type);
        JsonObject artifacts = invokeManagementApi(groupId, nodeId, url, 
            accessToken, searchKey);
        return artifacts.get(listKeyName).getAsJsonArray();
    }    

    private static JsonObject invokeManagementApi(String groupId, String nodeId, String url, 
        String accessToken, String searchKey)
            throws ManagementApiException {
        CloseableHttpResponse response;
        if (searchKey == null) {
            response = Utils.doGet(groupId, nodeId, accessToken, url);
        } else {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("searchKey", searchKey);
            response = Utils.doGet(groupId, nodeId, accessToken, url, paramMap);
        }
        return HttpUtils.getJsonResponse(response);
    }

    public static boolean updateArtifact(String artifactType, String groupId, ArtifactUpdateRequest request,
                                         JsonObject payload) throws ManagementApiException {
        String nodeId = request.getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);

        if (null != mgtApiUrl && !mgtApiUrl.isEmpty()) {
            String accessToken = DATA_MANAGER.getAccessToken(groupId, nodeId);
            String url = mgtApiUrl.concat(artifactType);
            CloseableHttpResponse response = Utils.doPost(groupId, nodeId, accessToken, url, payload);
            if (response.getStatusLine().getStatusCode() == 200) {
                return true;
            }
        }
        return false;
    }

}
