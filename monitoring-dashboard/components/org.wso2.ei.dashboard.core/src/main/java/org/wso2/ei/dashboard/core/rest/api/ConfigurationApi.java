package org.wso2.ei.dashboard.core.rest.api;

import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.delegates.ConfigurationDelegate;
import org.wso2.ei.dashboard.core.rest.model.Error;
import org.wso2.ei.dashboard.core.rest.model.ModelConfiguration;

import javax.ws.rs.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;

@Path("/configuration")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2021-02-01T21:22:37.175+05:30[Asia/Colombo]")
public class ConfigurationApi {

    @GET
    @Produces({ "application/json" })
    @Operation(summary = "Get configurations using management api", description = "", tags={ "configuration" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "The configuration of the relevant artifact",
                     content = @Content(schema = @Schema(implementation = ModelConfiguration.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public ModelConfiguration retrieveConfiguration(
            @NotNull  @QueryParam("groupId") @Parameter(description = "Group id of the node")  String groupId,
            @NotNull  @QueryParam("nodeId") @Parameter(description = "Node id of the node")  String nodeId,
            @NotNull  @QueryParam("artifactType") @Parameter(description = "Type of the artifact")  String artifactType,
            @NotNull  @QueryParam("artifactName") @Parameter(description = "Name of the artifact")  String artifactName) throws ManagementApiException {
        ConfigurationDelegate configurationDelegate = new ConfigurationDelegate(groupId, nodeId, artifactType, artifactName);
        return configurationDelegate.getConfiguration();
    }}
