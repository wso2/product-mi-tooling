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

package org.wso2.micro.integrator.dashboard.integration.tests.startup;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.extensions.carbonserver.CarbonTestServerManager;
import org.wso2.esb.integration.common.utils.CarbonLogReader;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.wso2.micro.integrator.dashboard.integration.tests.utils.TestUtils.getNode;

public class ServerStartUpTest {

    private CarbonTestServerManager node;
    private CarbonLogReader logReader;
    private static final String STARTUP_MSG = "WSO2 Micro Integration Monitoring Dashboard started.";
    private static final int TIMEOUT = 60;

    @BeforeClass
    public void initialize() throws Exception {
        node = getNode(0);
        node.startServer();
        String logFilePath =
                node.getCarbonHome() + File.separator + "logs" + File.separator + "wso2carbon.log";
        System.setProperty("logFile", logFilePath);
        logReader = new CarbonLogReader(false);
    }

    @Test
    public void testServerStartup() throws Exception {
        logReader.start();
        Assert.assertTrue(logReader.checkForLog(STARTUP_MSG, TIMEOUT), "Dashboard startup message not found.");
    }

    @Test(dependsOnMethods = "testServerStartup")
    public void testServerErrors() throws Exception {

        node.stopServer();
        TimeUnit.SECONDS.sleep(TIMEOUT);
        logReader.stop();
        Assert.assertFalse(logReader.getLogs().contains("ERROR"), "Dashboard started with errors.");
    }
}
