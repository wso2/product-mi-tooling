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
import org.wso2.ei.dashboard.core.rest.delegates.auth.LoginDelegate;
import org.wso2.ei.dashboard.core.rest.model.CSRFToken;
import org.wso2.ei.dashboard.core.rest.model.Error;
import org.wso2.ei.dashboard.core.rest.model.Token;
import org.wso2.ei.dashboard.core.rest.model.UnauthorizedError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.security.SecureRandom;

@Path("/login")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2021-02-15T11:57:53.518+05:30[Asia/Colombo]")
public class LoginApi {
    LoginDelegate loginDelegate = new LoginDelegate();

    @POST
    @Consumes({ "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Receive logins to the dashboard", description = "", tags={ "login" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = Token.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = UnauthorizedError.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response receiveLogin(@FormParam(value = "username")  String username,@FormParam(value = "password")  String password) {
        return loginDelegate.authenticateUser(username, password);
    }

    @GET
    @Produces({ "application/json" })
    @Operation(summary = "Get CSRF token", description = "Fetches CSRF token for the session", tags = { "csrf" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token fetched successfully", content = @Content(schema = @Schema(implementation = CSRFToken.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getCsrfToken(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String csrfToken = (String) session.getAttribute("CSRF_TOKEN");

        if (csrfToken == null) {
            csrfToken = new BigInteger(130, new SecureRandom()).toString(32);
            session.setAttribute("CSRF_TOKEN", csrfToken);
        }

        CSRFToken token = new CSRFToken(csrfToken);
        return Response.ok(token).build();
    }
}
