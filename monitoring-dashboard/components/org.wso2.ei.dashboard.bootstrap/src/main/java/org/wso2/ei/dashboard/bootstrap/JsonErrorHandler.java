package org.wso2.ei.dashboard.bootstrap;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
