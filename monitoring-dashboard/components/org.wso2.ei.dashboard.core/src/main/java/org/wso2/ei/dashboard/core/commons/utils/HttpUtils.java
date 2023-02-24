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
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;

/**
 * Utilities to execute http requests.
 */
public class HttpUtils {

    private HttpUtils() {
    }

    public static CloseableHttpResponse doGet(String accessToken, String url) {
        final HttpGet httpGet = new HttpGet(url);
        String authHeader = "Bearer " + accessToken;
        httpGet.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpGet.setHeader("Authorization", authHeader);

        return doGet(httpGet);
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
        } catch (URISyntaxException e) {
            throw new DashboardServerException("Error occurred while sending get http request.", e);
        }
        
    }

    public static CloseableHttpResponse doGet(HttpGet httpGet) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            return httpClient.execute(httpGet);
        } catch (IOException e) {
            throw new DashboardServerException("Error occurred while sending get http request.", e);
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
        } catch (UnsupportedEncodingException e) {
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
        } catch (UnsupportedEncodingException e) {
            throw new DashboardServerException("Error occurred while creating http post request.", e);
        }
    }

    public static CloseableHttpResponse doPatch(String accessToken, String url, JsonObject payload) {
        final HttpPatch httpPatch = new HttpPatch(url);
        String authHeader = "Bearer " + accessToken;
        httpPatch.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPatch.setHeader("Content-Type", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPatch.setHeader("Authorization", authHeader);
        HttpEntity httpEntity = new ByteArrayEntity(payload.toString().getBytes(StandardCharsets.UTF_8));
        httpPatch.setEntity(httpEntity);
        return doPatch(httpPatch);
    }

    public static CloseableHttpResponse doPut(String accessToken, String url, JsonObject payload) {
        final HttpPut httpPut = new HttpPut(url);

        String authHeader = "Bearer " + accessToken;
        httpPut.setHeader("Accept", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPut.setHeader("Content-Type", Constants.HEADER_VALUE_APPLICATION_JSON);
        httpPut.setHeader("Authorization", authHeader);
        HttpEntity httpEntity = new ByteArrayEntity(payload.toString().getBytes(StandardCharsets.UTF_8));
        httpPut.setEntity(httpEntity);
        return doPut(httpPut);
    }

    public static CloseableHttpResponse doDelete(String accessToken, String url) {
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
        }
    }

    private static CloseableHttpResponse doPost(HttpPost httpPost) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            return httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new DashboardServerException("Error occurred while sending http post request.", e);
        }
    }

    private static CloseableHttpResponse doPatch(HttpPatch httpPatch) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            return httpClient.execute(httpPatch);
        } catch (IOException e) {
            throw new DashboardServerException("Error occurred while sending http patch request.", e);
        }
    }

    private static CloseableHttpResponse doPut(HttpPut httpPut) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            return httpClient.execute(httpPut);
        } catch (IOException e) {
            throw new DashboardServerException("Error occurred while sending http put request.", e);
        }
    }

    private static CloseableHttpResponse doDelete(HttpDelete httpDelete) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            return httpClient.execute(httpDelete);
        } catch (IOException e) {
            throw new DashboardServerException("Error occurred while sending delete http request.", e);
        }
    }

    private static CloseableHttpClient getHttpClient() {
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        try {
            SSLContext sslContext = SSLContexts.custom()
                                               .loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
                                                                                      NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", socketFactory)
                            .register("http", new PlainConnectionSocketFactory())
                            .build();
            BasicHttpClientConnectionManager connectionManager =
                    new BasicHttpClientConnectionManager(socketFactoryRegistry);
            return HttpClients.custom().setSSLSocketFactory(socketFactory)
                              .setConnectionManager(connectionManager).build();
        } catch (Exception e) {
            throw new DashboardServerException("Error occurred while creating http client.", e);
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
                .header("X-XSS-Protection", "1; mode=block");
    }

}
