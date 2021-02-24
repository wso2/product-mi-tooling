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

import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlParseResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This class starting up the jetty server on the port as defined in deployment.toml file.
 * React application serve in root context and all the war files serve in "/api" context.
 */
public class Bootstrap {
    private static final String CONF_DIR = "conf";
    private static final String DEPLOYMENT_TOML = "deployment.toml";
    private static final String TOML_CONF_PORT = "server_config.port";
    private static final String TOML_MI_USERNAME = "mi_user_store.password";
    private static final String TOML_MI_PASSWORD = "mi_user_store.password";
    private static final String MI_USERNAME = "mi_username";
    private static final String MI_PASSWORD = "mi_password";
    private static final String TOML_CONF_HEARTBEAT_POOL_SIZE = "heartbeat_config.pool_size";
    private static final String HEARTBEAT_POOL_SIZE = "heartbeat_pool_size";
    private static final String SERVER_DIR = "server";
    private static final String WEBAPPS_DIR = "webapps";
    private static final String WWW_DIR = "www";
    private static final String WEBAPP_UI = "org.wso2.micro.integrator.dashboard.web.war";
    private static final String DASHBOARD_HOME = "DASHBOARD_HOME";

    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) {

        startServerWithConfigs();
    }

    private static void startServerWithConfigs() {

        String dashboardHome = System.getenv(DASHBOARD_HOME);
        int serverPort = 9743;
        String tomlFile = dashboardHome + File.separator + CONF_DIR + File.separator + DEPLOYMENT_TOML;
        
        try {
            TomlParseResult parseResult = Toml.parse(Paths.get(tomlFile));
            
            Long serverPortConfig = parseResult.getLong(TOML_CONF_PORT);
            if (serverPortConfig != null) {
                serverPort = serverPortConfig.intValue();
            }
            
            loadConfigurations(parseResult);
        } catch (IOException e) {
            logger.warn(
                    String.format("Error while reading TOML file in %s. Using default port %d", tomlFile,
                            serverPort), e);
        }
        
        Server server = new Server();
        setServerConnectors(serverPort, server);
        setServerHandlers(dashboardHome, server);
        
        try {
            server.start();
            writePID(dashboardHome);
            logger.info("Server started in port " + serverPort);
            server.join();
        } catch (Exception ex) {
            logger.error("Error while starting up the server", ex);
        }

        logger.info("Stopping the server");
    }

    private static void setServerConnectors(int serverPort, Server server) {

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(Bootstrap.class.getResource(
                "/dashboard.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("wso2carbon");
        sslContextFactory.setKeyManagerPassword("wso2carbon");

        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(serverPort);

        server.setConnectors(new Connector[] { sslConnector });
    }

    private static void setServerHandlers(String dashboardHome, Server server) {

        String webAppsPath = dashboardHome + File.separator + SERVER_DIR + File.separator + WEBAPPS_DIR;
        File webAppFilePath = new File(webAppsPath);

        HandlerCollection handlers = new HandlerCollection();
        String[] pathnames = webAppFilePath.list();
        for (String pathname : pathnames) {
            WebAppContext webApp = new WebAppContext();
            webApp.setContextPath("/dashboard/");
            File warFile = new File(webAppsPath + File.separator + pathname);
            webApp.setExtractWAR(true);
            webApp.setWar(warFile.getAbsolutePath());
            handlers.addHandler(webApp);
        }
        
        WebAppContext wwwApp = new WebAppContext();
        wwwApp.setContextPath("/");
        wwwApp.setExtractWAR(true);
        wwwApp.setWar(dashboardHome + File.separator + SERVER_DIR + File.separator + WWW_DIR + File.separator
                + WEBAPP_UI);
        wwwApp.setParentLoaderPriority(true);
        handlers.addHandler(wwwApp);
        server.setHandler(handlers);
    }

    private static void loadConfigurations(TomlParseResult parseResult) {
        String heartbeatPoolSize = String.valueOf(5);
        if (parseResult.contains(TOML_CONF_HEARTBEAT_POOL_SIZE)) {
            heartbeatPoolSize = parseResult.getLong(TOML_CONF_HEARTBEAT_POOL_SIZE).toString();
        }
        String miUsername = parseResult.getString(TOML_MI_USERNAME);
        String miPassword = parseResult.getString(TOML_MI_PASSWORD);
        Properties properties = System.getProperties();
        properties.put(HEARTBEAT_POOL_SIZE, heartbeatPoolSize);
        properties.put(MI_USERNAME , miUsername);
        properties.put(MI_PASSWORD, miPassword);
        System.setProperties(properties);
    }

    /**
     * Write the process ID of this process to the file.
     *
     * @param runtimePath DASHBOARD_HOME sys property value.
     */
    private static void writePID(String runtimePath) {
        // Adopted from: https://stackoverflow.com/a/7690178
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int indexOfAt = jvmName.indexOf('@');
        if (indexOfAt < 1) {
            logger.warn("Cannot extract current process ID from JVM name '" + jvmName + "'.");
            return;
        }
        String pid = jvmName.substring(0, indexOfAt);

        Path runtimePidFile = Paths.get(runtimePath, "runtime.pid");
        try {
            Files.write(runtimePidFile, pid.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.warn("Cannot write process ID '" + pid + "' to '" + runtimePidFile.toString() + "' file.", e);
        }
    }
}
