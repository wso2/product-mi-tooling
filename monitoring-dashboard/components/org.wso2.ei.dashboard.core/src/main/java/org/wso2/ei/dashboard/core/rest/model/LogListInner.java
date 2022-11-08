package org.wso2.ei.dashboard.core.rest.model;

import java.util.ArrayList;
import java.util.List;
import org.wso2.ei.dashboard.core.rest.model.LogDetail;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class LogListInner   {
  private @Valid String name = null;
  private @Valid List<LogDetail> nodes = new ArrayList<LogDetail>();

  /**
   **/
  public LogListInner name(String name) {
    this.name = name;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("name")

  public String getName() {
    return name;
  }

  public String getNameIgnoreCase() {
    return name.toLowerCase();
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   **/
  public LogListInner nodes(List<LogDetail> nodes) {
    this.nodes = nodes;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("nodes")

  public List<LogDetail> getNodes() {
    return nodes;
  }
  public void setNodes(List<LogDetail> nodes) {
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
    LogListInner logListInner = (LogListInner) o;
    return Objects.equals(name, logListInner.name) &&
        Objects.equals(nodes, logListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogListInner {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
