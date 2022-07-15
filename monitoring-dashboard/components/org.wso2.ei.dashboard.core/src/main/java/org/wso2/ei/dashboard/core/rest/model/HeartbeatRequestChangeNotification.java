package org.wso2.ei.dashboard.core.rest.model;

import java.util.ArrayList;
import java.util.List;
import org.wso2.ei.dashboard.core.rest.model.UpdatedArtifact;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class HeartbeatRequestChangeNotification   {
  private @Valid List<UpdatedArtifact> deployedArtifacts = new ArrayList<UpdatedArtifact>();
  private @Valid List<UpdatedArtifact> undeployedArtifacts = new ArrayList<UpdatedArtifact>();

  /**
   **/
  public HeartbeatRequestChangeNotification deployedArtifacts(List<UpdatedArtifact> deployedArtifacts) {
    this.deployedArtifacts = deployedArtifacts;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("deployedArtifacts")

  public List<UpdatedArtifact> getDeployedArtifacts() {
    return deployedArtifacts;
  }
  public void setDeployedArtifacts(List<UpdatedArtifact> deployedArtifacts) {
    this.deployedArtifacts = deployedArtifacts;
  }

  /**
   **/
  public HeartbeatRequestChangeNotification undeployedArtifacts(List<UpdatedArtifact> undeployedArtifacts) {
    this.undeployedArtifacts = undeployedArtifacts;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("undeployedArtifacts")

  public List<UpdatedArtifact> getUndeployedArtifacts() {
    return undeployedArtifacts;
  }
  public void setUndeployedArtifacts(List<UpdatedArtifact> undeployedArtifacts) {
    this.undeployedArtifacts = undeployedArtifacts;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HeartbeatRequestChangeNotification heartbeatRequestChangeNotification = (HeartbeatRequestChangeNotification) o;
    return Objects.equals(deployedArtifacts, heartbeatRequestChangeNotification.deployedArtifacts) &&
        Objects.equals(undeployedArtifacts, heartbeatRequestChangeNotification.undeployedArtifacts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deployedArtifacts, undeployedArtifacts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HeartbeatRequestChangeNotification {\n");
    
    sb.append("    deployedArtifacts: ").append(toIndentedString(deployedArtifacts)).append("\n");
    sb.append("    undeployedArtifacts: ").append(toIndentedString(undeployedArtifacts)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
