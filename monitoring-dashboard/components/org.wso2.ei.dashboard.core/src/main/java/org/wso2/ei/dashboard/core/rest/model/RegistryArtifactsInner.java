/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.wso2.ei.dashboard.core.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

public class RegistryArtifactsInner   {
  private @Valid String childName = null;
  private @Valid String mediaType = null;
  private @Valid String childPath = null;
  private @Valid List<RegistryProperty> properties = new ArrayList<RegistryProperty>();

  /**
   **/
  public RegistryArtifactsInner childName(String childName) {
    this.childName = childName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("childName")

  public String getChildName() {
    return childName;
  }
  public void setChildName(String childName) {
    this.childName = childName;
  }

  public String getChildNameIgnoreCase() {
    return childName.toLowerCase();
  }
  
  /** 
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("childPath")

  public String getChildPath() {
    return childPath;
  }
  public void setChildPath(String childPath) {
    this.childPath = childPath;
  }


  /**
   **/
  public RegistryArtifactsInner mediaType(String mediaType) {
    this.mediaType = mediaType;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("mediaType")

  public String getMediaType() {
    return mediaType;
  }
  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  /**
   **/
  public RegistryArtifactsInner properties(List<RegistryProperty> properties) {
    this.properties = properties;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("properties")

  public List<RegistryProperty> getProperties() {
    return properties;
  }
  public void setProperties(List<RegistryProperty> properties) {
    this.properties = properties;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegistryArtifactsInner registryArtifactsInner = (RegistryArtifactsInner) o;
    return Objects.equals(childName, registryArtifactsInner.childName) &&
        Objects.equals(mediaType, registryArtifactsInner.mediaType) &&
        Objects.equals(properties, registryArtifactsInner.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(childName, mediaType, properties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistryArtifactsInner {\n");
    
    sb.append("    childName: ").append(toIndentedString(childName)).append("\n");
    sb.append("    mediaType: ").append(toIndentedString(mediaType)).append("\n");
    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
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
