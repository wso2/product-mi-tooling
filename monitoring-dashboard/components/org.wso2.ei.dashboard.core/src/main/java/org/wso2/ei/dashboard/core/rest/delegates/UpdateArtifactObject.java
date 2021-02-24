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

package org.wso2.ei.dashboard.core.rest.delegates;

/**
 * Update artifact object Class.
 */
public class UpdateArtifactObject {

    private String mgtApiUrl;
    private String type;
    private String name;
    private String groupId;
    private String nodeId;

    public UpdateArtifactObject(String mgtApiUrl, String type, String name, String groupId, String nodeId) {

        this.mgtApiUrl = mgtApiUrl;
        this.type = type;
        this.name = name;
        this.groupId = groupId;
        this.nodeId = nodeId;
    }

    public String getMgtApiUrl() {

        return mgtApiUrl;
    }

    public void setMgtApiUrl(String mgtApiUrl) {

        this.mgtApiUrl = mgtApiUrl;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
    public String getGroupId() {

        return groupId;
    }

    public void setGroupId(String groupId) {

        this.groupId = groupId;
    }

    public String getNodeId() {

        return nodeId;
    }

    public void setNodeId(String nodeId) {

        this.nodeId = nodeId;
    }
}
