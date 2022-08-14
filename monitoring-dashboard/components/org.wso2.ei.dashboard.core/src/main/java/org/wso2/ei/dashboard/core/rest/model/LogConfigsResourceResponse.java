package org.wso2.ei.dashboard.core.rest.model;

public class LogConfigsResourceResponse {
    private LogConfigs resourceList;
    private int count;

    public void setResourceList(LogConfigs resourceList) {
        this.resourceList = resourceList;
    }

    public LogConfigs getResourceList() {
        return resourceList;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

