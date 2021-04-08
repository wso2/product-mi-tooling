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

import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.rest.annotation.Secured;
import org.wso2.ei.dashboard.core.rest.delegates.groups.GroupDelegate;
import org.wso2.ei.dashboard.core.rest.delegates.nodes.NodesDelegate;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.AddUserRequest;
import org.wso2.ei.dashboard.core.rest.model.ArtifactUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.Artifacts;
import org.wso2.ei.dashboard.core.rest.model.DatasourceList;
import org.wso2.ei.dashboard.core.rest.model.Error;
import org.wso2.ei.dashboard.core.rest.model.GroupList;
import org.wso2.ei.dashboard.core.rest.model.LocalEntryValue;
import org.wso2.ei.dashboard.core.rest.model.LogConfigAddRequest;
import org.wso2.ei.dashboard.core.rest.model.LogConfigUpdateRequest;
import org.wso2.ei.dashboard.core.rest.model.LogConfigs;
import org.wso2.ei.dashboard.core.rest.model.LogList;
import org.wso2.ei.dashboard.core.rest.model.SuccessStatus;

import java.io.File;

import org.wso2.ei.dashboard.core.rest.model.NodeList;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.wso2.ei.dashboard.core.rest.model.Users;
import org.wso2.ei.dashboard.micro.integrator.delegates.ApisDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.CarbonAppsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.ConnectorsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.DataServicesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.EndpointsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.InboundEndpointDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.LocalEntriesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.LogConfigDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.LogsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.MessageProcessorsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.MessageStoresDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.ProxyServiceDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.SequencesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.TasksDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.TemplatesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.UsersDelegate;

import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Secured
@Path("/groups")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2020-12-15T14:16:00.637+05:30[Asia/Colombo]")
public class GroupsApi {

