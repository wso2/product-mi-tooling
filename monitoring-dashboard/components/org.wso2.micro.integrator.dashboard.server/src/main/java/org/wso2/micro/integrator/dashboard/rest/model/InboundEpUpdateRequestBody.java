package org.wso2.micro.integrator.dashboard.rest.model;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class InboundEpUpdateRequestBody   {
  private @Valid String inboundEpName = null;
  private @Valid String nodeId = null;
  private @Valid Boolean tracing = null;

  /**
   **/
  public InboundEpUpdateRequestBody inboundEpName(String inboundEpName) {
    this.inboundEpName = inboundEpName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("inboundEpName")

  public String getInboundEpName() {
    return inboundEpName;
  }
  public void setInboundEpName(String inboundEpName) {
    this.inboundEpName = inboundEpName;
  }

  /**
   **/
  public InboundEpUpdateRequestBody nodeId(String nodeId) {
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
  public InboundEpUpdateRequestBody tracing(Boolean tracing) {
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
    InboundEpUpdateRequestBody inboundEpUpdateRequestBody = (InboundEpUpdateRequestBody) o;
    return Objects.equals(inboundEpName, inboundEpUpdateRequestBody.inboundEpName) &&
        Objects.equals(nodeId, inboundEpUpdateRequestBody.nodeId) &&
        Objects.equals(tracing, inboundEpUpdateRequestBody.tracing);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inboundEpName, nodeId, tracing);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InboundEpUpdateRequestBody {\n");
    
    sb.append("    inboundEpName: ").append(toIndentedString(inboundEpName)).append("\n");
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
