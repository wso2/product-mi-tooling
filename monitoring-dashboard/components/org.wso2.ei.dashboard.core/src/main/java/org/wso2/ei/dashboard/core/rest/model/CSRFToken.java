package org.wso2.ei.dashboard.core.rest.model;

public class CSRFToken {
    private final String csrfToken;

    public CSRFToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public String getCsrfToken() {
        return csrfToken;
    }
}
