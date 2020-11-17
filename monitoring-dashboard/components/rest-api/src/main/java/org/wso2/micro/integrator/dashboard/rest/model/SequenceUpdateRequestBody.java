package org.wso2.micro.integrator.dashboard.rest.model;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class SequenceUpdateRequestBody   {
  private @Valid String sequenceName = null;
  private @Valid String nodeId = null;
  private @Valid Boolean tracing = null;

  /**
   **/
  public SequenceUpdateRequestBody sequenceName(String sequenceName) {
    this.sequenceName = sequenceName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("sequenceName")

  public String getSequenceName() {
    return sequenceName;
  }
  public void setSequenceName(String sequenceName) {
    this.sequenceName = sequenceName;
  }

  /**
   **/
  public SequenceUpdateRequestBody nodeId(String nodeId) {
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
  public SequenceUpdateRequestBody tracing(Boolean tracing) {
    this.tracing = tracing;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("tracing")

  public Boolean isTracing() {
    return tracing;
  }
  public void setTracing(Boolean tracing) {
    this.tracing = tracing;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SequenceUpdateRequestBody sequenceUpdateRequestBody = (SequenceUpdateRequestBody) o;
    return Objects.equals(sequenceName, sequenceUpdateRequestBody.sequenceName) &&
        Objects.equals(nodeId, sequenceUpdateRequestBody.nodeId) &&
        Objects.equals(tracing, sequenceUpdateRequestBody.tracing);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sequenceName, nodeId, tracing);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SequenceUpdateRequestBody {\n");
    
    sb.append("    sequenceName: ").append(toIndentedString(sequenceName)).append("\n");
    sb.append("    nodeId: ").append(toIndentedString(nodeId)).append("\n");
    sb.append("    tracing: ").append(toIndentedString(tracing)).append("\n");
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
