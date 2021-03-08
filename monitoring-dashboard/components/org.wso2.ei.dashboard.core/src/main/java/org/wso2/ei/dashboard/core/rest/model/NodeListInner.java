package org.wso2.ei.dashboard.core.rest.model;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class NodeListInner   {
  private @Valid String nodeId = null;
  private @Valid String status = null;
  private @Valid String details = null;

  /**
   * node id.
   **/
  public NodeListInner nodeId(String nodeId) {
    this.nodeId = nodeId;
    return this;
  }

  
  @ApiModelProperty(value = "node id.")
  @JsonProperty("nodeId")

  public String getNodeId() {
    return nodeId;
  }
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  /**
   **/
  public NodeListInner status(String status) {
    this.status = status;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("status")

  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * String contains role, upTime, serverName, serverVersion, miHome, javaHome, javaVersion, javaVendor, osName
   **/
  public NodeListInner details(String details) {
    this.details = details;
    return this;
  }

  
  @ApiModelProperty(value = "String contains role, upTime, serverName, serverVersion, miHome, javaHome, javaVersion, javaVendor, osName")
  @JsonProperty("details")

  public String getDetails() {
    return details;
  }
  public void setDetails(String details) {
    this.details = details;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NodeListInner nodeListInner = (NodeListInner) o;
    return Objects.equals(nodeId, nodeListInner.nodeId) &&
        Objects.equals(status, nodeListInner.status) &&
        Objects.equals(details, nodeListInner.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodeId, status, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NodeListInner {\n");
    
    sb.append("    nodeId: ").append(toIndentedString(nodeId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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
