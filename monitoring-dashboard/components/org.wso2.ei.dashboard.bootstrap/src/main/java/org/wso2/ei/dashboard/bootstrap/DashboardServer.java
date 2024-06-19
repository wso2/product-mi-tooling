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

package org.wso2.ei.dashboard.bootstrap;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import io.asgardeo.java.oidc.sdk.config.model.OIDCAgentConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.DetectorConnectionFactory;
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
import org.wso2.carbon.securevault.SecretManagerInitializer;
import org.wso2.config.mapper.ConfigParser;
import org.wso2.config.mapper.ConfigParserException;
import org.wso2.micro.integrator.dashboard.utils.Constants;
import org.wso2.micro.integrator.dashboard.utils.ExecutorServiceHolder;
import org.wso2.micro.integrator.dashboard.utils.SSOConfig;
import org.wso2.micro.integrator.dashboard.utils.SSOConfigException;
import org.wso2.micro.integrator.dashboard.utils.SSOConstants;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;
import org.wso2.securevault.commons.MiscellaneousUtil;
import org.wso2.securevault.secret.SecretCallbackHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.DispatcherType;

/**
 * This class starting up the jetty server on the port as defined in deployment.toml file.
 * React application serve in root context and all the war files serve in "/api" context.
 */
public class DashboardServer {
    private static final String CONF_DIR = "conf";
    private static final String MI_REPOSITORY_DIR = "repository";
    private static final String MI_RESOURCE_DIR = "resources";
    private static final String DEPLOYMENT_TOML = "deployment.toml";
    private static final String SECURITY_DIR = "security";
    private static final String KEYSTORE_FILE = "dashboard.jks";
    private static final String TOML_CONF_PORT = "server_config.port";
    private static final String TOML_MI_USERNAME = "mi_super_admin.username";
    private static final String TOML_MI_PASSWORD = "mi_super_admin.password";
    private static final String MI_USERNAME = "mi_username";
    private static final String MI_PASSWORD = "mi_password";
    private static final String TOML_BAL_SERVICE_USERNAME = "bal_service_account.username";
    private static final String TOML_BAL_SERVICE_PASSWORD = "bal_service_account.password";
    private static final String BAL_USERNAME = "bal_username";
    private static final String BAL_PASSWORD = "bal_password";
//    private static final String TOML_USER_STORE_TYPE = "user_store.type";
    private static final String TOML_CONF_HEARTBEAT_POOL_SIZE = "heartbeat_config.pool_size";
    private static final String SERVER_DIR = "server";
    private static final String WEBAPPS_DIR = "webapps";
    private static final String WWW_DIR = "www";
    private static final String DASHBOARD_HOME = System.getProperty("dashboard.home");
    private static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";
    private static final String TOML_KEYSTORE_PASSWORD = "keystore.password";
    private static final String KEY_MANAGER_PASSWORD = "KEY_MANAGER_PASSWORD";
    private static final String TOML_KEY_MANAGER_PASSWORD = "keystore.key_password";
    private static final String JKS_FILE_LOCATION = "JKS_FILE_LOCATION";
    private static final String TOML_JKS_FILE_LOCATION = "keystore.file_name";
    private static final String TOML_TRUSTSTORE_PASSWORD = "truststore.password";
    private static final String TOML_TRUSTSTORE_FILE_LOCATION = "truststore.file_name";
    private static final String JAVAX_SSL_TRUSTSTORE = "javax.net.ssl.trustStore";
    private static final String JAVAX_SSL_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";
    private static final String CARBON_HOME = "carbon.home";
    private static final String SECRET_CONF = "secret-conf.properties";
    private static final String CARBON_CONFIG_DIR = "carbon.config.dir.path";
//    private static final String USER_STORE_TYPE = "user_store_type";
    private static final String FILE_BASED_USER_STORE_ENABLE = "internal_apis.file_user_store.enable";
    private static final String IS_USER_STORE_FILE_BASED = "is.user.store.file.based";
    private static final int EXECUTOR_SERVICE_TERMINATION_TIMEOUT = 5000;
    private static final int DEFAULT_HEARTBEAT_POOL_SIZE = 10;
    private static String keyStorePassword;
    private static String keyManagerPassword;
    private static String jksFileLocation;
    private static SSOConfig ssoConfig;
    private static Thread shutdownHook;
    private static SecretResolver secretResolver = new SecretResolver();

