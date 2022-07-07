package org.wso2.ei.dashboard.core.rest.model;

import java.util.ArrayList;
import java.util.List;

public class CAppArtifactCollection {

    private List<String> faultyArtifacts;

    public CAppArtifactCollection() {
        faultyArtifacts = new ArrayList<>();
    }

    public List<String> getFaultyArtifacts() {
        return faultyArtifacts;
    }

    public void setFaultyArtifacts(List<String> faultyArtifacts) {
        this.faultyArtifacts = faultyArtifacts;
    }
}
