package org.wso2.ei.dashboard.core.rest.model;

import javax.validation.Valid;

public class ManageServerRuntimeRequest {
    private @Valid String status;

    public ManageServerRuntimeRequest() {
        status = null;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
