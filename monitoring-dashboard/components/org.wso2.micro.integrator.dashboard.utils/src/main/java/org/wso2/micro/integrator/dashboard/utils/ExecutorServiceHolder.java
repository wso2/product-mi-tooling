/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 */
package org.wso2.micro.integrator.dashboard.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Holds the executor services of the dashboard server.
 */
public class ExecutorServiceHolder {
    private static final int heartbeatPoolSize = Integer.parseInt(System.getProperty(Constants.HEARTBEAT_POOL_SIZE));
    private static ExecutorService miArtifactsManagerExecutorService = Executors.newFixedThreadPool(heartbeatPoolSize);
    private static ExecutorService balArtifactsManagerExecutorService = Executors.newFixedThreadPool(heartbeatPoolSize);
    private static ExecutorService siArtifactsManagerExecutorService = Executors.newFixedThreadPool(heartbeatPoolSize);

    private ExecutorServiceHolder() {
    }

    public static ExecutorService getMiArtifactsManagerExecutorService() {
        return miArtifactsManagerExecutorService;
    }

    public static ExecutorService getBalArtifactsManagerExecutorService() {
        return balArtifactsManagerExecutorService;
    }

    public static ExecutorService getSiArtifactsManagerExecutorService() {
        return siArtifactsManagerExecutorService;
    }
}
