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

import org.wso2.ei.dashboard.core.rest.model.DatasourceList;
import org.wso2.ei.dashboard.core.rest.model.EndpointUpdateRequestBody;
import org.wso2.ei.dashboard.core.rest.model.Error;
import org.wso2.ei.dashboard.core.rest.model.GroupList;
import org.wso2.ei.dashboard.core.rest.model.InboundEpList;
import org.wso2.ei.dashboard.core.rest.model.InboundEpUpdateRequestBody;
import org.wso2.ei.dashboard.core.rest.model.LocalEntryList;
import org.wso2.ei.dashboard.core.rest.model.LogConfig;
import org.wso2.ei.dashboard.core.rest.model.LogList;
import org.wso2.ei.dashboard.core.rest.model.MessageProcessorList;
import org.wso2.ei.dashboard.core.rest.model.MessageProcessorUpdateRequestBody;
import org.wso2.ei.dashboard.core.rest.model.ProxyList;
import org.wso2.ei.dashboard.core.rest.model.SequenceList;
import org.wso2.ei.dashboard.core.rest.model.SequenceUpdateRequestBody;
import org.wso2.ei.dashboard.core.rest.model.SuccessStatus;
import org.wso2.ei.dashboard.core.rest.model.TaskList;
import org.wso2.ei.dashboard.core.rest.model.TemplateList;
import org.wso2.ei.dashboard.core.rest.model.UserAddRequestBody;
import org.wso2.ei.dashboard.core.rest.model.ApiList;
import org.wso2.ei.dashboard.core.rest.model.ApiUpdateRequestBody;
import org.wso2.ei.dashboard.core.rest.model.CAppList;
import org.wso2.ei.dashboard.core.rest.model.ConnectorList;
import org.wso2.ei.dashboard.core.rest.model.DataserviceList;
import org.wso2.ei.dashboard.core.rest.model.EndpointList;

import java.io.File;

import org.wso2.ei.dashboard.core.rest.model.LogConfigAddRequestBody;
import org.wso2.ei.dashboard.core.rest.model.MessageStoreList;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.ProxyUpdateRequestBody;
import org.wso2.ei.dashboard.core.rest.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/groups")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2020-12-15T14:16:00.637+05:30[Asia/Colombo]")
public class GroupsApi {

