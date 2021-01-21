/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
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

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class NodeListInner   {
  private @Valid String id = null;
  private @Valid String details = null;

  /**
   * node id.
   **/
  public NodeListInner id(String id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(value = "node id.")
  @JsonProperty("id")

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * String contains role, upTime, serverName, serverVersion, miHome, javaHome, javaVersion, javaVendor, osName
   **/
  public NodeListInner details(String details) {
    this.details = details;
    return this;
  }

  
  @ApiModelProperty(value = "String contains role, upTime, serverName, serverVersion, miHome, javaHome, javaVersion, javaVendor, osName")
  @JsonProperty("details")

  public String getDetails() {
    return details;
  }
  public void setDetails(String details) {
    this.details = details;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NodeListInner nodeListInner = (NodeListInner) o;
    return Objects.equals(id, nodeListInner.id) &&
        Objects.equals(details, nodeListInner.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NodeListInner {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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
