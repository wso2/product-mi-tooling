package org.wso2.ei.dashboard.core.rest.model;


public class LogsResourceResponse {
    private LogList resourceList;
    private int count;

    public void setResourceList(LogList resourceList) {
        this.resourceList = resourceList;
    }

    public LogList getResourceList() {
        return resourceList;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

