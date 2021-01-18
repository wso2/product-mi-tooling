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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;

import java.io.IOException;
import javax.net.ssl.SSLContext;

/**
 * Utilities to execute http requests.
 */
public class HttpUtils {

    private HttpUtils() {
    }

    public static CloseableHttpResponse doGet(HttpGet httpGet) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            return httpClient.execute(httpGet);
        } catch (IOException e) {
            throw new DashboardServerException("Error occurred while sending http request.", e);
        }
    }

    public static JsonObject getJsonResponse(CloseableHttpResponse response) {
        String stringResponse = getStringResponse(response);
        return JsonParser.parseString(stringResponse).getAsJsonObject();
    }

    public static String getStringResponse(CloseableHttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            return EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            throw new DashboardServerException("Error occurred while converting Http response to string", e);
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

}
