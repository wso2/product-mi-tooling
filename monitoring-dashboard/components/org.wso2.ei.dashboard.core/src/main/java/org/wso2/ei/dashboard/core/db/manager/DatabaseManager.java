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

package org.wso2.ei.dashboard.core.db.manager;

import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.HeartbeatObject;
import org.wso2.ei.dashboard.core.rest.model.Artifacts;
import org.wso2.ei.dashboard.core.rest.model.GroupList;
import org.wso2.ei.dashboard.core.rest.model.NodeList;

import java.util.List;

/**
 * This interface represents database operations.
 */
public interface DatabaseManager {

    boolean insertHeartbeat(HeartbeatObject heartbeat);

    boolean insertServerInformation(HeartbeatObject heartbeat, String serverInfo);

    boolean insertArtifact(String groupId, String nodeId, String artifactType, String artifactName,
                           String artifactDetails);

    GroupList fetchGroups();

    NodeList fetchNodes(String groupId);

    Artifacts fetchArtifacts(String artifactType, String groupId, List<String> nodeList);

    String getMgtApiUrl(String groupId, String nodeId);

    boolean checkIfTimestampExceedsInitial(HeartbeatObject heartbeat, String initialTimestamp);

    String retrieveTimestampOfHeartBeat(HeartbeatObject heartbeat);

    boolean updateHeartbeat(HeartbeatObject heartbeat);

    boolean updateDetails(String artifactType, String artifactName, String groupId, String nodeId, String details);

    int deleteHeartbeat(HeartbeatObject heartbeat);

    boolean deleteServerInformation(String groupId, String nodeId);

    boolean deleteAllArtifacts(String artifactType, String groupId, String nodeId);

    boolean deleteArtifact(String artifactType, String name, String groupId, String nodeId);
}
