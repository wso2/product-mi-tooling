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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.data.manager.InMemoryDataManager;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.ArtifactDelegate;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.Artifacts;
import org.wso2.ei.dashboard.core.rest.model.LocalEntryValue;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * Delegate class to handle requests from local entries page.
 */
public class LocalEntriesDelegate implements ArtifactDelegate {
    private static final Log log = LogFactory.getLog(LocalEntriesDelegate.class);
    private final DataManager dataManager = DataManagerSingleton.getDataManager();

    @Override
    public Artifacts getArtifactsList(String groupId, List<String> nodeList) throws ManagementApiException {
        log.debug("Fetching local entries from MI.");
        return DelegatesUtil.getArtifactsFromMI(groupId, nodeList,  Constants.LOCAL_ENTRIES);
    }

    @Override
    public Ack updateArtifact(String groupId, ArtifactUpdateRequest request) {

        return null;
    }

    public LocalEntryValue getValue(String groupId, String nodeId, String localEntry) throws ManagementApiException {
        log.debug("Fetching value of local entry " + localEntry + " in node " + nodeId + " of group " + groupId);
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        HashMap valueMap = InMemoryDataManager.heartBeatStore.get(groupId + nodeId);
        String accessToken = valueMap.get("accessToken").toString();
        if (localEntry.contains(" ")) {
            localEntry = Utils.encode(localEntry);
        }
        String url = mgtApiUrl.concat("local-entries?name=").concat(localEntry);
        CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url);
        JsonObject jsonResponse = HttpUtils.getJsonResponse(httpResponse);
        String value = jsonResponse.get("value").getAsString();
        LocalEntryValue localEntryValue = new LocalEntryValue();
        localEntryValue.setValue(value);
        return localEntryValue;
    }
}
