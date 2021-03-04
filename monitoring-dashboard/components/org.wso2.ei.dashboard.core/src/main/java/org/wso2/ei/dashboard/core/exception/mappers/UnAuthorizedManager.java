package org.wso2.ei.dashboard.core.exception.mappers;

import org.wso2.ei.dashboard.core.exception.UnAuthorizedException;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnAuthorizedManager implements ExceptionMapper<UnAuthorizedException> {

    @Override
    public Response toResponse(UnAuthorizedException e) {

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Unauthorized");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(responseBody)
                .header("content" +
                        "-type", "application/json").build();
    }
}
