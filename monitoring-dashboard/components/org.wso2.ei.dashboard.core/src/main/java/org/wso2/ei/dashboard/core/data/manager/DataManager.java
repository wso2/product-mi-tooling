/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dashboard.core.data.manager;

import org.wso2.ei.dashboard.core.commons.Constants.Product;
import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.HeartbeatObject;
import org.wso2.ei.dashboard.core.rest.model.GroupList;
import org.wso2.ei.dashboard.core.rest.model.NodeList;

/**
 * This interface represents data related operations.
 */
public interface DataManager {

    boolean insertHeartbeat(HeartbeatObject heartbeat, String accessToken);

    boolean insertServerInformation(HeartbeatObject heartbeat, String serverInfo);

    GroupList fetchGroups();

    NodeList fetchNodes(String groupId);

    NodeList fetchNodes(String groupId, Product product);

    String getMgtApiUrl(String groupId, String nodeId);

    String getAccessToken(String groupId, String nodeId);

    String getHeartbeatInterval(String groupId, String nodeId);

    boolean checkIfTimestampExceedsInitial(HeartbeatObject heartbeat, String initialTimestamp);

    String retrieveTimestampOfLastHeartbeat(String groupId, String nodeId);

    boolean updateHeartbeat(HeartbeatObject heartbeat);

    boolean updateAccessToken(String groupId, String nodeId, String accessToken);

    int deleteHeartbeat(HeartbeatObject heartbeat);

    boolean deleteServerInformation(String groupId, String nodeId);
    
}
