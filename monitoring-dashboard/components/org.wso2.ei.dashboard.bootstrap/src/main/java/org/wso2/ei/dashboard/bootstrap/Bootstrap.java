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
package org.wso2.ei.dashboard.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class starting up the jetty server on the port as defined in deployment.toml file.
 * React application serve in root context and all the war files serve in "/api" context.
 */
public class Bootstrap {

    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) {

        if (logger.isDebugEnabled()) {
            logger.debug("Starting up MI Dashboard Server...");
        }

        DashboardServer dashboardServer = new DashboardServer();
        dashboardServer.startServerWithConfigs();
    }


}