    @POST
    @Path("/{group-id}/log-configs")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Add logger", description = "", tags={ "logConfigs" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Logger insert status",
                     content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response addLogger(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid LogConfigAddRequest request) {
        LogConfigDelegate logConfigDelegate = new LogConfigDelegate();
        Ack ack = logConfigDelegate.addLogger(groupId, request);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(ack);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @PATCH
    @Path("/{group-id}/log-configs")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update log level", description = "", tags={ "logConfigs" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logger update status",
                         content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                         content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response updateLogLevel(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid LogConfigUpdateRequest request) {
        LogConfigDelegate logConfigDelegate = new LogConfigDelegate();
        Ack ack = logConfigDelegate.updateLogLevel(groupId, request);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(ack);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
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
    public Response addUser(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid AddUserRequest request) {
        UsersDelegate usersDelegate = new UsersDelegate();
        Ack ack = usersDelegate.addUser(groupId, request);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(ack);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @DELETE
    @Path("/{group-id}/users/{user-id}")
    @Produces({ "application/json" })
    @Operation(summary = "Delete user", description = "", tags={ "Users" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deletion status",
                         content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error", content =
            @Content(schema = @Schema(implementation = Error.class)))
    }) public Response deleteUser(
            @PathParam("group-id") @Parameter(description = "Group ID") String groupId,
            @PathParam("user-id") @Parameter(description = "User ID") String userId) {
        UsersDelegate usersDelegate = new UsersDelegate();
        Ack ack = usersDelegate.deleteUser(groupId, userId);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(ack);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/nodes/{node-id}/logs/{file-name}")
    @Produces({ "text/plain" })
    @Operation(summary = "Get log content", description = "", tags={ "logFiles" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Get log file content.",
                     content = @Content(schema = @Schema(implementation = File.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    }) public Response getLogContent(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @PathParam("node-id") @Parameter(description = "Node id of the file") String nodeId,
            @PathParam("file-name") @Parameter(description = "Log file name") String fileName) {
        LogsDelegate logsDelegate = new LogsDelegate();
        String logContent = logsDelegate.getLogByName(groupId, nodeId, fileName);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(logContent);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/apis")
    @Produces({ "application/json" })
    @Operation(summary = "Get APIs by node ids", description = "", tags={ "apis" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of APIs deployed in provided nodes", content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getApisByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        ApisDelegate apisDelegate = new ApisDelegate();
        Artifacts apiList = apisDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(apiList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/capps")
    @Produces({ "application/json" })
    @Operation(summary = "Get carbon applications by node ids", description = "", tags={ "carbonApplications" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of carbon applications deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getCarbonApplicationsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        CarbonAppsDelegate cappsDelegate = new CarbonAppsDelegate();
        Artifacts cappList = cappsDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(cappList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/connectors")
    @Produces({ "application/json" })
    @Operation(summary = "Get connectors by node ids", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of connectors deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getConnectorsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        ConnectorsDelegate connectorsDelegate = new ConnectorsDelegate();
        Artifacts connectorList = connectorsDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(connectorList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/data-services")
    @Produces({ "application/json" })
    @Operation(summary = "Get data-services by node ids", description = "", tags={ "data-services" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of data-services deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getDataServicesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        DataServicesDelegate dataServicesDelegate = new DataServicesDelegate();
        Artifacts dataServicesList = dataServicesDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(dataServicesList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/datasources")
    @Produces({ "application/json" })
    @Operation(summary = "Get datasources by node ids", description = "", tags={ "datasources" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of datsources deployed in provided nodes", content = @Content(schema = @Schema(implementation = DatasourceList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getDatasourcesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {
        Response.ResponseBuilder responseBuilder = Response.ok().entity("magic!");
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/endpoints")
    @Produces({ "application/json" })
    @Operation(summary = "Get endpoints by node ids", description = "", tags={ "endpoints" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of endpoints deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getEndpointsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        EndpointsDelegate endpointsDelegate = new EndpointsDelegate();
        Artifacts endpointList = endpointsDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(endpointList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/inbound-endpoints")
    @Produces({ "application/json" })
    @Operation(summary = "Get inbound endpoints by node ids", description = "", tags={ "inboundEndpoints" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of inbound endpoints deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getInboundEpsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        InboundEndpointDelegate inboundEndpointDelegate = new InboundEndpointDelegate();
        Artifacts inboundEndpointList = inboundEndpointDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(inboundEndpointList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/local-entries")
    @Produces({ "application/json" })
    @Operation(summary = "Get local entries by node ids", description = "", tags={ "localEntries" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of local entries deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getLocalEntriesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        LocalEntriesDelegate localEntriesDelegate = new LocalEntriesDelegate();
        Artifacts localEntriesList = localEntriesDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(localEntriesList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/nodes/{node-id}/local-entries/{local-entry}/value")
    @Produces({ "application/json" })
    @Operation(summary = "Get value of local entry", description = "", tags={ "localEntries" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Value of the local entry",
                         content = @Content(schema = @Schema(implementation = LocalEntryValue.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                         content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getLocalEntryValue(
            @PathParam("group-id") @Parameter(description = "Group id of the node") String groupId,
            @PathParam("node-id") @Parameter(description = "Node id") String nodeId,
            @PathParam("local-entry") @Parameter(description = "Local entry name") String localEntry) {

        LocalEntriesDelegate localEntriesDelegate = new LocalEntriesDelegate();
        LocalEntryValue localEntryValue = localEntriesDelegate.getValue(groupId, nodeId, localEntry);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(localEntryValue);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/log-configs")
    @Produces({ "application/json" })
    @Operation(summary = "Get log configs", description = "", tags={ "logConfigs" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of log configs",
                     content = @Content(schema = @Schema(implementation = LogConfigs.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    }) public Response getLogConfigs(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId) {
        LogConfigDelegate logConfigDelegate = new LogConfigDelegate();
        LogConfigs logConfigs = logConfigDelegate.fetchLogConfigs(groupId);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(logConfigs);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/log-configs/nodes/{node-id}")
    @Produces({ "application/json" })
    @Operation(summary = "Get log configs by node id", description = "", tags={ "logConfigs" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of log configs in provided node id",
                         content = @Content(schema = @Schema(implementation = LogConfigs.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                         content = @Content(schema = @Schema(implementation = Error.class)))
    }) public Response getLogConfigsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @PathParam("node-id") @Parameter(description = "NodeId") String nodeId) {
        LogConfigDelegate logConfigDelegate = new LogConfigDelegate();
        LogConfigs logConfigs = logConfigDelegate.fetchLogConfigsByNodeId(groupId, nodeId);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(logConfigs);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @PATCH
    @Path("/{group-id}/log-configs/nodes/{node-id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update log level by nodeId", description = "", tags={ "logConfigs" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logger update status",
                         content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                         content = @Content(schema = @Schema(implementation = Error.class)))
    }) public Response updateLogLevelByNodeId(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @PathParam("node-id") @Parameter(description = "NodeId") String nodeId,
            @Valid LogConfigUpdateRequest request) {
        LogConfigDelegate logConfigDelegate = new LogConfigDelegate();
        Ack ack = logConfigDelegate.updateLogLevelByNodeId(groupId, nodeId, request);
        return Response.ok().entity(ack).build();
    }

    @GET
    @Path("/{group-id}/logs")
    @Produces({ "application/json" })
    @Operation(summary = "Get log files by node ids", description = "", tags={ "logFiles" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of log files of provided nodes",
                     content = @Content(schema = @Schema(implementation = LogList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getLogFilesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        LogsDelegate logsDelegate = new LogsDelegate();
        LogList logList = logsDelegate.getLogsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(logList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/message-processors")
    @Produces({ "application/json" })
    @Operation(summary = "Get message processors by node ids", description = "", tags={ "messageProcessors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of message processorss deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getMessageProcessorsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        MessageProcessorsDelegate messageProcessorsDelegate = new MessageProcessorsDelegate();
        Artifacts messageProcessorList = messageProcessorsDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(messageProcessorList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/message-stores")
    @Produces({ "application/json" })
    @Operation(summary = "Get message stores by node ids", description = "", tags={ "messageStores" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of message stores deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getMessageStoresByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        MessageStoresDelegate messageStoresDelegate = new MessageStoresDelegate();
        Artifacts messageStoresList = messageStoresDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(messageStoresList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/proxy-services")
    @Produces({ "application/json" })
    @Operation(summary = "Get proxy services by node ids", description = "", tags={ "proxyServices" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of proxy services deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getProxyServicesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes) {
        ProxyServiceDelegate proxyServiceDelegate = new ProxyServiceDelegate();
        Artifacts proxyList = proxyServiceDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(proxyList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/sequences")
    @Produces({ "application/json" })
    @Operation(summary = "Get sequences by node ids", description = "", tags={ "sequences" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of sequences deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    }) public Response getSequencesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {
        SequencesDelegate sequencesDelegate = new SequencesDelegate();
        Artifacts sequenceList = sequencesDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(sequenceList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/tasks")
    @Produces({ "application/json" })
    @Operation(summary = "Get tasks by node ids", description = "", tags={ "tasks" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of tasks deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getTasksByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {

        TasksDelegate tasksDelegate = new TasksDelegate();
        Artifacts tasksList = tasksDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(tasksList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/templates")
    @Produces({ "application/json" })
    @Operation(summary = "Get templates by node ids", description = "", tags={ "templates" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of templates deployed in provided nodes",
                     content = @Content(schema = @Schema(implementation = Artifacts.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getTemplatesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull  @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes")  List<String> nodes) {
        TemplatesDelegate templatesDelegate = new TemplatesDelegate();
        Artifacts templateList =templatesDelegate.getArtifactsList(groupId, nodes);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(templateList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Path("/{group-id}/users")
    @Produces({ "application/json" })
    @Operation(summary = "Get users", description = "", tags={ "Users" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "List of users",
                     content = @Content(schema = @Schema(implementation = Users.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getUsers(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId) {

        UsersDelegate usersDelegate = new UsersDelegate();
        Users users = usersDelegate.fetchUsers(groupId);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(users);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }
    @GET
    @Produces({ "application/json" })
    @Operation(summary = "Get set of groups", description = "", tags={ "groups" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "The list of groups registered to dashboard", content = @Content(schema = @Schema(implementation = GroupList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response retrieveGroups() {
        GroupDelegate groupDelegate = new GroupDelegate();
        GroupList groupList = groupDelegate.getGroupList();
        Response.ResponseBuilder responseBuilder = Response.ok().entity(groupList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/nodes")
    @Produces({ "application/json" })
    @Operation(summary = "Get set of nodes in the group", description = "", tags={ "nodes" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "The list of nodes in group",
                     content = @Content(schema = @Schema(implementation = NodeList.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response retrieveNodesByGroupId(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId) {

        NodesDelegate nodesDeligate = new NodesDelegate();
        NodeList nodeList = nodesDeligate.getNodes(groupId);
        Response.ResponseBuilder responseBuilder = Response.ok().entity(nodeList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @PATCH
    @Path("/{group-id}/apis")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update API", description = "", tags={ "apis" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "API update status",
                     content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Ack updateApi(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) {
        ApisDelegate apisDelegate = new ApisDelegate();
        return apisDelegate.updateArtifact(groupId, request);
    }
    @PATCH
    @Path("/{group-id}/endpoints")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update endpoint", description = "", tags={ "endpoints" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Endpoint update status",
                     content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Ack updateEndpoint(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) {
        EndpointsDelegate endpointsDelegate = new EndpointsDelegate();
        return endpointsDelegate.updateArtifact(groupId, request);
    }
    @PATCH
    @Path("/{group-id}/inbound-endpoints")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update inbound endpoint", description = "", tags={ "inboundEndpoints" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Inbound endpoint update status",
                     content = @Content(schema = @Schema(implementation = Ack.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Ack updateInboundEp(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) {
        InboundEndpointDelegate inboundEndpointDelegate = new InboundEndpointDelegate();
        return inboundEndpointDelegate.updateArtifact(groupId, request);
    }
    @PATCH
    @Path("/{group-id}/message-processors")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update message processor", description = "", tags={ "messageProcessors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Message processor update status",
                     content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Ack updateMessageProcessor(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) {

        MessageProcessorsDelegate messageProcessorsDelegate = new MessageProcessorsDelegate();
        return messageProcessorsDelegate.updateArtifact(groupId, request);
    }
    @PATCH
    @Path("/{group-id}/proxy-services")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update proxy service", description = "", tags={ "proxyServices" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Proxy update status",
                     content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Ack updateProxyService(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) {
        ProxyServiceDelegate proxyServiceDelegate = new ProxyServiceDelegate();
        return proxyServiceDelegate.updateArtifact(groupId, request);
    }
    @PATCH
    @Path("/{group-id}/sequences")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update sequence", description = "", tags={ "sequences" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Sequence update status",
                     content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error",
                     content = @Content(schema = @Schema(implementation = Error.class)))})
    public Ack updateSequence(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) {

        SequencesDelegate sequencesDelegate = new SequencesDelegate();
        return sequencesDelegate.updateArtifact(groupId, request);
    }}
