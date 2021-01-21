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

package org.wso2.ei.dashboard.core.rest.delegates.heartbeat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.core.rest.model.ProxyList;

import java.util.List;

/**
 * Delegate class to handle requests from proxy-services page.
 */
public class ProxyServicesDelegate {
    private static final Log log = LogFactory.getLog(ProxyServicesDelegate.class);
    private final DatabaseManager databaseManager = DatabaseManagerFactory.getDbManager();

    public ProxyList getProxyServices(String groupId, List<String> nodeList) {
        log.debug("Fetching proxy services from database.");
        return databaseManager.fetchProxyServices(groupId, nodeList);
    }
}
