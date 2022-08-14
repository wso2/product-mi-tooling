package org.wso2.ei.dashboard.core.rest.model;

public class RolesResourceResponse {
    private RoleList resourceList;
    private int count;

    public void setResourceList(RoleList resourceList) {
        this.resourceList = resourceList;
    }

    public RoleList getResourceList() {
        return resourceList;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

