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
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.wso2.config.mapper.ConfigParser;
import org.wso2.config.mapper.ConfigParserException;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private static final String CONFIG_PARSER_DIR = "config-parser";
    private static final String CONF_DIR = "conf";
    private static final String DEPLOYMENT_TOML = "deployment.toml";
    private static final String SECURITY_DIR = "security";
    private static final String KEYSTORE_FILE = "dashboard.jks";
    private static final String TOML_CONF_PORT = "server_config.port";
    private static final String TOML_MI_USERNAME = "mi_user_store.username";
    private static final String TOML_MI_PASSWORD = "mi_user_store.password";
    private static final String MI_USERNAME = "mi_username";
    private static final String MI_PASSWORD = "mi_password";
    private static final String TOML_CONF_HEARTBEAT_POOL_SIZE = "heartbeat_config.pool_size";
    private static final String HEARTBEAT_POOL_SIZE = "heartbeat_pool_size";
    private static final String SERVER_DIR = "server";
    private static final String WEBAPPS_DIR = "webapps";
    private static final String WWW_DIR = "www";
    private static final String DASHBOARD_HOME = "DASHBOARD_HOME";
    private static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";
    private static final String TOML_KEYSTORE_PASSWORD = "keystore.password";    
    private static final String KEY_MANAGER_PASSWORD = "KEY_MANAGER_PASSWORD";
    private static final String TOML_KEY_MANAGER_PASSWORD = "keystore.key_password";
    private static final String JKS_FILE_LOCATION = "JKS_FILE_LOCATION";
    private static final String TOML_JKS_FILE_LOCATION = "keystore.file_name";
    private static String keyStorePassword;
    private static String keyManagerPassword;
    private static String jksFileLocation;
    

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
        setServerConnectors(serverPort, server, dashboardHome);
        setServerHandlers(dashboardHome, server);
        
        try {
            generateSSOConfigJS(tomlFile);
            server.start();
            writePID(dashboardHome);
            printServerStartupLog(serverPort);
            server.join();
        } catch (Exception ex) {
            logger.error("Error while starting up the server", ex);
        }
        logger.info("Stopping the server");
    }

    private static void setServerConnectors(int serverPort, Server server, String dashboardHome) {

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        https.setSendServerVersion(false);
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 2000);

        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        String jksPath =
                dashboardHome + File.separator + jksFileLocation;
        sslContextFactory.setKeyStorePath(jksPath);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);

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
            webApp.setContextPath("/dashboard/*");
            File warFile = new File(webAppsPath + File.separator + pathname);
            webApp.setExtractWAR(true);
            webApp.setWar(warFile.getAbsolutePath());
            ErrorHandler errorHandler = new JsonErrorHandler();
            webApp.setErrorHandler(errorHandler);
            handlers.addHandler(webApp);
        }
        
        WebAppContext wwwApp = new WebAppContext();
        wwwApp.setContextPath("/");
        wwwApp.setResourceBase(dashboardHome + File.separator + SERVER_DIR + File.separator + WWW_DIR);
        wwwApp.setParentLoaderPriority(true);
        handlers.addHandler(wwwApp);
        server.setHandler(handlers);
    }

    private static void printServerStartupLog(int serverPort) {
        InetAddress localHost;
        String hostName;
        try {
            localHost = InetAddress.getLocalHost();
            hostName = localHost.getHostName();
        } catch (UnknownHostException e) {
            hostName = "127.0.0.1";
        }
        String loginUrl = "https://" + hostName + ":" + serverPort + "/login";
        logger.info("WSO2 Micro Integration Monitoring Dashboard started.");
        logger.info("Login to Micro Integrator Dashboard : '" + loginUrl + "'");
    }

    private static void loadConfigurations(TomlParseResult parseResult) {
        String heartbeatPoolSize = String.valueOf(5);
        if (parseResult.contains(TOML_CONF_HEARTBEAT_POOL_SIZE)) {
            heartbeatPoolSize = parseResult.getLong(TOML_CONF_HEARTBEAT_POOL_SIZE).toString();
        }
        Properties properties = System.getProperties();
        properties.put(HEARTBEAT_POOL_SIZE, heartbeatPoolSize);

        String miUsername = System.getProperty(MI_USERNAME);
        if (StringUtils.isEmpty(miUsername)) {
            miUsername = parseResult.getString(TOML_MI_USERNAME);
            properties.put(MI_USERNAME , miUsername);
        }

        String miPassword = System.getProperty(MI_PASSWORD);
        if (StringUtils.isEmpty(miPassword)) {
            miPassword = parseResult.getString(TOML_MI_PASSWORD);
            properties.put(MI_PASSWORD, miPassword);
        }

        keyStorePassword = System.getProperty(KEYSTORE_PASSWORD);
        if (StringUtils.isEmpty(keyStorePassword)) {
            keyStorePassword = parseResult.getString(TOML_KEYSTORE_PASSWORD);
        }        
        
        keyManagerPassword = System.getProperty(KEY_MANAGER_PASSWORD);
        if (StringUtils.isEmpty(keyManagerPassword)) {
            keyManagerPassword = parseResult.getString(TOML_KEY_MANAGER_PASSWORD);
        }

        jksFileLocation = System.getProperty(JKS_FILE_LOCATION);
        if (StringUtils.isEmpty(jksFileLocation)) {
            jksFileLocation = parseResult.getString(TOML_JKS_FILE_LOCATION);
        }
        
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

    private static void generateSSOConfigJS(String tomlPath) throws ConfigParserException {

        String resourcesDir =
                System.getenv(DASHBOARD_HOME) + File.separator + CONF_DIR + File.separator + CONFIG_PARSER_DIR;

        String outputDir =
                System.getenv(DASHBOARD_HOME) + File.separator + SERVER_DIR + File.separator + WWW_DIR +
                        File.separator + CONF_DIR;

        File directory = new File(outputDir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        ConfigParser.parse(tomlPath, resourcesDir, outputDir);
    }
}
