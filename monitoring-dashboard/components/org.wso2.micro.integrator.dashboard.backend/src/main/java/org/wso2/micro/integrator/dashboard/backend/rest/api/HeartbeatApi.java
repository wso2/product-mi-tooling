package org.wso2.micro.integrator.dashboard.backend.rest.api;

import org.wso2.micro.integrator.dashboard.backend.rest.delegates.heartbeat.HeartBeatDelegate;
import org.wso2.micro.integrator.dashboard.backend.rest.model.Ack;
import org.wso2.micro.integrator.dashboard.backend.rest.model.Error;
import org.wso2.micro.integrator.dashboard.backend.rest.model.HeatbeatSignalRequestBody;

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
