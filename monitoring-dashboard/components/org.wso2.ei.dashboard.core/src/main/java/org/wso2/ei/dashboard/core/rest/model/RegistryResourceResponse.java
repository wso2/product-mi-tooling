package org.wso2.ei.dashboard.core.rest.model;

public class RegistryResourceResponse {
    private RegistryArtifacts resourceList;
    private int count;

    public void setResourceList(RegistryArtifacts resourceList) {
        this.resourceList = resourceList;
    }

    public RegistryArtifacts getResourceList() {
        return resourceList;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

