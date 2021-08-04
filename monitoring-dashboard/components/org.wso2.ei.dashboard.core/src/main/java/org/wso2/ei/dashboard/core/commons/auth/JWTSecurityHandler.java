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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import io.asgardeo.java.oidc.sdk.exception.SSOAgentServerException;
import io.asgardeo.java.oidc.sdk.validators.IDTokenValidator;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.TokenUtils;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.micro.integrator.dashboard.utils.SSOConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * This class implements SecurityHandler to implement the authentication logic for a JWT self contained token.
 */
public class JWTSecurityHandler implements SecurityHandler {

    private static final Logger logger = LogManager.getLogger(JWTSecurityHandler.class);

    @Override
    public boolean isAuthenticated(SSOConfig config, String token) {

        JWT idTokenJWT = null;
        try {
            idTokenJWT = JWTParser.parse(token);
            if (config.getOidcAgentConfig().getJwksEndpoint() == null) {
                config.getOidcAgentConfig()
                        .setJwksEndpoint(getJWKSEndpointFromWellKnownEndpoint(config.getWellKnownEndpoint()));
            }
            IDTokenValidator validator = new IDTokenValidator(config.getOidcAgentConfig(), idTokenJWT);
            validator.validate(null);
            return true;
        } catch (DashboardServerException | ParseException | SSOAgentServerException e) {
            if (logger.isDebugEnabled()) {
                logger.error("Error validating the access token", e);
            }
        }
        return false;
    }

    @Override
    public boolean isAuthorized(SSOConfig ssoConfig, String token) {

        JsonElement jsonElementPayload = TokenUtils.getParsedToken(token);
        return isUserInAdminGroup(jsonElementPayload, ssoConfig);
    }

    private boolean isUserInAdminGroup(JsonElement tokenPayload, SSOConfig config) {

        JsonArray groupElement = tokenPayload.getAsJsonObject().getAsJsonArray(config.getAdminGroupAttribute());
        for (JsonElement group : groupElement) {
            if (config.getAllowedAdminGroups().contains(group.getAsString())) {
                return true;
            }
        }
        return false;
    }

    private URI getJWKSEndpointFromWellKnownEndpoint(String wellKnownEndpointPath) {

        HttpGet httpGet = new HttpGet(wellKnownEndpointPath);
        CloseableHttpResponse httpResponse = HttpUtils.doGet(httpGet);

        int httpSc = httpResponse.getStatusLine().getStatusCode();

        if (httpSc == HttpStatus.SC_OK) {
            try {
                return new URI(HttpUtils.getJsonResponse(httpResponse).get(Constants.JWKS_URI).getAsString());
            } catch (URISyntaxException e) {
                throw new DashboardServerException("Invalid url for " + Constants.JWKS_URI, e);
            }
        }
        throw new DashboardServerException("Cannot find " + Constants.JWKS_URI + " in well known endpoint response. " +
                httpResponse.getStatusLine().getReasonPhrase());
    }
}
