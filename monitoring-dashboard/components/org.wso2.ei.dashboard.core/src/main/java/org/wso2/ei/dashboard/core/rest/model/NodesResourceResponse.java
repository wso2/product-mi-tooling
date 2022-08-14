package org.wso2.ei.dashboard.core.rest.model;

public class NodesResourceResponse {
    private NodeList resourceList;
    private int count;

    public void setResourceList(NodeList resourceList) {
        this.resourceList = resourceList;
    }

    public NodeList getResourceList() {
        return resourceList;
    }
    
    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
    
}
