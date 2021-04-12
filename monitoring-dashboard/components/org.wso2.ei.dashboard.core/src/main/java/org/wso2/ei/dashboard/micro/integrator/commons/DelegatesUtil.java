package org.wso2.ei.dashboard.micro.integrator.commons;

import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.exception.UnAuthorizedException;
import org.wso2.ei.dashboard.core.rest.delegates.UpdateArtifactObject;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.micro.integrator.MiArtifactsManager;

/**
 * Util class to update artifacts deployed in micro integrator and update the database of the dashboard server.
 */
public class DelegatesUtil {
    private static final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    private static final Logger logger = LogManager.getLogger(DelegatesUtil.class);

    private DelegatesUtil() {

    }

    public static boolean updateArtifact(String artifactType, String groupId, ArtifactUpdateRequest request,
                                         JsonObject payload) throws UnAuthorizedException {
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
                                          ArtifactUpdateRequest request) throws UnAuthorizedException {

        UpdateArtifactObject updateArtifactObject = new UpdateArtifactObject(mgtApiUrl, artifactType,
                                                                             request.getArtifactName(), groupId,
                                                                             request.getNodeId());
        MiArtifactsManager miArtifactsManager = new MiArtifactsManager(updateArtifactObject);
        return miArtifactsManager.updateArtifactDetails();
    }
}
