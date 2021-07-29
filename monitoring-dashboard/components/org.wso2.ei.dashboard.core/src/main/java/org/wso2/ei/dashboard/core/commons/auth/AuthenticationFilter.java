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
import com.google.gson.JsonParser;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import io.asgardeo.java.oidc.sdk.exception.SSOAgentServerException;
import io.asgardeo.java.oidc.sdk.validators.IDTokenValidator;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ContainerRequest;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.annotation.Secured;
import org.wso2.micro.integrator.dashboard.utils.SSOConfig;
import org.wso2.micro.integrator.dashboard.utils.SSOConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Authenticate the request coming to the rest api.
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();
    private static final List<String> adminOnlyPaths = Arrays.asList("groups/mi_test/log-configs",
                                                                     "groups/mi_test/users");

    private static final Logger logger = LogManager.getLogger(AuthenticationFilter.class);

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }
        boolean isSelfContainedToken = false;
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        SSOConfig config = null;
        if (servletRequest.getServletContext()
                .getAttribute(SSOConstants.CONFIG_BEAN_NAME) instanceof SSOConfig) {
            config = (SSOConfig) servletRequest.getServletContext().getAttribute(SSOConstants.CONFIG_BEAN_NAME);
        }

        if (inValidToken(token)) {
            if (inValidSelfContainedToken(config, token)) {
                abortWithUnauthorized(requestContext);
            } else {
                isSelfContainedToken = true;
            }
        }

        if (!isAuthorized(token, requestContext, isSelfContainedToken, config)) {
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isAuthorized(String token, ContainerRequestContext requestContext, boolean isSelfContainedToken,
                                 SSOConfig config) {

        String path = ((ContainerRequest) requestContext).getPath(false);

        if (adminOnlyPaths.contains(path)) {
            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                String payloadJson = new String(decoder.decode(parts[1]));
                JsonElement jsonElementPayload = JsonParser.parseString(payloadJson);
                if (isSelfContainedToken) {
                    return isUserInAdminGroup(jsonElementPayload, config);
                }
                JsonElement scopeElement = jsonElementPayload.getAsJsonObject().get("scope");
                if (scopeElement != null) {
                    String scope = scopeElement.getAsString();
                    return scope.equals("admin");
                }
            }
        } else {
            return true;
        }

        return false;
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Unauthorized");

        Response unauthorizedResponse = Response.status(Response.Status.UNAUTHORIZED)
                .entity(responseBody)
                .header("content" +
                "-type", "application/json").build();
        requestContext.abortWith(unauthorizedResponse);
    }

    private boolean inValidToken(String token) {
        return TokenCache.getInstance().getToken(token) == null;
    }

    private boolean inValidSelfContainedToken(SSOConfig config, String token) {

        if (config == null) {
            return true;
        }
        JWT idTokenJWT = null;
        try {
            idTokenJWT = JWTParser.parse(token);
            if (config.getOidcAgentConfig().getJwksEndpoint() == null) {
                config.getOidcAgentConfig()
                        .setJwksEndpoint(getJWKSEndpointFromWellKnownEndpoint(config.getWellKnownEndpoint()));
            }
            IDTokenValidator validator = new IDTokenValidator(config.getOidcAgentConfig(), idTokenJWT);
            validator.validate(null);
            return false;
        } catch (DashboardServerException | ParseException | SSOAgentServerException e) {
            if (logger.isDebugEnabled()) {
                logger.error("Error validating the access token", e);
            }
        }
        return true;
    }

    private boolean isUserInAdminGroup(JsonElement tokenPayload, SSOConfig config) {

        if (config == null) {
            return false;
        }
        JsonArray groupElement = tokenPayload.getAsJsonObject().getAsJsonArray(config.getAdminGroupAttribute());
        for (JsonElement group : groupElement) {
            if (config.getAllowedAdminGroups().contains(group.getAsString())) {
                return true;
            }
        }
        return false;
    }

    private static URI getJWKSEndpointFromWellKnownEndpoint(String wellKnownEndpointPath) {

        HttpGet httpGet = new HttpGet(wellKnownEndpointPath);
        CloseableHttpResponse httpResponse = HttpUtils.doGet(httpGet);

        int httpSc = httpResponse.getStatusLine().getStatusCode();

        if (httpSc == HttpStatus.SC_OK) {
            try {
                return new URI(HttpUtils.getJsonResponse(httpResponse).get(Constants.JWKS_URI).getAsString());
            } catch (URISyntaxException e) {
                throw new DashboardServerException("Invalid url for JWKS Endpoint.", e);
            }
        }
        throw new DashboardServerException("Cannot find jwks_uri in well known endpoint response. " +
                httpResponse.getStatusLine().getReasonPhrase());
    }
}
