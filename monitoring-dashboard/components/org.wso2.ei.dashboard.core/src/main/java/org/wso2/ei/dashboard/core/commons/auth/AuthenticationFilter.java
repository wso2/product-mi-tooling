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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.glassfish.jersey.server.ContainerRequest;
import org.wso2.ei.dashboard.core.rest.annotation.Secured;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
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
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        if (inValidToken(token)) {
            abortWithUnauthorized(requestContext);
        }
        
        if (!isAuthorized(token, requestContext)) {
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isAuthorized(String token, ContainerRequestContext requestContext) {

        String path = ((ContainerRequest) requestContext).getPath(false);
        
        if (adminOnlyPaths.contains(path)) {
            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                String payloadJson = new String(decoder.decode(parts[1]));
                JsonElement jsonElementPayload = JsonParser.parseString(payloadJson);
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
}
