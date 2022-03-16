package org.wso2.ei.dashboard.micro.integrator.commons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.UpdateArtifactObject;
import org.wso2.ei.dashboard.core.rest.model.ArtifactDetails;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.Artifacts;
import org.wso2.ei.dashboard.core.rest.model.ArtifactsInner;
import org.wso2.ei.dashboard.micro.integrator.MiArtifactsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Util class to update artifacts deployed in micro integrator and update the database of the dashboard server.
 */
public class DelegatesUtil {
    private static final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    private static final Logger logger = LogManager.getLogger(DelegatesUtil.class);

    private DelegatesUtil() {

    }

    public static Artifacts getArtifactsFromMI(String groupId, List<String> nodeList, String artifactType)
            throws ManagementApiException {
        Artifacts artifacts = new Artifacts();
        for (String nodeId: nodeList) {
            String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
            String accessToken = databaseManager.getAccessToken(groupId, nodeId);

            JsonArray artifactList = getArtifactList(groupId, nodeId, artifactType, mgtApiUrl, accessToken);

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
        return artifacts;
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

    private static JsonArray getArtifactList(String groupId, String nodeId, String type, String mgtApiUrl,
                                             String accessToken) throws ManagementApiException {
        String url = mgtApiUrl.concat(type);
        JsonObject artifacts = invokeManagementApi(groupId, nodeId, url, accessToken);
        return artifacts.get("list").getAsJsonArray();
    }

    private static JsonObject invokeManagementApi(String groupId, String nodeId, String url, String accessToken)
            throws ManagementApiException {
        CloseableHttpResponse response = Utils.doGet(groupId, nodeId, accessToken, url);
        return HttpUtils.getJsonResponse(response);
    }


    public static boolean updateArtifact(String artifactType, String groupId, ArtifactUpdateRequest request,
                                         JsonObject payload) throws ManagementApiException {
        String nodeId = request.getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);

        if (null != mgtApiUrl && !mgtApiUrl.isEmpty()) {
            String accessToken = databaseManager.getAccessToken(groupId, nodeId);
            String url = mgtApiUrl.concat(artifactType);
            CloseableHttpResponse response = Utils.doPost(groupId, nodeId, accessToken, url, payload);
            if (response.getStatusLine().getStatusCode() == 200) {
                return updateDatabase(artifactType, mgtApiUrl, groupId, request);
            }
        }
        return false;
    }

    private static boolean updateDatabase(String artifactType, String mgtApiUrl, String groupId,
                                          ArtifactUpdateRequest request) throws ManagementApiException {

        UpdateArtifactObject updateArtifactObject = new UpdateArtifactObject(mgtApiUrl, artifactType,
                                                                             request.getArtifactName(), groupId,
                                                                             request.getNodeId());
        MiArtifactsManager miArtifactsManager = new MiArtifactsManager(updateArtifactObject);
        return miArtifactsManager.updateArtifactDetails();
    }
}
