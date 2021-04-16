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
package org.wso2.ei.dashboard.core.exception.mappers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper to create response for Dashboard server exceptions depending on the error code.
 */
@Provider
public class DashboardServerExceptionMapper implements ExceptionMapper<ManagementApiException>  {

    private static final Logger logger = LogManager.getLogger(DashboardServerExceptionMapper.class);

    @Override
    public Response toResponse(ManagementApiException error) {

        logger.debug("Error: ", error);
        String errorMessage = error.getMessage();
        switch (error.getErrorCode()) {
            case 400: {
                return populateResponse(getErrorMessage(errorMessage, "Bad request"),
                                        Response.Status.BAD_REQUEST);
            }
            case 401: {
                return populateResponse(getErrorMessage(errorMessage, "Authorization failure"),
                                        Response.Status.UNAUTHORIZED);
            }
            case 404: {
                return populateResponse(getErrorMessage(errorMessage, "Resource not found"),
                                        Response.Status.NOT_FOUND);
            }
            default: {
                return populateResponse(getErrorMessage(
                        errorMessage, "Error processing request. Please check server logs"),
                                        Response.Status.INTERNAL_SERVER_ERROR);
            }

        }
    }

    private Response populateResponse(String message, Response.Status httpStatusCode) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", message);
        return Response.status(httpStatusCode).entity(responseBody).header("content-type", "application/json").build();
    }

    private String getErrorMessage(String errorMessage, String defaultMessage) {
        if (null == errorMessage || errorMessage.isEmpty()) {
            return defaultMessage;
        }
        return errorMessage;
    }
}
