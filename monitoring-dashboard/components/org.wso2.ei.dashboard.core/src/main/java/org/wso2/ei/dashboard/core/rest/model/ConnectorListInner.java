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


public class ConnectorListInner   {
  private @Valid String connectorName = null;
  private @Valid String _package = null;
  private @Valid String description = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public ConnectorListInner connectorName(String connectorName) {
    this.connectorName = connectorName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("connectorName")

  public String getConnectorName() {
    return connectorName;
  }
  public void setConnectorName(String connectorName) {
    this.connectorName = connectorName;
  }

  /**
   **/
  public ConnectorListInner _package(String _package) {
    this._package = _package;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("package")

  public String getPackage() {
    return _package;
  }
  public void setPackage(String _package) {
    this._package = _package;
  }

  /**
   **/
  public ConnectorListInner description(String description) {
    this.description = description;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("description")

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   **/
  public ConnectorListInner nodes(List<Object> nodes) {
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
    ConnectorListInner connectorListInner = (ConnectorListInner) o;
    return Objects.equals(connectorName, connectorListInner.connectorName) &&
        Objects.equals(_package, connectorListInner._package) &&
        Objects.equals(description, connectorListInner.description) &&
        Objects.equals(nodes, connectorListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(connectorName, _package, description, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnectorListInner {\n");
    
    sb.append("    connectorName: ").append(toIndentedString(connectorName)).append("\n");
    sb.append("    _package: ").append(toIndentedString(_package)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
