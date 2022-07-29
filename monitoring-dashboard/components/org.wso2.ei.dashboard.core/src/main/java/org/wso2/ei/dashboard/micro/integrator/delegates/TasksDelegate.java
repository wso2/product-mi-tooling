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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.ArtifactDelegate;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.Artifacts;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;

import java.util.List;

/**
 * Delegate class to handle requests from tasks page.
 */
public class TasksDelegate implements ArtifactDelegate {
    private static final Log log = LogFactory.getLog(TasksDelegate.class);

    @Override
    public Artifacts getArtifactsList(String groupId, List<String> nodeList) throws ManagementApiException {
        log.debug("Fetching tasks from MI.");
        return DelegatesUtil.getArtifactsFromMI(groupId, nodeList, Constants.TASKS);
    }

    @Override
    public Ack updateArtifact(String groupId, ArtifactUpdateRequest request) {

        return null;
    }
}
