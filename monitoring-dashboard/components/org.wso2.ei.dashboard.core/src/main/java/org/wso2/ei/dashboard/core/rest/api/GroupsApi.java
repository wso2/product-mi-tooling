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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.security.user.core.common.DashboardUserStoreException;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.annotation.Secured;
import org.wso2.ei.dashboard.core.rest.delegates.groups.GroupDelegate;
import org.wso2.ei.dashboard.core.rest.delegates.nodes.NodesDelegate;
import org.wso2.ei.dashboard.core.rest.model.Error;
import org.wso2.ei.dashboard.core.rest.model.*;
import org.wso2.ei.dashboard.micro.integrator.delegates.ApisDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.CarbonAppsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.ConnectorsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.DataServicesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.DataSourcesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.EndpointsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.InboundEndpointDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.ListenersDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.LocalEntriesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.LogConfigDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.LogsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.MessageProcessorsDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.MessageStoresDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.ProxyServiceDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.RegistryResourceDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.RolesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.SequencesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.ServicesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.TasksDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.TemplatesDelegate;
import org.wso2.ei.dashboard.micro.integrator.delegates.UsersDelegate;
import org.wso2.micro.integrator.security.user.api.UserStoreException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.File;
import java.util.List;

@Secured
@Path("/groups")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2020-12-15T14:16:00.637+05:30[Asia/Colombo]")
public class GroupsApi {
    private static final Log logger = LogFactory.getLog(GroupsApi.class);

