package org.wso2.ei.dashboard.core.rest.model;

public class ArtifactsResourceResponse {
    private Artifacts resourceList;
    private int count;

    public void setResourceList(Artifacts resourceList) {
        this.resourceList = resourceList;
    }

    public Artifacts getResourceList() {
        return resourceList;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

}

