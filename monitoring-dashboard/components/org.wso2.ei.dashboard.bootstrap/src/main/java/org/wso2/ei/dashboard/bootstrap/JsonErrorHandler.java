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
package org.wso2.ei.dashboard.bootstrap;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is responsible for handling Jetty errors and giving appropriate JSON responses.
 */
public class JsonErrorHandler extends ErrorHandler {

    @Override
    public void doError(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PrintWriter out = response.getWriter();
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        out.print(getJsonBody(response.getStatus()));
        out.flush();

    }

    private String getJsonBody(int status) {

        return String.format("{\n" +
                "\t\"message\": \"%s\"\n" +
                "}", getResponseMessage(status));

    }

    private String getResponseMessage(int status) {

        switch (status) {
            case 404:
                return "Resource not found";
            case 401:
                return "Not authorized";
            default:
                return "Internal server error";
        }
    }

}