    private static final Logger logger = LogManager.getLogger(DashboardServer.class);

    public void startServerWithConfigs() {

        int serverPort = 9743;
        String tomlFile = DASHBOARD_HOME + File.separator + CONF_DIR + File.separator + DEPLOYMENT_TOML;

        try {
            Map<String, Object> parsedConfigs = parseConfigJS(tomlFile);

            Object serverPortConfig = parsedConfigs.get(TOML_CONF_PORT);
            if (serverPortConfig instanceof Long) {
                serverPort = ((Long) serverPortConfig).intValue();
            }

            initSecureVault(parsedConfigs);
            loadConfigurations(parsedConfigs);
            ssoConfig = generateSSOConfig(parsedConfigs);
        } catch (SSOConfigException e) {
            logger.error("Error reading SSO configs from TOML file", e);
            System.exit(1);
        } catch (ConfigParserException e) {
            logger.error("Error while reading TOML file configs", e);
        }

        Server server = new Server();
        setServerConnectors(serverPort, server, DASHBOARD_HOME);
        setServerHandlers(DASHBOARD_HOME, server);
        addShutdownHook();

        try {
            server.start();
            writePID(DASHBOARD_HOME);
            printServerStartupLog(serverPort);
            server.join();
        } catch (Exception ex) {
            logger.error("Error while starting up the server", ex);
        }
        logger.info("Stopping the server");
    }