    @POST
    @Path("/{group-id}/log-configs")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Add logger", description = "", tags = {"logConfigs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logger insert status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response addLogger(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid LogConfigAddRequest request) throws ManagementApiException {
        LogConfigDelegate logConfigDelegate = new LogConfigDelegate();
        Ack ack = logConfigDelegate.addLogger(groupId, request);
        ResponseBuilder responseBuilder = Response.ok().entity(ack);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @PATCH
    @Path("/{group-id}/log-configs")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update log level", description = "", tags = {"logConfigs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logger update status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response updateLogLevel(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid LogConfigUpdateRequest request) throws ManagementApiException {
        LogConfigDelegate logConfigDelegate = new LogConfigDelegate();
        Ack ack = logConfigDelegate.updateLogLevel(groupId, request);
        ResponseBuilder responseBuilder = Response.ok().entity(ack);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @POST
    @Path("/{group-id}/users")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Add user", description = "", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User insert status", content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response addUser(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid AddUserRequest request) {
        try {
            Ack ack = UsersDelegate.getDelegate(groupId).addUser(request);
            ResponseBuilder responseBuilder = Response.ok().entity(ack);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        } catch (DashboardUserStoreException e) {
            return handleDashboardUserStoreException(e);
        } catch (UserStoreException e) {
            return handleUserStoreException(e);
        } catch (ManagementApiException e) {
            return handleManagementApiException(e);
        }
    }

    private Response handleDashboardUserStoreException(DashboardUserStoreException e) {
        Error error = getError(e);
        int errorCode = getHttpErrorCode(error.getCode(), 400);
        ResponseBuilder responseBuilder = Response.status(errorCode).entity(error);
        return responseBuilder.build();
    }

    private static int getHttpErrorCode(int code, int defaultCode) {
        return isHttpErrorCode(code) ? code : defaultCode;
    }

    private static boolean isHttpErrorCode(int code) {
        return (code >= 400 && code < 600);
    }

    private static Error getError(DashboardUserStoreException e) {
        Error error = new Error();
        error.setCode(parseIntOrDefault(e.getErrorCode(), 500));
        error.setMessage(e.getMessage());
        return error;
    }

    private static int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static Response handleUserStoreException(UserStoreException exception) {
        ResponseBuilder responseBuilder = Response.status(400).entity(getError(exception));
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    private static Error getError(UserStoreException exception) {
        Error error = new Error();
        error.setMessage(exception.getMessage());
        return error;
    }

    private static Response handleManagementApiException(ManagementApiException exception) {
        ResponseBuilder responseBuilder = Response.status(exception.getErrorCode()).entity(getError(exception));
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    private static Error getError(ManagementApiException e) {
        Error error = new Error();
        error.setCode(e.getErrorCode());
        error.setMessage(e.getMessage());
        return error;
    }

    @PATCH
    @Path("/{group-id}/user/password")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Change user password", description = "", tags = {"Password"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password update status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response updatePassword(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid PasswordRequest request, @CookieParam("JWT_TOKEN") String accessToken,
            @Context ContainerRequestContext requestContext) {
        String performedBy = (String) requestContext.getProperty("performedBy");
        try {
            Ack ack = UsersDelegate.getDelegate(groupId).updateUserPassword(request, accessToken, performedBy);
            ResponseBuilder responseBuilder = Response.ok().entity(ack);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        } catch (DashboardUserStoreException e) {
            return handleDashboardUserStoreException(e);
        } catch (UserStoreException e) {
            return handleUserStoreException(e);
        } catch (ManagementApiException e) {
            return handleManagementApiException(e);
        }
    }

    @DELETE
    @Path("/{group-id}/users/{user-id}")
    @Produces({"application/json"})
    @Operation(summary = "Delete user", description = "", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deletion status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error", content =
            @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response deleteUser(
            @PathParam("group-id") @Parameter(description = "Group ID") String groupId,
            @PathParam("user-id") @Parameter(description = "User ID") String userId,
            @QueryParam("domain") @Parameter(description = "domain name") String domain,
            @Context ContainerRequestContext requestContext) {
        String performedBy = (String) requestContext.getProperty("performedBy");
        try {
            Ack ack = UsersDelegate.getDelegate(groupId).deleteUser(userId, domain, performedBy);
            ResponseBuilder responseBuilder = Response.ok().entity(ack);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        } catch (DashboardUserStoreException e) {
            return handleDashboardUserStoreException(e);
        } catch (UserStoreException e) {
            return handleUserStoreException(e);
        } catch (ManagementApiException e) {
            return handleManagementApiException(e);
        } catch (IllegalArgumentException e) {
            Ack ack = new Ack("403");
            ack.setMessage(e.getMessage());
            ResponseBuilder responseBuilder = Response.status(Response.Status.FORBIDDEN).entity(ack);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        }
    }

    @GET
    @Path("/{group-id}/nodes/{node-id}/logs/{file-name}")
    @Produces({"text/plain"})
    @Operation(summary = "Get log content", description = "", tags = {"logFiles"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get log file content.",
                    content = @Content(schema = @Schema(implementation = File.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getLogContent(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @PathParam("node-id") @Parameter(description = "Node id of the file") String nodeId,
            @PathParam("file-name") @Parameter(description = "Log file name") String fileName) throws ManagementApiException {
        LogsDelegate logsDelegate = new LogsDelegate();
        String logContent = logsDelegate.getLogByName(groupId, nodeId, fileName);
        ResponseBuilder responseBuilder = Response.ok().entity(logContent);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/apis")
    @Produces({"application/json"})
    @Operation(summary = "Get APIs by node ids", description = "", tags = {"apis"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of APIs deployed in provided nodes", content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getApisByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        ApisDelegate apisDelegate = new ApisDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get apis");
        try {
            ArtifactsResourceResponse apiList = apisDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(apiList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/capps")
    @Produces({"application/json"})
    @Operation(summary = "Get carbon applications by node ids", description = "", tags = {"carbonApplications"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of carbon applications deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getCarbonApplicationsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        CarbonAppsDelegate cappsDelegate = new CarbonAppsDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get CApps");
        try {
            ArtifactsResourceResponse cappList = cappsDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(cappList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/nodes/{node-id}/capps/{capp-name}/artifacts")
    @Produces({"application/json"})
    @Operation(summary = "Get artifact list of carbon application by node id", description = "",
            tags = {"carbonApplications"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of artifacts in carbon applications deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = CAppArtifacts.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getCarbonApplicationArtifactsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @PathParam("node-id") @Parameter(description = "Node ID") String nodeId,
            @PathParam("capp-name") @Parameter(description = "Carbon application name") String cappName)
            throws ManagementApiException {
        CarbonAppsDelegate cappsDelegate = new CarbonAppsDelegate();
        CAppArtifacts cAppArtifactList = cappsDelegate.getCAppArtifactList(groupId, nodeId, cappName);
        ResponseBuilder responseBuilder = Response.ok().entity(cAppArtifactList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/connectors")
    @Produces({"application/json"})
    @Operation(summary = "Get connectors by node ids", description = "", tags = {"connectors"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of connectors deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getConnectorsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        ConnectorsDelegate connectorsDelegate = new ConnectorsDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Connectors");
        try {
            ArtifactsResourceResponse connectorList = connectorsDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(connectorList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/data-services")
    @Produces({"application/json"})
    @Operation(summary = "Get data-services by node ids", description = "", tags = {"data-services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of data-services deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getDataServicesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {
        DataServicesDelegate dataServicesDelegate = new DataServicesDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Data Services");
        try {
            ArtifactsResourceResponse dataServicesList = dataServicesDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(dataServicesList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/datasources")
    @Produces({"application/json"})
    @Operation(summary = "Get datasources by node ids", description = "", tags = {"datasources"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of datsources deployed in provided nodes", content = @Content(schema = @Schema(implementation = DatasourceList.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getDatasourcesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        DataSourcesDelegate dataSourcesDelegate = new DataSourcesDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Data Sources");
        try {
            ArtifactsResourceResponse dataSourcesList = dataSourcesDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(dataSourcesList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/endpoints")
    @Produces({"application/json"})
    @Operation(summary = "Get endpoints by node ids", description = "", tags = {"endpoints"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of endpoints deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getEndpointsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        EndpointsDelegate endpointsDelegate = new EndpointsDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Endpoints");
        try {
            ArtifactsResourceResponse endpointList = endpointsDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(endpointList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/services")
    @Produces({"application/json"})
    @Operation(summary = "Get services by node ids", description = "", tags = {"services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of services deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getServicesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        ServicesDelegate servicesDelegate = new ServicesDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get services");
        try {
            ArtifactsResourceResponse serviceList = servicesDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(serviceList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/listeners")
    @Produces({"application/json"})
    @Operation(summary = "Get listeners by node ids", description = "", tags = {"services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of listeners deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getListenersByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        ListenersDelegate listenersDelegate = new ListenersDelegate();
        Response.ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get services");
        try {
            ArtifactsResourceResponse serviceList = listenersDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(serviceList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/inbound-endpoints")
    @Produces({"application/json"})
    @Operation(summary = "Get inbound endpoints by node ids", description = "", tags = {"inboundEndpoints"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of inbound endpoints deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getInboundEpsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        InboundEndpointDelegate inboundEndpointDelegate = new InboundEndpointDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Inbound Endpoints");
        try {
            ArtifactsResourceResponse inboundEndpointList = inboundEndpointDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(inboundEndpointList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/local-entries")
    @Produces({"application/json"})
    @Operation(summary = "Get local entries by node ids", description = "", tags = {"localEntries"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of local entries deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getLocalEntriesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        LocalEntriesDelegate localEntriesDelegate = new LocalEntriesDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Local Entries");
        try {
            ArtifactsResourceResponse localEntriesList = localEntriesDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(localEntriesList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/nodes/{node-id}/local-entries/{local-entry}/value")
    @Produces({"application/json"})
    @Operation(summary = "Get value of local entry", description = "", tags = {"localEntries"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Value of the local entry",
                    content = @Content(schema = @Schema(implementation = LocalEntryValue.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getLocalEntryValue(
            @PathParam("group-id") @Parameter(description = "Group id of the node") String groupId,
            @PathParam("node-id") @Parameter(description = "Node id") String nodeId,
            @PathParam("local-entry") @Parameter(description = "Local entry name") String localEntry) throws ManagementApiException {

        LocalEntriesDelegate localEntriesDelegate = new LocalEntriesDelegate();
        LocalEntryValue localEntryValue = localEntriesDelegate.getValue(groupId, nodeId, localEntry);
        ResponseBuilder responseBuilder = Response.ok().entity(localEntryValue);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/log-configs")
    @Produces({"application/json"})
    @Operation(summary = "Get log configs", description = "", tags = {"logConfigs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of log configs",
                    content = @Content(schema = @Schema(implementation = LogConfigsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getLogConfigs(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        LogConfigDelegate logConfigDelegate = new LogConfigDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Log Configs");
        try {
            LogConfigsResourceResponse logConfigs = logConfigDelegate.fetchPaginatedLogConfigsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(logConfigs);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @PATCH
    @Path("/{group-id}/log-configs/nodes/{node-id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update log level by nodeId", description = "", tags = {"logConfigs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logger update status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response updateLogLevelByNodeId(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @PathParam("node-id") @Parameter(description = "NodeId") String nodeId,
            @Valid LogConfigUpdateRequest request) throws ManagementApiException {
        LogConfigDelegate logConfigDelegate = new LogConfigDelegate();
        Ack ack = logConfigDelegate.updateLogLevelByNodeId(groupId, nodeId, request);
        return Response.ok().entity(ack).build();
    }

    @GET
    @Path("/{group-id}/logs")
    @Produces({"application/json"})
    @Operation(summary = "Get log files by node ids", description = "", tags = {"logFiles"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of log files of provided nodes",
                    content = @Content(schema = @Schema(implementation = LogsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getLogFilesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        LogsDelegate logsDelegate = new LogsDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Logs");
        try {
            LogsResourceResponse logList = logsDelegate.getPaginatedLogsListResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(logList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/message-processors")
    @Produces({"application/json"})
    @Operation(summary = "Get message processors by node ids", description = "", tags = {"messageProcessors"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of message processorss deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getMessageProcessorsByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        MessageProcessorsDelegate messageProcessorsDelegate = new MessageProcessorsDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API Message Processors");
        try {
            ArtifactsResourceResponse messageProcessorList = messageProcessorsDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(messageProcessorList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/message-stores")
    @Produces({"application/json"})
    @Operation(summary = "Get message stores by node ids", description = "", tags = {"messageStores"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of message stores deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getMessageStoresByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        MessageStoresDelegate messageStoresDelegate = new MessageStoresDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Message Stores");
        try {
            ArtifactsResourceResponse messageStoresList = messageStoresDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(messageStoresList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/proxy-services")
    @Produces({"application/json"})
    @Operation(summary = "Get proxy services by node ids", description = "", tags = {"proxyServices"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of proxy services deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getProxyServicesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        ProxyServiceDelegate proxyServiceDelegate = new ProxyServiceDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Proxy Services");
        try {
            ArtifactsResourceResponse proxyList = proxyServiceDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(proxyList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/registry-resources/properties")
    @Produces({"application/json"})
    @Operation(summary = "Get registry resource content", description = "", tags = {"registryResources"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get registry resource file content", content = @Content(schema = @Schema(implementation = RegistryArtifacts.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getRegistryResourceProperties(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @QueryParam("path") @Parameter(description = "Path of the registry") String path) throws ManagementApiException {
        RegistryResourceDelegate registryResourceDelegate = new RegistryResourceDelegate();
        List<RegistryProperty> registryContent = registryResourceDelegate.getRegistryProperties(groupId, path);
        ResponseBuilder responseBuilder = Response.ok().entity(registryContent);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/registry-resources")
    @Produces({"application/json"})
    @Operation(summary = "Get registryResources services", description = "", tags = {"registryResources"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of registry resources deployed in provided nodes", content = @Content(schema = @Schema(implementation = RegistryResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getRegistryResources(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("path") @Parameter(description = "Path of the registry") String path,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy) {

        RegistryResourceDelegate registryResourceDelegate = new RegistryResourceDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Registry Resources");
        try {
            RegistryResourceResponse registryArtifacts = registryResourceDelegate.getPaginatedRegistryResponse(groupId, searchKey, lowerLimit, upperLimit, order, orderBy, path);
            responseBuilder = Response.ok().entity(registryArtifacts);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/registry-resources/content")
    @Produces({"application/json"})
    @Operation(summary = "Get registry resource content", description = "", tags = {"registryResources"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get registry resource file content", content = @Content(schema = @Schema(implementation = RegistryArtifacts.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getRegistryResourceContent(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @QueryParam("path") @Parameter(description = "Path of the registry") String path) throws ManagementApiException {
        RegistryResourceDelegate registryResourceDelegate = new RegistryResourceDelegate();
        String registryContent = registryResourceDelegate.getRegistryContent(groupId, path);
        ResponseBuilder responseBuilder = Response.ok().entity(registryContent);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/sequences")
    @Produces({"application/json"})
    @Operation(summary = "Get sequences by node ids", description = "", tags = {"sequences"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of sequences deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getSequencesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {
        SequencesDelegate sequencesDelegate = new SequencesDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Sequences");
        try {
            ArtifactsResourceResponse sequenceList = sequencesDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(sequenceList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/tasks")
    @Produces({"application/json"})
    @Operation(summary = "Get tasks by node ids", description = "", tags = {"tasks"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tasks deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getTasksByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        TasksDelegate tasksDelegate = new TasksDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Tasks");
        try {
            ArtifactsResourceResponse tasksList = tasksDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(tasksList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/templates")
    @Produces({"application/json"})
    @Operation(summary = "Get templates by node ids", description = "", tags = {"templates"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of templates deployed in provided nodes",
                    content = @Content(schema = @Schema(implementation = ArtifactsResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getTemplatesByNodeIds(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @NotNull @QueryParam("nodes") @Parameter(description = "ID/IDs of the nodes") List<String> nodes,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @NotNull @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @NotNull @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate
    ) {

        TemplatesDelegate templatesDelegate = new TemplatesDelegate();
        ResponseBuilder responseBuilder;
        logger.debug("Invoking the Groups API to get Templates");
        try {
            ArtifactsResourceResponse templateList = templatesDelegate.getPaginatedArtifactsResponse(groupId, nodes, searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            responseBuilder = Response.ok().entity(templateList);
        } catch (ManagementApiException e) {
            responseBuilder = Response.status(e.getErrorCode()).entity(getError(e));
        }
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/users")
    @Produces({"application/json"})
    @Operation(summary = "Get users", description = "", tags = {"Users"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users",
                    content = @Content(schema = @Schema(implementation = UsersResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Response getUsers(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate) {
        logger.debug("Invoking the Groups API to get Users");
        try {
            UsersResourceResponse users = UsersDelegate.getDelegate(groupId).fetchPaginatedUsers(searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            ResponseBuilder responseBuilder = Response.ok().entity(users);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        } catch (DashboardUserStoreException e) {
            return handleDashboardUserStoreException(e);
        } catch (UserStoreException e) {
            return handleUserStoreException(e);
        } catch (ManagementApiException e) {
            return handleManagementApiException(e);
        }
    }

    @GET
    @Path("/{group-id}/all-roles")
    @Produces({"application/json"})
    @Operation(summary = "Get roles", description = "", tags = {"Roles"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of roles",
                    content = @Content(schema = @Schema(implementation = RolesResourceResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getRoles(
            @PathParam("group-id") @Parameter(description = "Group ID") String groupId) {
        logger.debug("Invoking the Groups API to get All Roles");
        try {
            RolesResourceResponse roleList = RolesDelegate.getDelegate(groupId).getAllRoles();
            ResponseBuilder responseBuilder = Response.ok().entity(roleList);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        } catch (DashboardUserStoreException e) {
            return handleDashboardUserStoreException(e);
        } catch (UserStoreException e) {
            return handleUserStoreException(e);
        } catch (ManagementApiException e) {
            return handleManagementApiException(e);
        }
    }

    @GET
    @Path("/{group-id}/roles")
    @Produces({"application/json"})
    @Operation(summary = "Get roles", description = "", tags = {"Roles"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of roles",
                    content = @Content(schema = @Schema(implementation = RolesResourceResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response getRoles(
            @PathParam("group-id") @Parameter(description = "Group ID") String groupId,
            @QueryParam("searchKey") @Parameter(description = "Search key") String searchKey,
            @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit,
            @QueryParam("order") @Parameter(description = "Order") String order,
            @QueryParam("orderBy") @Parameter(description = "Order By") String orderBy,
            @QueryParam("isUpdate") @Parameter(description = "Whether it is an update") String isUpdate) {
        logger.debug("Invoking the Groups API to get Roles");
        try {
            RolesResourceResponse roleList = RolesDelegate.getDelegate(groupId).fetchPaginatedRolesResponse(searchKey, lowerLimit, upperLimit, order, orderBy, isUpdate);
            ResponseBuilder responseBuilder = Response.ok().entity(roleList);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        } catch (DashboardUserStoreException e) {
            return handleDashboardUserStoreException(e);
        } catch (UserStoreException e) {
            return handleUserStoreException(e);
        } catch (ManagementApiException e) {
            return handleManagementApiException(e);
        }
    }

    @POST
    @Path("/{group-id}/roles")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Add role", description = "", tags = {"Roles"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role insert status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response addRole(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid AddRoleRequest request) {
        try {
            Ack ack = RolesDelegate.getDelegate(groupId).addRole(request);
            ResponseBuilder responseBuilder = Response.ok().entity(ack);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        } catch (DashboardUserStoreException e) {
            return handleDashboardUserStoreException(e);
        } catch (UserStoreException e) {
            return handleUserStoreException(e);
        } catch (ManagementApiException e) {
            return handleManagementApiException(e);
        }
    }

    @PATCH
    @Path("/{group-id}/roles")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update role", description = "", tags = {"Roles"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role update status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response updateRole(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid UpdateRoleRequest request) {
        try {
            Ack ack = RolesDelegate.getDelegate(groupId).updateRole(request);
            ResponseBuilder responseBuilder = Response.ok().entity(ack);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        } catch (DashboardUserStoreException e) {
            return handleDashboardUserStoreException(e);
        } catch (UserStoreException e) {
            return handleUserStoreException(e);
        } catch (ManagementApiException e) {
            return handleManagementApiException(e);
        }
    }

    @DELETE
    @Path("/{group-id}/roles/{role-name}")
    @Produces({"application/json"})
    @Operation(summary = "Delete role", description = "", tags = {"Roles"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deletion status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response deleteRole(
            @PathParam("group-id") @Parameter(description = "Group ID") String groupId,
            @PathParam("role-name") @Parameter(description = "Role Name") String roleName,
            @QueryParam("domain") @Parameter(description = "domain name") String domain) {
        try {
            Ack ack = RolesDelegate.getDelegate(groupId).deleteRole(roleName, domain);
            ResponseBuilder responseBuilder = Response.ok().entity(ack);
            HttpUtils.setHeaders(responseBuilder);
            return responseBuilder.build();
        } catch (DashboardUserStoreException e) {
            return handleDashboardUserStoreException(e);
        } catch (UserStoreException e) {
            return handleUserStoreException(e);
        } catch (ManagementApiException e) {
            return handleManagementApiException(e);
        }
    }

    @GET
    @Produces({"application/json"})
    @Operation(summary = "Get set of groups", description = "", tags = {"groups"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of groups registered to dashboard", content = @Content(schema = @Schema(implementation = GroupList.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response retrieveGroups() {
        GroupDelegate groupDelegate = new GroupDelegate();
        GroupList groupList = groupDelegate.getGroupList();
        ResponseBuilder responseBuilder = Response.ok().entity(groupList);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/all-nodes")
    @Produces({"application/json"})
    @Operation(summary = "Get set of nodes in the group", description = "", tags = {"nodes"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of nodes in group",
                    content = @Content(schema = @Schema(implementation = NodeList.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response retrieveAllNodes(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId) {

        logger.debug("Invoking the Groups API to get All Nodes");
        NodesDelegate nodesDeligate = new NodesDelegate();
        NodeList nodesResponse = nodesDeligate.getNodes(groupId);
        ResponseBuilder responseBuilder = Response.ok().entity(nodesResponse);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/nodes/{product-id}")
    @Produces({"application/json"})
    @Operation(summary = "Get all nodes in the group based on product", description = "", tags = {"nodes"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of nodes in group based on product",
                    content = @Content(schema = @Schema(implementation = NodeList.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response retrieveAllNodesByGroupIdByProductId(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @PathParam("product-id") @Parameter(description = "Product ID") String productId) {
        logger.debug("Retrieving All nodes of product: " + productId + " in group: " + groupId);
        NodesDelegate nodesDeligate = new NodesDelegate();
        NodeList nodesResponse = nodesDeligate.getNodesByProductID(groupId, productId);
        ResponseBuilder responseBuilder = Response.ok().entity(nodesResponse);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @GET
    @Path("/{group-id}/nodes")
    @Produces({"application/json"})
    @Operation(summary = "Get set of nodes in the group", description = "", tags = {"nodes"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of nodes in group",
                    content = @Content(schema = @Schema(implementation = NodesResourceResponse.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response retrieveNodesByGroupId(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @QueryParam("lowerLimit") @Parameter(description = "Lower Limit") String lowerLimit,
            @QueryParam("upperLimit") @Parameter(description = "Upper Limit") String upperLimit) {

        logger.debug("Invoking the Groups API to get Nodes");
        NodesDelegate nodesDeligate = new NodesDelegate();
        NodesResourceResponse nodesResponse = nodesDeligate.getNodesResponse(groupId, lowerLimit, upperLimit);
        ResponseBuilder responseBuilder = Response.ok().entity(nodesResponse);
        HttpUtils.setHeaders(responseBuilder);
        return responseBuilder.build();
    }

    @PATCH
    @Path("/{group-id}/apis")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update API", description = "", tags = {"apis"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API update status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Ack updateApi(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) throws ManagementApiException {
        ApisDelegate apisDelegate = new ApisDelegate();
        return apisDelegate.updateArtifact(groupId, request);
    }

    @PATCH
    @Path("/{group-id}/endpoints")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update endpoint", description = "", tags = {"endpoints"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endpoint update status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Ack updateEndpoint(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) throws ManagementApiException {
        EndpointsDelegate endpointsDelegate = new EndpointsDelegate();
        return endpointsDelegate.updateArtifact(groupId, request);
    }

    @PATCH
    @Path("/{group-id}/inbound-endpoints")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update inbound endpoint", description = "", tags = {"inboundEndpoints"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inbound endpoint update status",
                    content = @Content(schema = @Schema(implementation = Ack.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Ack updateInboundEp(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) throws ManagementApiException {
        InboundEndpointDelegate inboundEndpointDelegate = new InboundEndpointDelegate();
        return inboundEndpointDelegate.updateArtifact(groupId, request);
    }

    @PATCH
    @Path("/{group-id}/message-processors")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update message processor", description = "", tags = {"messageProcessors"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message processor update status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Ack updateMessageProcessor(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) throws ManagementApiException {

        MessageProcessorsDelegate messageProcessorsDelegate = new MessageProcessorsDelegate();
        return messageProcessorsDelegate.updateArtifact(groupId, request);
    }

    @PATCH
    @Path("/{group-id}/proxy-services")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update proxy service", description = "", tags = {"proxyServices"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proxy update status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Ack updateProxyService(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) throws ManagementApiException {
        ProxyServiceDelegate proxyServiceDelegate = new ProxyServiceDelegate();
        return proxyServiceDelegate.updateArtifact(groupId, request);
    }

    @PATCH
    @Path("/{group-id}/sequences")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update sequence", description = "", tags = {"sequences"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sequence update status",
                    content = @Content(schema = @Schema(implementation = SuccessStatus.class))),
            @ApiResponse(responseCode = "200", description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public Ack updateSequence(
            @PathParam("group-id") @Parameter(description = "Group ID of the node") String groupId,
            @Valid ArtifactUpdateRequest request) throws ManagementApiException {

        SequencesDelegate sequencesDelegate = new SequencesDelegate();
        return sequencesDelegate.updateArtifact(groupId, request);
    }
}

