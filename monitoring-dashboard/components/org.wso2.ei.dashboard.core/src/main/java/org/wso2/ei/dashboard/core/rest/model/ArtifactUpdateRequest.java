package org.wso2.ei.dashboard.core.rest.model;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ArtifactUpdateRequest   {
  private @Valid String artifactName = null;
  private @Valid String nodeId = null;
  private @Valid String type = null;
  private @Valid Boolean value = null;

  /**
   **/
  public ArtifactUpdateRequest artifactName(String artifactName) {
    this.artifactName = artifactName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("artifactName")

  public String getArtifactName() {
    return artifactName;
  }
  public void setArtifactName(String artifactName) {
    this.artifactName = artifactName;
  }

  /**
   **/
  public ArtifactUpdateRequest nodeId(String nodeId) {
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
  public ArtifactUpdateRequest type(String type) {
    this.type = type;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("type")

  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  /**
   **/
  public ArtifactUpdateRequest value(Boolean value) {
    this.value = value;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("value")

  public Boolean isValue() {
    return value;
  }
  public void setValue(Boolean value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArtifactUpdateRequest artifactUpdateRequest = (ArtifactUpdateRequest) o;
    return Objects.equals(artifactName, artifactUpdateRequest.artifactName) &&
        Objects.equals(nodeId, artifactUpdateRequest.nodeId) &&
        Objects.equals(type, artifactUpdateRequest.type) &&
        Objects.equals(value, artifactUpdateRequest.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(artifactName, nodeId, type, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArtifactUpdateRequest {\n");
    
    sb.append("    artifactName: ").append(toIndentedString(artifactName)).append("\n");
    sb.append("    nodeId: ").append(toIndentedString(nodeId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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
