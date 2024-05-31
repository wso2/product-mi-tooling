/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ei.dashboard.core.rest.model;

import javax.validation.Valid;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class LogConfigDetail {
  private @Valid String nodeId = null;
  private @Valid String level = null;

  public LogConfigDetail(String nodeId, String level) {
    this.nodeId = nodeId;
    this.level = level;
  }

  public LogConfigDetail nodeId(String nodeId) {
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

  public LogConfigDetail level(String level) {
    this.level = level;
    return this;
  }
  
  @ApiModelProperty(value = "")
  @JsonProperty("level")

  public String getLevel() {
    return level;
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
    LogConfigDetail logConfigDetail = (LogConfigDetail) o;
    return Objects.equals(nodeId, logConfigDetail.nodeId) &&
        Objects.equals(level, logConfigDetail.level);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodeId, level);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogConfigDetail {\n");
    
    sb.append("    nodeId: ").append(toIndentedString(nodeId)).append("\n");
    sb.append("    level: ").append(toIndentedString(level)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