    @POST
    @Path("/{group-id}/log-configs")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Add logger", description = "", tags={ "logConfigs" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Logger insert status", content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response addLogger( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
,@Valid LogConfigAddRequestBody body) {
        return Response.ok().entity("magic!").build();
    }
    @POST
    @Path("/{group-id}/users")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Add user", description = "", tags={ "Users" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "User insert status", content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response addUser( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
,@Valid UserAddRequestBody body) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/logs/{file-name}/nodes/{node-id}")
    @Produces({ "binary/octet-stream", "application/json" })
    @Operation(summary = "Download log file", description = "", tags={ "logFiles" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Get log file in .log format", content = @Content(schema = @Schema(implementation = File.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response downloadLogFile( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @PathParam("file-name")

 @Parameter(description = "Log file name") String fileName
, @PathParam("node-id")

 @Parameter(description = "Node id of the file") String nodeId
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/apis")
    @Produces({ "application/json" })
    @Operation(summary = "Get APIs by node ids", description = "", tags={ "apis" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of APIs deployed in provided nodes", content = @Content(schema = @Schema(implementation = ApiList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getApisByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/capps")
    @Produces({ "application/json" })
    @Operation(summary = "Get carbon applications by node ids", description = "", tags={ "carbonApplications" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of carbon applications deployed in provided nodes", content = @Content(schema = @Schema(implementation = CAppList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getCarbonApplicationsByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/connectors")
    @Produces({ "application/json" })
    @Operation(summary = "Get connectors by node ids", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of connectors deployed in provided nodes", content = @Content(schema = @Schema(implementation = ConnectorList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getConnectorsByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/dataservices")
    @Produces({ "application/json" })
    @Operation(summary = "Get dataservices by node ids", description = "", tags={ "dataservices" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of dataservices deployed in provided nodes", content = @Content(schema = @Schema(implementation = DataserviceList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getDataservicesByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/datasources")
    @Produces({ "application/json" })
    @Operation(summary = "Get datasources by node ids", description = "", tags={ "datasources" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of datsources deployed in provided nodes", content = @Content(schema = @Schema(implementation = DatasourceList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getDatasourcesByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/endpoints")
    @Produces({ "application/json" })
    @Operation(summary = "Get endpoints by node ids", description = "", tags={ "endpoints" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of endpoints deployed in provided nodes", content = @Content(schema = @Schema(implementation = EndpointList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getEndpointsByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/inbound-endpoints")
    @Produces({ "application/json" })
    @Operation(summary = "Get inbound endpoints by node ids", description = "", tags={ "inboundEndpoints" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of inbound endpoints deployed in provided nodes", content = @Content(schema = @Schema(implementation = InboundEpList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getInboundEpsByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/local-entries")
    @Produces({ "application/json" })
    @Operation(summary = "Get local entries by node ids", description = "", tags={ "localEntries" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of local entries deployed in provided nodes", content = @Content(schema = @Schema(implementation = LocalEntryList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getLocalEntriesByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/log-configs")
    @Produces({ "application/json" })
    @Operation(summary = "Get log configs", description = "", tags={ "logConfigs" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of log configs", content = @Content(schema = @Schema(implementation = LogConfig.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getLogConfigs( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/logs")
    @Produces({ "application/json" })
    @Operation(summary = "Get log files by node ids", description = "", tags={ "logFiles" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of log files of provided nodes", content = @Content(schema = @Schema(implementation = LogList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getLogFilesByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/message-processors")
    @Produces({ "application/json" })
    @Operation(summary = "Get message processors by node ids", description = "", tags={ "messageProcessors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of message processorss deployed in provided nodes", content = @Content(schema = @Schema(implementation = MessageProcessorList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getMessageProcessorsByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/message-stores")
    @Produces({ "application/json" })
    @Operation(summary = "Get message stores by node ids", description = "", tags={ "messageStores" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of message stores deployed in provided nodes", content = @Content(schema = @Schema(implementation = MessageStoreList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getMessageStoresByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/proxy-services")
    @Produces({ "application/json" })
    @Operation(summary = "Get proxy services by node ids", description = "", tags={ "proxyServices" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of proxy services deployed in provided nodes", content = @Content(schema = @Schema(implementation = ProxyList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getProxyServicesByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/sequences")
    @Produces({ "application/json" })
    @Operation(summary = "Get sequences by node ids", description = "", tags={ "sequences" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of sequences deployed in provided nodes", content = @Content(schema = @Schema(implementation = SequenceList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getSequencesByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/tasks")
    @Produces({ "application/json" })
    @Operation(summary = "Get tasks by node ids", description = "", tags={ "tasks" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of tasks deployed in provided nodes", content = @Content(schema = @Schema(implementation = TaskList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getTasksByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/templates")
    @Produces({ "application/json" })
    @Operation(summary = "Get templates by node ids", description = "", tags={ "templates" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of templates deployed in provided nodes", content = @Content(schema = @Schema(implementation = TemplateList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getTemplatesByNodeIds( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
, @NotNull  @QueryParam("nodes") 

 @Parameter(description = "ID/IDs of the nodes")  List<String> nodes
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/users")
    @Produces({ "application/json" })
    @Operation(summary = "Get users", description = "", tags={ "Users" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of users", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getUsers( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
) {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Produces({ "application/json" })
    @Operation(summary = "Get set of groups", description = "", tags={ "groups" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "The list of groups registered to dashboard", content = @Content(schema = @Schema(implementation = GroupList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response retrieveGroups() {
        return Response.ok().entity("magic!").build();
    }
    @GET
    @Path("/{group-id}/nodes")
    @Produces({ "application/json" })
    @Operation(summary = "Get set of nodes in the group", description = "", tags={ "nodes" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "The list of nodes in group", content = @Content(schema = @Schema(implementation = NodeList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response retrieveNodesByGroupId( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
) {
        return Response.ok().entity("magic!").build();
    }
    @PUT
    @Path("/{group-id}/apis")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update API", description = "", tags={ "apis" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "API update status", content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response updateApi( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
,@Valid ApiUpdateRequestBody body) {
        return Response.ok().entity("magic!").build();
    }
    @PUT
    @Path("/{group-id}/endpoints")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update endpoint", description = "", tags={ "endpoints" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Endpoint update status", content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response updateEndpoint( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
,@Valid EndpointUpdateRequestBody body) {
        return Response.ok().entity("magic!").build();
    }
    @PUT
    @Path("/{group-id}/inbound-endpoints")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update inbound endpoint", description = "", tags={ "inboundEndpoints" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Inbound endpoint update status", content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response updateInboundEp( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
,@Valid InboundEpUpdateRequestBody body) {
        return Response.ok().entity("magic!").build();
    }
    @PUT
    @Path("/{group-id}/message-processors")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update message processor", description = "", tags={ "messageProcessors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Message processor update status", content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response updateMessageProcessor( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
,@Valid MessageProcessorUpdateRequestBody body) {
        return Response.ok().entity("magic!").build();
    }
    @PUT
    @Path("/{group-id}/proxy-services")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update proxy service", description = "", tags={ "proxyServices" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Proxy update status", content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response updateProxyService( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
,@Valid ProxyUpdateRequestBody body) {
        return Response.ok().entity("magic!").build();
    }
    @PUT
    @Path("/{group-id}/sequences")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update sequence", description = "", tags={ "sequences" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Sequence update status", content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response updateSequence( @PathParam("group-id")

 @Parameter(description = "Group ID of the node") String groupId
,@Valid SequenceUpdateRequestBody body) {
        return Response.ok().entity("magic!").build();
    }}
