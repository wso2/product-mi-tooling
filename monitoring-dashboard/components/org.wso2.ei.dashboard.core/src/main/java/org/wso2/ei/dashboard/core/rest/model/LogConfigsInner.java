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
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class LogConfigsInner   {
  private @Valid String name = null;
  private @Valid String componentName = null;
  private @Valid String level = null;

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
  public LogConfigsInner level(String level) {
    this.level = level;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("level")

  public String getLevel() {
    return level;
  }
  public String getLevelIgnoreCase() {
    return level.toLowerCase();
  }
  public void setLevel(String level) {
    this.level = level;
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
        Objects.equals(level, logConfigsInner.level);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, componentName, level);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogConfigsInner {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    componentName: ").append(toIndentedString(componentName)).append("\n");
    sb.append("    level: ").append(toIndentedString(level)).append("\n");
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
