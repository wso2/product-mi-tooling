/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.ei.dashboard.core.commons.auth;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.micro.integrator.dashboard.utils.SSOConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.wso2.ei.dashboard.core.commons.Constants.TOKEN_CACHE_TIMEOUT;

/**
 * This class implements SecurityHandler to implement the authentication logic for a Opaque token.
 */
public class OpaqueTokenSecurityHandler implements SecurityHandler {

    private static final Logger logger = LogManager.getLogger(OpaqueTokenSecurityHandler.class);
    private static final Cache<String, Boolean> adminClaimMap =
            CacheBuilder.newBuilder().expireAfterWrite(TOKEN_CACHE_TIMEOUT, TimeUnit.MINUTES).build();

    @Override
    public boolean isAuthenticated(SSOConfig config, String token) {

        if (config.getIntrospectionEndpoint() == null) {
            config.setIntrospectionEndpoint(
                    getIntrospectionEndpointFromWellKnownEndpoint(config.getWellKnownEndpoint()));
        }

        Map<String, String> introspectionRequestBody = new HashMap<>();
        introspectionRequestBody.put(Constants.TOKEN, token);
        introspectionRequestBody.put(Constants.CLIENT_ID, config.getOidcAgentConfig().getConsumerKey().getValue());
        introspectionRequestBody
                .put(Constants.CLIENT_SECRET, config.getOidcAgentConfig().getConsumerSecret().getValue());

        CloseableHttpResponse httpResponse =
                HttpUtils.doPost(config.getIntrospectionEndpoint(), introspectionRequestBody);

        int httpSc = httpResponse.getStatusLine().getStatusCode();

        if (httpSc == HttpStatus.SC_OK) {
            return HttpUtils.getJsonResponse(httpResponse).get(Constants.ACTIVE).getAsBoolean();
        }
        if (logger.isDebugEnabled()) {
            logger.error("Error validating the token using introspection endpoint. ",
                    httpResponse.getStatusLine().getReasonPhrase());
        }
        return false;
    }

    @Override
    public boolean isAuthorized(SSOConfig ssoConfig, String token) {

        return validateWithCache(token) || validateAdminWithUserInfoEndpoint(ssoConfig, token);
    }

    private boolean validateWithCache(String token) {

        if (adminClaimMap.getIfPresent(token) != null) {
            return adminClaimMap.getIfPresent(token);
        }
        return false;
    }

    private boolean validateAdminWithUserInfoEndpoint(SSOConfig config, String token) {

        if (config.getUserInfoEndpoint() == null) {
            config.setUserInfoEndpoint(
                    getUserInfoEndpointFromWellKnownEndpoint(config.getWellKnownEndpoint()));
        }

        CloseableHttpResponse httpResponse =
                HttpUtils.doGet(token, config.getUserInfoEndpoint());

        int httpSc = httpResponse.getStatusLine().getStatusCode();

        if (httpSc == HttpStatus.SC_OK) {
            JsonArray groupElement =
                    HttpUtils.getJsonResponse(httpResponse).get(config.getAdminGroupAttribute()).getAsJsonArray();
            for (JsonElement group : groupElement) {
                if (config.getAllowedAdminGroups().contains(group.getAsString())) {
                    adminClaimMap.put(token, true);
                    return true;
                }
            }
            adminClaimMap.put(token, false);
        }
        if (logger.isDebugEnabled()) {
            logger.error("Error validating the token using userInfo endpoint. ",
                    httpResponse.getStatusLine().getReasonPhrase());
        }
        return false;
    }

    private String getUserInfoEndpointFromWellKnownEndpoint(String wellKnownEndpoint) {

        HttpGet httpGet = new HttpGet(wellKnownEndpoint);
        CloseableHttpResponse httpResponse = HttpUtils.doGet(httpGet);

        int httpSc = httpResponse.getStatusLine().getStatusCode();

        if (httpSc == HttpStatus.SC_OK) {
            JsonObject jsonResponse = HttpUtils.getJsonResponse(httpResponse);
            if (jsonResponse.has(Constants.USERINFO_URI)) {
                return jsonResponse.get(Constants.USERINFO_URI).getAsString();
            }
        }
        throw new DashboardServerException("Cannot find " + Constants.USERINFO_URI + " in well known endpoint " +
                "response. " +
                httpResponse.getStatusLine().getReasonPhrase());
    }

    private String getIntrospectionEndpointFromWellKnownEndpoint(String wellKnownEndpoint) {

        HttpGet httpGet = new HttpGet(wellKnownEndpoint);
        CloseableHttpResponse httpResponse = HttpUtils.doGet(httpGet);

        int httpSc = httpResponse.getStatusLine().getStatusCode();

        if (httpSc == HttpStatus.SC_OK) {
            JsonObject jsonResponse = HttpUtils.getJsonResponse(httpResponse);
            if (jsonResponse.has(Constants.INTROSPECTION_URI)) {
                return jsonResponse.get(Constants.INTROSPECTION_URI).getAsString();
            }
        }
        throw new DashboardServerException("Cannot find " + Constants.INTROSPECTION_URI + " in well known endpoint " +
                "response. " +
                httpResponse.getStatusLine().getReasonPhrase());
    }
}
