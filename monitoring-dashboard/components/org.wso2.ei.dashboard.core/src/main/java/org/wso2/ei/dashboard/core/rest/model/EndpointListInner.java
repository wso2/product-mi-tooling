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

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class EndpointListInner   {
  private @Valid String endpointName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public EndpointListInner endpointName(String endpointName) {
    this.endpointName = endpointName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("endpointName")

  public String getEndpointName() {
    return endpointName;
  }
  public void setEndpointName(String endpointName) {
    this.endpointName = endpointName;
  }

  /**
   **/
  public EndpointListInner nodes(List<Object> nodes) {
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
    EndpointListInner endpointListInner = (EndpointListInner) o;
    return Objects.equals(endpointName, endpointListInner.endpointName) &&
        Objects.equals(nodes, endpointListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(endpointName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EndpointListInner {\n");
    
    sb.append("    endpointName: ").append(toIndentedString(endpointName)).append("\n");
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
