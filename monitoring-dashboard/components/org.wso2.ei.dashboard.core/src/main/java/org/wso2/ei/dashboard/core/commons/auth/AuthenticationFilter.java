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

import org.glassfish.jersey.server.ContainerRequest;
import org.wso2.ei.dashboard.core.rest.annotation.Secured;
import org.wso2.micro.integrator.dashboard.utils.SSOConfig;
import org.wso2.micro.integrator.dashboard.utils.SSOConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static org.wso2.ei.dashboard.core.commons.Constants.JWT_COOKIE;

/**
 * Authenticate the request coming to the rest api.
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static final List<String> adminOnlyPaths = Arrays.asList("/log-configs",
                                                                     "/users");

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        Map<String, Cookie> cookies = requestContext.getCookies();
        String token;
        SecurityHandler securityHandler;

        if (isTokenBasedAuthentication(authorizationHeader)) {
            token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
            securityHandler = getSSOSecurityHandler(token);
        } else if (isCookieBasedAuthentication(cookies)) {
            token = cookies.get(JWT_COOKIE).getValue();
            securityHandler = new InMemorySecurityHandler();
        } else {
            abortWithUnauthorized(requestContext);
            return;
        }

        SSOConfig config = null;
        if (servletRequest.getServletContext()
                .getAttribute(SSOConstants.CONFIG_BEAN_NAME) instanceof SSOConfig) {
            config = (SSOConfig) servletRequest.getServletContext().getAttribute(SSOConstants.CONFIG_BEAN_NAME);
        }

        if (!securityHandler.isAuthenticated(config, token)) {
            abortWithUnauthorized(requestContext);
        }

        if (isAdminResource(requestContext) && !securityHandler.isAuthorized(config, token)) {
            abortWithUnauthorized(requestContext);
        }
    }

    private static boolean isAdminResource(ContainerRequestContext requestContext) {

        String path = ((ContainerRequest) requestContext).getPath(false);
        String resource = path.substring(path.lastIndexOf("/"));
        return adminOnlyPaths.contains(resource);
    }

    private static SecurityHandler getSSOSecurityHandler(String token) {

        if (isJWTToken(token)) {
            return new JWTSecurityHandler();
        }
        return new OpaqueTokenSecurityHandler();
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private boolean isCookieBasedAuthentication(Map<String, Cookie> cookies) {

        return cookies != null && cookies.get(JWT_COOKIE) != null;
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

    private static boolean isJWTToken(String token) {

        String[] parts = token.split("\\.");
        return parts.length >= 2;
    }
}
