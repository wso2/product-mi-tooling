package org.wso2.ei.dashboard.core.rest.model;

public class UsersResourceResponse {
    private Users resourceList;
    private int count;

    public void setResourceList(Users resourceList) {
        this.resourceList = resourceList;
    }

    public Users getResourceList() {
        return resourceList;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

