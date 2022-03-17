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
 *
 *
 */

package org.wso2.ei.dashboard.core.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.rest.delegates.auth.LogoutDelegate;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.Error;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

@Path("/logout")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2021-02-15T11:57:53.518+05:30[Asia/Colombo]")
public class LogoutApi {
    LogoutDelegate logoutDelegate = new LogoutDelegate();

    @GET
    @Produces({ "application/json" })
    @Operation(summary = "Receive logouts to the dashboard", description = "", security = {
        @SecurityRequirement(name = "bearerAuth")    }, tags={ "logout" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(schema = @Schema(implementation = Ack.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response receiveLogout(@CookieParam(Constants.JWT_COOKIE) Cookie cookie) {
        if (cookie != null) {
            String token = Constants.EMPTY_STRING;
            if (!cookie.getValue().isEmpty()) {
                token = cookie.getValue();
            }
            return logoutDelegate.logoutUser(token);
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }}
