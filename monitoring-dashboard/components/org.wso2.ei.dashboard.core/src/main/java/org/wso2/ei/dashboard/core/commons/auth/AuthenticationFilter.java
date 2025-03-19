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
import static org.wso2.ei.dashboard.core.commons.auth.JwtUtil.isJWTToken;

/**
 * Authenticate the request coming to the rest api.
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static final List<String> ADMIN_ONLY_PATHS = Arrays.asList("/log-configs", "/users", "/roles");
    private static final String MAKE_NON_ADMIN_USERS_READ_ONLY = "make_non_admin_users_read_only";
    private static final String ACTION_PERFORMED_BY = "performedBy";

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String httpMethod = requestContext.getMethod();
        String token = extractToken(requestContext);
        SecurityHandler securityHandler = getSecurityHandler(requestContext, token);
        if (token == null || securityHandler == null) {
            abortWithUnauthorized(requestContext);
            return;
        }

        SSOConfig config = getSsoConfig();
        if (!securityHandler.isAuthenticated(config, token)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        boolean makeNonAdminUsersReadOnly = Boolean.parseBoolean(System.getProperty(MAKE_NON_ADMIN_USERS_READ_ONLY));
        if (isAdminResource(requestContext) && !securityHandler.isAuthorized(config, token)) {
            abortWithUnauthorized(requestContext);
            return;
        }
        if (!"GET".equalsIgnoreCase(httpMethod) && makeNonAdminUsersReadOnly
                && !securityHandler.isAuthorized(config, token)) {
            // For non-admin resources, request except GET are blocked
            // if the 'makeNonAdminUsersReadOnly' is set to 'true'
            abortWithUnauthorized(requestContext);
            return;
        }
        String performedBy = extractPerformedBy(token);
        requestContext.setProperty(ACTION_PERFORMED_BY, performedBy);
    }

    private SSOConfig getSsoConfig() {
        Object config = this.servletRequest.getServletContext().getAttribute(SSOConstants.CONFIG_BEAN_NAME);
        return config instanceof SSOConfig ? (SSOConfig) config : null;
    }

    private static boolean isAdminResource(ContainerRequestContext requestContext) {
        String path = ((ContainerRequest) requestContext).getPath(false);
        String resource = path.substring(path.lastIndexOf("/"));
        return ADMIN_ONLY_PATHS.contains(resource);
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Unauthorized");
        Response unauthorizedResponse = Response.status(Response.Status.UNAUTHORIZED).entity(responseBody)
                .header("content-type", "application/json").build();
        requestContext.abortWith(unauthorizedResponse);
    }

    private String extractToken(ContainerRequestContext requestContext) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (isTokenBasedAuthentication(authorizationHeader)) {
            return authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        }
        Map<String, Cookie> cookies = requestContext.getCookies();
        if (isCookieBasedAuthentication(cookies)) {
            return cookies.get(JWT_COOKIE).getValue();
        }
        return null;
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private boolean isCookieBasedAuthentication(Map<String, Cookie> cookies) {
        return cookies != null && cookies.get(JWT_COOKIE) != null;
    }

    private String extractPerformedBy(String token) {
        return isJWTToken(token) ? JwtUtil.extractSubject(token) : null;
    }

    private SecurityHandler getSecurityHandler(ContainerRequestContext requestContext, String token) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (isTokenBasedAuthentication(authorizationHeader)) {
            return getSSOSecurityHandler(token);
        }
        if (isCookieBasedAuthentication(requestContext.getCookies())) {
            return new InMemorySecurityHandler();
        }
        return null;
    }

    private static SecurityHandler getSSOSecurityHandler(String token) {
        if (JwtUtil.isJWTToken(token)) {
            return new JWTSecurityHandler();
        }
        return new OpaqueTokenSecurityHandler();
    }
}
