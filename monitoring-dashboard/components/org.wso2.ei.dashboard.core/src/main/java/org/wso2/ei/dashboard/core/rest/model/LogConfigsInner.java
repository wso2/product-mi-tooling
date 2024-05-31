/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class LogConfigsInner   {
  private @Valid String name = null;
  private @Valid String componentName = null;
  private @Valid List<LogConfigDetail> nodes = new ArrayList<LogConfigDetail>();

  /**
   **/
  public LogConfigsInner name(String name) {
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
  public LogConfigsInner componentName(String componentName) {
    this.componentName = componentName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("componentName")

  public String getComponentName() {
    return componentName;
  }
  public String getComponentNameIgnoreCase() {
    return componentName.toLowerCase();
  }
  public void setComponentName(String componentName) {
    this.componentName = componentName;
  }

  /**
   **/
  public LogConfigsInner nodes(List<LogConfigDetail> nodes) {
    this.nodes = nodes;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("nodes")

  public List<LogConfigDetail> getNodes() {
    return nodes;
  }
  public void setNodes(List<LogConfigDetail> nodes) {
    this.nodes = new ArrayList<>(nodes);
  }

  public void addNode(LogConfigDetail node) {
    if (this.nodes == null) {
      this.nodes = new ArrayList<>(); // Initialize if null
    }
    this.nodes.add(node);
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogConfigsInner logConfigsInner = (LogConfigsInner) o;
    return Objects.equals(name, logConfigsInner.name) &&
        Objects.equals(componentName, logConfigsInner.componentName) &&
            Objects.equals(nodes, logConfigsInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, componentName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogConfigsInner {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    componentName: ").append(toIndentedString(componentName)).append("\n");
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
