package org.wso2.ei.dashboard.core.exception.mappers;

import org.wso2.ei.dashboard.core.exception.DashboardServerException;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityNotFoundMapper implements ExceptionMapper<DashboardServerException> {

    @Override
    public Response toResponse(DashboardServerException e) {

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Internal server error");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(responseBody)
                .header("content" +
                        "-type", "application/json").build();
    }
}
