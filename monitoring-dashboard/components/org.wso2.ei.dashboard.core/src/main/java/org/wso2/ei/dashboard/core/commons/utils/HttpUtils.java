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

package org.wso2.ei.dashboard.core.commons.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;

/**
 * Utilities to execute http requests.
 */
public class HttpUtils {

    private static volatile CloseableHttpClient httpClientInstance = null;
    private static PoolingHttpClientConnectionManager clientConnectionManager;
    private static final Lock lock = new ReentrantLock();
    private static final Logger logger = LogManager.getLogger(HttpUtils.class);

    private HttpUtils() {
    }

    public static CloseableHttpResponse doGet(String accessToken, String url) throws ManagementApiException {
        try {
            final HttpGet httpGet = new HttpGet(url);
            String authHeader = "Bearer " + accessToken;
            httpGet.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
            httpGet.setHeader("Authorization", authHeader);

            return doGet(httpGet);
        } catch (IllegalArgumentException | DashboardServerException e) {
            throw new ManagementApiException("Error occurred while creating http get request.", 400, e.getCause());
        }
    }

    public static CloseableHttpResponse doGet(String accessToken, String url, Map<String, String> params) {
        URIBuilder builder;
        try {
            builder = new URIBuilder(url);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.setParameter(entry.getKey(), entry.getValue());
            }
            final HttpGet httpGet = new HttpGet(builder.build());
            String authHeader = "Bearer " + accessToken;
            httpGet.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
            httpGet.setHeader("Authorization", authHeader);
            return doGet(httpGet);
        } catch (URISyntaxException | DashboardServerException e) {
            throw new DashboardServerException("Error occurred while sending get http request.", e);
        }

    }

    public static CloseableHttpResponse doGet(HttpGet httpGet) throws DashboardServerException {
        CloseableHttpClient httpClient;
        try {
            httpClient = getHttpClient();
            return httpClient.execute(httpGet);
        } catch (IOException | DashboardServerException e) {
            throw new DashboardServerException("Error occurred while sending get http request.", e);
        } catch (Throwable e) {
            // Using the same lock object to avoid multiple clients being created.
            // Also, this is ok since the lock is a reentrant lock, and it supports multiple lock calls with same
            // thread.
            lock.lock();
            try {
                return getHttpClient().execute(httpGet);
            } catch (Throwable ex) {
                handleConnectionError(ex);
                httpClient = getHttpClient();
                try {
                    return httpClient.execute(httpGet);
                } catch (Throwable exception) {
                    throw new DashboardServerException("Retry with new client failed due to: ", ex);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public static CloseableHttpResponse doPost(String accessToken, String url, JsonObject payload) {
        final HttpPost httpPost = new HttpPost(url);
        String authHeader = "Bearer " + accessToken;
        httpPost.setHeader("Authorization", authHeader);
        httpPost.setHeader("content-type", Constants.HEADER_VALUE_APPLICATION_JSON);
        try {
            StringEntity entity = new StringEntity(payload.toString());
            httpPost.setEntity(entity);
            return doPost(httpPost);
        } catch (UnsupportedEncodingException | DashboardServerException e) {
            throw new DashboardServerException("Error occurred while creating http post request.", e);
        }
    }

    public static CloseableHttpResponse doPost(String url, Map<String, String> params) {
        final HttpPost httpPost = new HttpPost(url);
        StringBuilder payload = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            payload.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        httpPost.setHeader("Content-type", Constants.APPLICATION_X_WWW_FORM_URLENCODED);
        try {
            StringEntity entity = new StringEntity(payload.toString());
            httpPost.setEntity(entity);
            return doPost(httpPost);
        } catch (UnsupportedEncodingException | DashboardServerException e) {
            throw new DashboardServerException("Error occurred while creating http post request.", e);
        }
    }

    public static CloseableHttpResponse doPatch(String accessToken, String url, JsonObject payload)
            throws DashboardServerException {
        final HttpPatch httpPatch = new HttpPatch(url);
        String authHeader = "Bearer " + accessToken;
        httpPatch.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPatch.setHeader("Content-Type", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPatch.setHeader("Authorization", authHeader);
        HttpEntity httpEntity = new ByteArrayEntity(payload.toString().getBytes(StandardCharsets.UTF_8));
        httpPatch.setEntity(httpEntity);
        return doPatch(httpPatch);
    }

    public static CloseableHttpResponse doPut(String accessToken, String url, JsonObject payload)
            throws DashboardServerException {
        final HttpPut httpPut = new HttpPut(url);

        String authHeader = "Bearer " + accessToken;
        httpPut.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPut.setHeader("Content-Type", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPut.setHeader("Authorization", authHeader);
        HttpEntity httpEntity = new ByteArrayEntity(payload.toString().getBytes(StandardCharsets.UTF_8));
        httpPut.setEntity(httpEntity);
        return doPut(httpPut);
    }

    public static CloseableHttpResponse doDelete(String accessToken, String url) throws DashboardServerException {
        String authHeader = "Bearer " + accessToken;
        final HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpDelete.setHeader("Authorization", authHeader);

        return doDelete(httpDelete);
    }

    public static JsonObject getJsonResponse(CloseableHttpResponse response) {
        String stringResponse = getStringResponse(response);
        return JsonParser.parseString(stringResponse).getAsJsonObject();
    }

    public static JsonArray getJsonArray(CloseableHttpResponse response) {
        String stringResponse = getStringResponse(response);
        return JsonParser.parseString(stringResponse).getAsJsonArray();
    }

    public static String getStringResponse(CloseableHttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            return EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            throw new DashboardServerException("Error occurred while converting Http response to string", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                throw new DashboardServerException("Error occurred while closing Http response", e);
            }
        }
    }

    private static CloseableHttpResponse doPost(HttpPost httpPost) {
        CloseableHttpClient httpClient;
        try {
            httpClient = getHttpClient();
            return httpClient.execute(httpPost);
        } catch (Throwable e) {
            // Using the same lock object to avoid multiple clients being created.
            // Also, this is ok since the lock is a reentrant lock, and it supports multiple lock calls with same
            // thread.
            lock.lock();
            try {
                return getHttpClient().execute(httpPost);
            } catch (Throwable ex) {
                handleConnectionError(ex);
                httpClient = getHttpClient();
                try {
                    return httpClient.execute(httpPost);
                } catch (Throwable exception) {
                    throw new DashboardServerException("Retry with new client failed due to: ", ex);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private static CloseableHttpResponse doPatch(HttpPatch httpPatch) {
        CloseableHttpClient httpClient;
        try {
            httpClient = getHttpClient();
            return httpClient.execute(httpPatch);
        } catch (Throwable e) {
            // Using the same lock object to avoid multiple clients being created.
            // Also, this is ok since the lock is a reentrant lock, and it supports multiple lock calls with same
            // thread.
            lock.lock();
            try {
                return getHttpClient().execute(httpPatch);
            } catch (Throwable ex) {
                handleConnectionError(ex);
                httpClient = getHttpClient();
                try {
                    return httpClient.execute(httpPatch);
                } catch (Throwable exception) {
                    throw new DashboardServerException("Retry with new client failed due to: ", ex);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private static CloseableHttpResponse doPut(HttpPut httpPut) {
        CloseableHttpClient httpClient;
        try {
            httpClient = getHttpClient();
            return httpClient.execute(httpPut);
        } catch (Throwable e) {
            // Using the same lock object to avoid multiple clients being created.
            // Also, this is ok since the lock is a reentrant lock, and it supports multiple lock calls with same
            // thread.
            lock.lock();
            try {
                return getHttpClient().execute(httpPut);
            } catch (Throwable ex) {
                handleConnectionError(ex);
                httpClient = getHttpClient();
                try {
                    return httpClient.execute(httpPut);
                } catch (Throwable exception) {
                    throw new DashboardServerException("Retry with new client failed due to: ", ex);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private static CloseableHttpResponse doDelete(HttpDelete httpDelete) {
        CloseableHttpClient httpClient;
        try {
            httpClient = getHttpClient();
            return httpClient.execute(httpDelete);
        } catch (Throwable e) {
            // Using the same lock object to avoid multiple clients being created.
            // Also, this is ok since the lock is a reentrant lock, and it supports multiple lock calls with same
            // thread.
            lock.lock();
            try {
                return getHttpClient().execute(httpDelete);
            } catch (Throwable ex) {
                handleConnectionError(ex);
                httpClient = getHttpClient();
                try {
                    return httpClient.execute(httpDelete);
                } catch (Throwable exception) {
                    throw new DashboardServerException("Retry with new client failed due to: ", ex);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private static CloseableHttpClient getHttpClient() {
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;

        if (httpClientInstance == null) {
            lock.lock();
            try {
                // Double-checked locking
                if (httpClientInstance == null) {
            SSLContext sslContext = SSLContexts.custom()
                                               .loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
                                                                                      NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", socketFactory)
                            .register("http", new PlainConnectionSocketFactory()).build();
                    RequestConfig defaultRequestConfig = RequestConfig.custom()
                            .setSocketTimeout(120000)  // 120 seconds of inactivity before timing out waiting for data
                            .build();
            clientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                    clientConnectionManager.setMaxTotal(800);
                    clientConnectionManager.setDefaultMaxPerRoute(400);
                    clientConnectionManager.setValidateAfterInactivity(30000);

                    httpClientInstance =
                            HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
                                    .setSSLSocketFactory(socketFactory).setConnectionManager(clientConnectionManager)
                                    .build();
                    new ConnectionManagerCleanup(clientConnectionManager).start();
                }
            } catch (Throwable e) {
                throw new DashboardServerException("Error occurred while creating http client.", e);
            } finally {
                lock.unlock();
            }
        }
        return httpClientInstance;
    }

    /**
     * Handle connection error by closing the existing connection manager and the old HttpClient instance.
     */
    public static void handleConnectionError(Throwable e) {
        CloseableHttpClient oldHttpClientInstance = null;
        PoolingHttpClientConnectionManager localClientConnectionManager = null;
        try {
            logger.error("HTTP request failed due to an error: ", e);
            // Save the reference to the old instance
            oldHttpClientInstance = httpClientInstance;
            localClientConnectionManager = clientConnectionManager;

            // Set the shared instance to null to prevent new connections from using it. Soon after this the new
            // requests will create a new HttpClient instance. Also, in that process it will create new instance of
            // connection manager. That is why we need to keep reference to old connection manager and close it
            // gracefully.
            httpClientInstance = null;

            // Close the connection manager if it exists gracefully
            logger.warn("Closing the existing connection manager and the HttpClient instance.");
            if (localClientConnectionManager != null) {
                try {
                    localClientConnectionManager.close();
                } catch (Exception ex) {
                    //ignore
                }
            }

            // Close the old HttpClient instance
            if (oldHttpClientInstance != null) {
                try {
                    oldHttpClientInstance.close();
                } catch (Exception ex) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //Ignore
        }
    }


    /**
     * This class is used to clean up expired and idle connections.
     */
    public static class ConnectionManagerCleanup {

        private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

        public ConnectionManagerCleanup(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
            this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
        }

        public void start() {
            Thread cleanupThread = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        // Close connections that have been idle for 30 seconds
                        poolingHttpClientConnectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
                        // Additionally, close expired connections
                        poolingHttpClientConnectionManager.closeExpiredConnections();
                        // Sleep for 15 seconds before the next iteration
                        Thread.sleep(15000);
                    }
                } catch (InterruptedException e) {
                    // Handle the interruption or exit the loop
                }
            });

            cleanupThread.setDaemon(true);
            cleanupThread.start();
        }
    }

    public static void setHeaders(Response.ResponseBuilder responseBuilder) {
        responseBuilder
                .header("Content-Security-Policy",
                        "default-src 'none'; script-src 'self'; connect-src 'self'; img-src 'self';" +
                                " style-src 'self'; frame-ancestors 'none'; form-action 'self'; object-src 'none';")
                .header("X-Frame-Options", "DENY")
                .header("X-Content-Type-Options", "nosniff")
                .header("Referrer-Policy", "same-origin")
                .header("X-XSS-Protection", "1; mode=block")
                .header("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .header("Cache-Control", "no-cache, no-store, must-revalidate");
    }

}
