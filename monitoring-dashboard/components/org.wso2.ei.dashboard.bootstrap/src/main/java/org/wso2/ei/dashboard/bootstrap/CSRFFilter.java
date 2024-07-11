package org.wso2.ei.dashboard.bootstrap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class CSRFFilter implements Filter {
    private static final String CSRF_TOKEN = "CSRF_TOKEN";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        HttpSession session = httpRequest.getSession();

        if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
            String csrfToken = new BigInteger(130, new SecureRandom()).toString(32);
            session.setAttribute(CSRF_TOKEN, csrfToken);
            httpResponse.setHeader("X-CSRF-Token", csrfToken);
        } else {
            String requestCsrfToken = httpRequest.getParameter(CSRF_TOKEN);
            String sessionCsrfToken = (String) session.getAttribute(CSRF_TOKEN);

            if (sessionCsrfToken == null || !sessionCsrfToken.equals(requestCsrfToken)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
                return;
            }
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {}
}
