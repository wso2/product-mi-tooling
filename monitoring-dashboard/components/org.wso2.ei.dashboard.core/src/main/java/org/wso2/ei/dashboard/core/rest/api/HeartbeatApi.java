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

package org.wso2.ei.dashboard.core.rest.api;

import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.HeartBeatDelegate;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.Error;
import org.wso2.ei.dashboard.core.rest.model.HeatbeatSignalRequestBody;

import javax.ws.rs.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;

@Path("/heartbeat")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2020-12-15T13:04:46.809+05:30[Asia/Colombo]")
public class HeartbeatApi {
    HeartBeatDelegate heartBeatDelegate = new HeartBeatDelegate();

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Receive heartbeats from nodes", description = "", tags={ "heartbeat" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Receive node heartbeats", content = @Content(schema = @Schema(implementation = Ack.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Ack receiveNodeHeartbeat(@Valid HeatbeatSignalRequestBody body) {
        return heartBeatDelegate.processHeartbeat(body);
    }}
