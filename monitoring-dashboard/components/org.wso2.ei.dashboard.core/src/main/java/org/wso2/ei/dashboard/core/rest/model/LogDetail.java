package org.wso2.ei.dashboard.core.rest.model;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class LogDetail   {
  private @Valid String nodeId = null;
  private @Valid String logSize = null;

  /**
   **/
  public LogDetail nodeId(String nodeId) {
    this.nodeId = nodeId;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("nodeId")

  public String getNodeId() {
    return nodeId;
  }
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  /**
   **/
  public LogDetail logSize(String logSize) {
    this.logSize = logSize;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("logSize")

  public String getLogSize() {
    return logSize;
  }
  public void setLogSize(String logSize) {
    this.logSize = logSize;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogDetail logDetail = (LogDetail) o;
    return Objects.equals(nodeId, logDetail.nodeId) &&
        Objects.equals(logSize, logDetail.logSize);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodeId, logSize);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogDetail {\n");
    
    sb.append("    nodeId: ").append(toIndentedString(nodeId)).append("\n");
    sb.append("    logSize: ").append(toIndentedString(logSize)).append("\n");
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
