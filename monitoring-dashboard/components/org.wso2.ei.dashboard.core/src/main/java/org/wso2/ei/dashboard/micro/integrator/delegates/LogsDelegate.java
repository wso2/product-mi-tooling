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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.rest.model.LogList;
import org.wso2.ei.dashboard.core.rest.model.LogListInner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Delegate class to handle requests from log files page.
 */
public class LogsDelegate {
    private static final Log log = LogFactory.getLog(LogsDelegate.class);

    public LogList getLogsList(String groupId, List<String> nodeList) {
        log.debug("Fetching logs via management api.");
        LogList logList = new LogList();
        for (String nodeId : nodeList) {
            JsonArray logsArray = getLogsArray(groupId, nodeId);
            for (JsonElement jsonElement : logsArray) {
                JsonObject logObject = jsonElement.getAsJsonObject();
                String fileName = logObject.get("FileName").getAsString();
                AtomicBoolean isRecordExist = new AtomicBoolean(false);
                logList.stream().filter(o -> o.getName().equals(fileName)).forEach(
                        o -> {
                           o.getNodes().add(nodeId);
                           isRecordExist.set(true);
                        });
                if (!isRecordExist.get()) {
                    LogListInner logListInner = new LogListInner();
                    logListInner.setName(fileName);
                    ArrayList<Object> nodes = new ArrayList<>(Collections.singletonList(nodeId));
                    logListInner.setNodes(nodes);
                    logList.add(logListInner);
                }
            }
        }
        return logList;
    }

    private JsonArray getLogsArray(String groupId, String nodeId) {
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        String url = mgtApiUrl.concat("logs");
        CloseableHttpResponse httpResponse = doGet(accessToken, url);
        return HttpUtils.getJsonResponse(httpResponse).getAsJsonArray("list");
    }

    public String getLogByName(String groupId, String nodeId, String fileName) {
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("logs?file=").concat(fileName);
        String accessToken = ManagementApiUtils.getAccessToken(mgtApiUrl);
        CloseableHttpResponse httpResponse = doGet(accessToken, url);
        HttpEntity responseEntity = httpResponse.getEntity();
        String response = "";
        if (responseEntity != null) {
            try {
                response = EntityUtils.toString(responseEntity);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

        }
        return response;
    }

    private CloseableHttpResponse doGet(String accessToken, String url) {
        String authHeader = "Bearer " + accessToken;
        final HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Authorization", authHeader);

        return HttpUtils.doGet(httpGet);
    }
}
