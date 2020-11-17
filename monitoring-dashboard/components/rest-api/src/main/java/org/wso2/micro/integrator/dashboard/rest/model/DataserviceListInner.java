package org.wso2.micro.integrator.dashboard.rest.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class DataserviceListInner   {
  private @Valid String dataserviceName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public DataserviceListInner dataserviceName(String dataserviceName) {
    this.dataserviceName = dataserviceName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("dataserviceName")

  public String getDataserviceName() {
    return dataserviceName;
  }
  public void setDataserviceName(String dataserviceName) {
    this.dataserviceName = dataserviceName;
  }

  /**
   **/
  public DataserviceListInner nodes(List<Object> nodes) {
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
    DataserviceListInner dataserviceListInner = (DataserviceListInner) o;
    return Objects.equals(dataserviceName, dataserviceListInner.dataserviceName) &&
        Objects.equals(nodes, dataserviceListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataserviceName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataserviceListInner {\n");
    
    sb.append("    dataserviceName: ").append(toIndentedString(dataserviceName)).append("\n");
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
