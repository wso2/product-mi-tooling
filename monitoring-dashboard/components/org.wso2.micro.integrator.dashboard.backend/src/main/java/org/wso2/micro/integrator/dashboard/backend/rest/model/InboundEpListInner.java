package org.wso2.micro.integrator.dashboard.backend.rest.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class InboundEpListInner   {
  private @Valid String inboundEpName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public InboundEpListInner inboundEpName(String inboundEpName) {
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
  public InboundEpListInner nodes(List<Object> nodes) {
    this.nodes = nodes;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("nodes")

  public List<Object> getNodes() {
    return nodes;
  }
  public void setNodes(List<Object> nodes) {
    this.nodes = nodes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InboundEpListInner inboundEpListInner = (InboundEpListInner) o;
    return Objects.equals(inboundEpName, inboundEpListInner.inboundEpName) &&
        Objects.equals(nodes, inboundEpListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inboundEpName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InboundEpListInner {\n");
    
    sb.append("    inboundEpName: ").append(toIndentedString(inboundEpName)).append("\n");
    sb.append("    nodes: ").append(toIndentedString(nodes)).append("\n");
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
