package org.wso2.ei.dashboard.core.rest.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ArtifactsInner   {
  private @Valid String name = null;
  private @Valid String templateType = null;
  private @Valid List<ArtifactDetails> nodes = new ArrayList<ArtifactDetails>();

  /**
   **/
  public ArtifactsInner name(String name) {
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

  //for templates artifact, there are 2 types (sequence and endpoints)
  public void setType(String type) {
    this.templateType = type;
  }

  public String getType() {
    return this.templateType;
  }

  /**
   **/
  public ArtifactsInner nodes(List<ArtifactDetails> nodes) {
    this.nodes = nodes;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("nodes")

  public List<ArtifactDetails> getNodes() {
    return nodes;
  }
  public void setNodes(List<ArtifactDetails> nodes) {
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
    ArtifactsInner artifactsInner = (ArtifactsInner) o;
    return Objects.equals(name, artifactsInner.name) &&
        Objects.equals(nodes, artifactsInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArtifactsInner {\n");
    
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