    private void addShutdownHook() {
        if (shutdownHook != null) {
            return;
        }
        shutdownHook = new Thread(() -> {
            logger.debug("Shutdown hook triggered....");
            shutdownGracefully();
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private void shutdownGracefully() {
        if (logger.isDebugEnabled()) {
            logger.debug("Shutting down MI Dashboard Server...");
        }
        ExecutorService executorService = ExecutorServiceHolder.getMiArtifactsManagerExecutorService();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(EXECUTOR_SERVICE_TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private void setServerConnectors(int serverPort, Server server, String dashboardHome) {

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        https.setSecureScheme("https");
        https.setSecurePort(serverPort);
        https.setSendServerVersion(false);
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 2000);

        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        String jksPath =
                dashboardHome + File.separator + jksFileLocation;
        sslContextFactory.setKeyStorePath(jksPath);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);

        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, "http/1.1");

        ServerConnector sslConnector = new ServerConnector(server,
                new DetectorConnectionFactory(sslConnectionFactory),
                new HttpConnectionFactory(https));
        sslConnector.setPort(serverPort);

        server.setConnectors(new Connector[] { sslConnector });
    }

    private void setServerHandlers(String dashboardHome, Server server) {

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
            webApp.setAttribute(SSOConstants.CONFIG_BEAN_NAME, ssoConfig);
            ErrorHandler errorHandler = new JsonErrorHandler();
            webApp.setErrorHandler(errorHandler);
            handlers.addHandler(webApp);
        }

        WebAppContext wwwApp = new WebAppContext();
        wwwApp.setContextPath("/");
        wwwApp.setResourceBase(dashboardHome + File.separator + SERVER_DIR + File.separator + WWW_DIR);
        wwwApp.setParentLoaderPriority(true);
        wwwApp.addFilter(CSRFFilter.class, "/login", EnumSet.of(DispatcherType.REQUEST));
        wwwApp.addFilter(SecurityHeaderFilter.class, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
        wwwApp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        handlers.addHandler(wwwApp);
        server.setHandler(handlers);
    }

    private void printServerStartupLog(int serverPort) {
        InetAddress localHost;
        String hostName;
        try {
            localHost = InetAddress.getLocalHost();
            hostName = localHost.getHostName();
        } catch (UnknownHostException e) {
            hostName = "127.0.0.1";
        }
        String loginUrl = "https://" + hostName + ":" + serverPort + "/login";
        logger.info("WSO2 Integration Control Plane started.");
        logger.info("Login to Integration Control Plane Dashboard : '" + loginUrl + "'");
    }

    private void loadConfigurations(Map<String, Object> parsedConfigs) {
        String heartbeatPoolSize = String.valueOf(DEFAULT_HEARTBEAT_POOL_SIZE);
        if (parsedConfigs.containsKey(TOML_CONF_HEARTBEAT_POOL_SIZE)) {
            heartbeatPoolSize = ((Long) parsedConfigs.get(TOML_CONF_HEARTBEAT_POOL_SIZE)).toString();
        }
        Properties properties = System.getProperties();
        properties.put(Constants.HEARTBEAT_POOL_SIZE, heartbeatPoolSize);

        String miUsername = System.getProperty(MI_USERNAME);
        if (StringUtils.isEmpty(miUsername)) {
            miUsername = (String) parsedConfigs.get(TOML_MI_USERNAME);
            properties.put(MI_USERNAME, resolveSecret(miUsername));
        }

        String miPassword = System.getProperty(MI_PASSWORD);
        if (StringUtils.isEmpty(miPassword)) {
            miPassword = (String) parsedConfigs.get(TOML_MI_PASSWORD);
            properties.put(MI_PASSWORD, resolveSecret(miPassword));
        }

        String balUsername = System.getProperty(BAL_USERNAME);
        if (StringUtils.isEmpty(balUsername)) {
            balUsername = (String) parsedConfigs.get(TOML_BAL_SERVICE_USERNAME);
            properties.put(BAL_USERNAME, resolveSecret(balUsername));
        }

        String balPassword = System.getProperty(BAL_PASSWORD);
        if (StringUtils.isEmpty(balPassword)) {
            balPassword = (String) parsedConfigs.get(TOML_BAL_SERVICE_PASSWORD);
            properties.put(BAL_PASSWORD, resolveSecret(balPassword));
        }

        keyStorePassword = System.getProperty(KEYSTORE_PASSWORD);
        if (StringUtils.isEmpty(keyStorePassword)) {
            keyStorePassword = resolveSecret((String) parsedConfigs.get(TOML_KEYSTORE_PASSWORD));
        }

        keyManagerPassword = System.getProperty(KEY_MANAGER_PASSWORD);
        if (StringUtils.isEmpty(keyManagerPassword)) {
            keyManagerPassword = resolveSecret((String) parsedConfigs.get(TOML_KEY_MANAGER_PASSWORD));
        }

        jksFileLocation = System.getProperty(JKS_FILE_LOCATION);
        if (StringUtils.isEmpty(jksFileLocation)) {
            jksFileLocation = resolveSecret((String) parsedConfigs.get(TOML_JKS_FILE_LOCATION));
        }

        if (StringUtils.isEmpty(System.getProperty(FILE_BASED_USER_STORE_ENABLE))) {
            boolean isFileBased = Boolean.parseBoolean(parsedConfigs.get(FILE_BASED_USER_STORE_ENABLE).toString());
            if (!isFileBased) {
                logger.info("File based user store has been disabled.");
            }
            properties.put(IS_USER_STORE_FILE_BASED, String.valueOf(isFileBased));
        }

        System.setProperties(properties);
    }

    /**
     * Write the process ID of this process to the file.
     *
     * @param runtimePath DASHBOARD_HOME sys property value.
     */
    private void writePID(String runtimePath) {
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

    private Map<String, Object> parseConfigJS(String tomlPath) throws ConfigParserException {

        String resourcesDir =
                DASHBOARD_HOME + File.separator + MI_REPOSITORY_DIR + File.separator + MI_RESOURCE_DIR + File.separator
                        + CONF_DIR;

        String outputDir = DASHBOARD_HOME;

        File directory = new File(outputDir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        ConfigParser.parse(tomlPath, resourcesDir, outputDir);
        return ConfigParser.getParsedConfigs();
    }

    private SSOConfig generateSSOConfig(Map<String, Object> parseResult) throws SSOConfigException {

        if (Boolean.parseBoolean(parseResult.get(SSOConstants.TOML_SSO_ENABLE).toString())) {
            OIDCAgentConfig oidcAgentConfig = generateOIDCAgentConfig(parseResult);
            String adminGroupAttribute = SSOConstants.DEFAULT_SSO_ADMIN_GROUP_ATTRIBUTE;
            if (parseResult.get(SSOConstants.TOML_SSO_ADMIN_GROUP_ATTRIBUTE) instanceof String) {
                adminGroupAttribute = (String) parseResult.get(SSOConstants.TOML_SSO_ADMIN_GROUP_ATTRIBUTE);
            }
            String adminGroups = "";
            if (parseResult.get(SSOConstants.TOML_SSO_ADMIN_GROUPS) instanceof List) {
                Gson gson = new Gson();
                adminGroups = gson.toJson(parseResult.get(SSOConstants.TOML_SSO_ADMIN_GROUPS));
            }
            String baseUrl = "";
            if (parseResult.get(SSOConstants.TOML_SSO_BASE_URL) instanceof String) {
                baseUrl = (String) parseResult.get(SSOConstants.TOML_SSO_BASE_URL);
            }
            String wellKnownEndpointPath = "";
            if (parseResult.get(SSOConstants.TOML_SSO_WELL_KNOWN_ENDPOINT) instanceof String) {
                wellKnownEndpointPath = (String) parseResult.get(SSOConstants.TOML_SSO_WELL_KNOWN_ENDPOINT);
            } else if (baseUrl != null && !baseUrl.isEmpty()) {
                wellKnownEndpointPath = baseUrl + SSOConstants.DEFAULT_WELL_KNOWN_ENDPOINT_PATH;
            }
            String introspectionEndpoint = null;
            if (parseResult.get(SSOConstants.TOML_SSO_INTROSPECTION_ENDPOINT) instanceof String) {
                introspectionEndpoint = (String) parseResult.get(SSOConstants.TOML_SSO_INTROSPECTION_ENDPOINT);
            }
            String userInfoEndpoint = null;
            if (parseResult.get(SSOConstants.TOML_SSO_USER_INFO_ENDPOINT) instanceof String) {
                userInfoEndpoint = (String) parseResult.get(SSOConstants.TOML_SSO_USER_INFO_ENDPOINT);
            }
            setJavaxSslTruststore(parseResult);
            return new SSOConfig(oidcAgentConfig, adminGroupAttribute, adminGroups, wellKnownEndpointPath, baseUrl,
                                 introspectionEndpoint, userInfoEndpoint);
        }
        return null;
    }

    private OIDCAgentConfig generateOIDCAgentConfig(Map<String, Object> parseResult) throws SSOConfigException {

        OIDCAgentConfig oidcAgentConfig = new OIDCAgentConfig();
        if (!(parseResult.get(SSOConstants.TOML_SSO_JWT_ISSUER) instanceof String)) {
            throw new SSOConfigException("Missing value for " + SSOConstants.TOML_SSO_JWT_ISSUER + " in SSO Configs");
        }
        Issuer issuer = new Issuer((String) parseResult.get(SSOConstants.TOML_SSO_JWT_ISSUER));
        oidcAgentConfig.setIssuer(issuer);
        if (parseResult.get(SSOConstants.TOML_SSO_JWKS_ENDPOINT) instanceof String) {
            URI jwksEndpoint = null;
            try {
                jwksEndpoint = new URI((String) parseResult.get(SSOConstants.TOML_SSO_JWKS_ENDPOINT));
                oidcAgentConfig.setJwksEndpoint(jwksEndpoint);
            } catch (URISyntaxException e) {
                throw new SSOConfigException("Invalid url for " + SSOConstants.TOML_SSO_JWKS_ENDPOINT + " in SSO " +
                                             "Configs", e);
            }
        }
        if (!(parseResult.get(SSOConstants.TOML_SSO_CLIENT_ID) instanceof String)) {
            throw new SSOConfigException("Missing value for " + SSOConstants.TOML_SSO_CLIENT_ID + " in SSO Configs");
        }
        ClientID consumerKey = new ClientID((String) parseResult.get(SSOConstants.TOML_SSO_CLIENT_ID));
        oidcAgentConfig.setConsumerKey(consumerKey);
        if (parseResult.get(SSOConstants.TOML_SSO_CLIENT_SECRET) instanceof String) {
            Secret consumerSecret = new Secret((String) parseResult.get(SSOConstants.TOML_SSO_CLIENT_SECRET));
            oidcAgentConfig.setConsumerSecret(consumerSecret);
        }
        if (parseResult.get(SSOConstants.TOML_SSO_JWKS_ALGORITHM) instanceof String) {
            JWSAlgorithm jwsAlgorithm = new JWSAlgorithm((String) parseResult.get(
                    SSOConstants.TOML_SSO_JWKS_ALGORITHM));
            oidcAgentConfig.setSignatureAlgorithm(jwsAlgorithm);
        }
        Set<String> trustedAudience = new HashSet<>();
        trustedAudience.add(consumerKey.getValue());
        if (parseResult.get(SSOConstants.TOML_SSO_ADDITIONAL_TRUSTED_AUDIENCE) instanceof List) {
            List array = (ArrayList) parseResult.get(SSOConstants.TOML_SSO_ADDITIONAL_TRUSTED_AUDIENCE);
            for (int i = 0; i < Objects.requireNonNull(array).size(); i++) {
                trustedAudience.add(array.get(i).toString());
            }
        }
        oidcAgentConfig.setTrustedAudience(trustedAudience);
        return oidcAgentConfig;
    }

    private void setJavaxSslTruststore(Map<String, Object> parseResult) throws SSOConfigException {

        Object trustStoreFileLocationRes = parseResult.get(TOML_TRUSTSTORE_FILE_LOCATION);
        Object truststorePasswordRes = parseResult.get(TOML_TRUSTSTORE_PASSWORD);
        if (!(trustStoreFileLocationRes instanceof String)
                || !(truststorePasswordRes instanceof String)
                || ((String) truststorePasswordRes).isEmpty()
                || ((String) trustStoreFileLocationRes).isEmpty()) {
            throw new SSOConfigException("Truststore information is missing");
        }
        String trustStoreLocation = (String) trustStoreFileLocationRes;
        trustStoreLocation = resolveSecret(trustStoreLocation);
        trustStoreLocation = DASHBOARD_HOME + File.separator + trustStoreLocation;
        System.setProperty(JAVAX_SSL_TRUSTSTORE, trustStoreLocation);

        String trustStorePassword = (String) truststorePasswordRes;
        trustStorePassword = resolveSecret(trustStorePassword);
        System.setProperty(JAVAX_SSL_TRUSTSTORE_PASSWORD, trustStorePassword);
    }

    private void initSecureVault(Map<String, Object> parseResult) {

        System.setProperty(CARBON_HOME, DASHBOARD_HOME);
        System.setProperty(CARBON_CONFIG_DIR, DASHBOARD_HOME + File.separator + CONF_DIR);
        if (!secretResolver.isInitialized()) {
            SecretManagerInitializer secretManagerInitializer = new SecretManagerInitializer();
            SecretCallbackHandler secretCallbackHandler =
                    secretManagerInitializer.init().getSecretCallbackHandler();
            secretResolver = SecretResolverFactory.create(loadProperties(parseResult));
            secretResolver.init(secretCallbackHandler);
        }
    }

    private Properties loadProperties(Map<String, Object> parseResult) {

        Properties properties = new Properties();
        String carbonHome = System.getProperty(CARBON_HOME);
        String filePath = Paths.get(carbonHome, CONF_DIR, SECURITY_DIR, SECRET_CONF).toString();

        File dataSourceFile = new File(filePath);
        if (!dataSourceFile.exists()) {
            appendTomlProperties(properties, parseResult);
            return properties;
        } else {

            Properties var8;
            try (FileInputStream in = new FileInputStream(dataSourceFile)) {
                properties.load(in);
                appendTomlProperties(properties, parseResult);
                return properties;
            } catch (IOException e) {
                logger.error(MessageFormat.format("Error loading properties from a file at :{0}", filePath), e);
                var8 = properties;
            }

            return var8;
        }
    }

    private void appendTomlProperties(Properties properties, Map<String, Object> parseResult) {

        Set<String> keysToInclude = new HashSet<>(Arrays.asList(TOML_MI_USERNAME, TOML_MI_PASSWORD,
                TOML_BAL_SERVICE_USERNAME, TOML_BAL_SERVICE_PASSWORD, TOML_KEYSTORE_PASSWORD, TOML_KEY_MANAGER_PASSWORD,
                TOML_JKS_FILE_LOCATION, TOML_TRUSTSTORE_PASSWORD, TOML_TRUSTSTORE_FILE_LOCATION));
        for (String dottedKey : parseResult.keySet()) {
            if (keysToInclude.contains(dottedKey) && Objects.nonNull(parseResult.get(dottedKey))) {
                properties.put(dottedKey, parseResult.get(dottedKey));
            }
        }
    }

    private String resolveSecret(String text) {
        String alias = MiscellaneousUtil.getProtectedToken(text);
        if (!StringUtils.isEmpty(alias)) {
            return MiscellaneousUtil.resolve(alias, secretResolver);
        }
        return text;
    }
}
