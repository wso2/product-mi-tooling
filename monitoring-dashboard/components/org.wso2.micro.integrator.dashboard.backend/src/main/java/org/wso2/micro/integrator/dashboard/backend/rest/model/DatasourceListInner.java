package org.wso2.micro.integrator.dashboard.backend.rest.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class DatasourceListInner   {
  private @Valid String datasourceName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public DatasourceListInner datasourceName(String datasourceName) {
    this.datasourceName = datasourceName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("datasourceName")

  public String getDatasourceName() {
    return datasourceName;
  }
  public void setDatasourceName(String datasourceName) {
    this.datasourceName = datasourceName;
  }

  /**
   **/
  public DatasourceListInner nodes(List<Object> nodes) {
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
    DatasourceListInner datasourceListInner = (DatasourceListInner) o;
    return Objects.equals(datasourceName, datasourceListInner.datasourceName) &&
        Objects.equals(nodes, datasourceListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasourceName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DatasourceListInner {\n");
    
    sb.append("    datasourceName: ").append(toIndentedString(datasourceName)).append("\n");
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
