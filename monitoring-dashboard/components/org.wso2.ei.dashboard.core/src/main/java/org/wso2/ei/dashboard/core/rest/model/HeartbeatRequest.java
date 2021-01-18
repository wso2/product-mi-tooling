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


public class HeartbeatRequest   {
  private @Valid String product = null;
  private @Valid String groupId = null;
  private @Valid String nodeId = null;
  private @Valid Integer interval = null;
  private @Valid String mgtApiUrl = null;

  /**
   **/
  public HeartbeatRequest product(String product) {
    this.product = product;
    return this;
  }


  @ApiModelProperty(value = "")
  @JsonProperty("product")

  public String getProduct() {
    return product;
  }
  public void setProduct(String product) {
    this.product = product;
  }

  /**
   **/
  public HeartbeatRequest groupId(String groupId) {
    this.groupId = groupId;
    return this;
  }


  @ApiModelProperty(value = "")
  @JsonProperty("groupId")

  public String getGroupId() {
    return groupId;
  }
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  /**
   **/
  public HeartbeatRequest nodeId(String nodeId) {
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
  public HeartbeatRequest interval(Integer interval) {
    this.interval = interval;
    return this;
  }


  @ApiModelProperty(value = "")
  @JsonProperty("interval")

  public Integer getInterval() {
    return interval;
  }
  public void setInterval(Integer interval) {
    this.interval = interval;
  }

  /**
   **/
  public HeartbeatRequest mgtApiUrl(String mgtApiUrl) {
    this.mgtApiUrl = mgtApiUrl;
    return this;
  }


  @ApiModelProperty(value = "")
  @JsonProperty("mgtApiUrl")

  public String getMgtApiUrl() {
    return mgtApiUrl;
  }
  public void setMgtApiUrl(String mgtApiUrl) {
    this.mgtApiUrl = mgtApiUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HeartbeatRequest heartbeatRequest = (HeartbeatRequest) o;
    return Objects.equals(product, heartbeatRequest.product) &&
           Objects.equals(groupId, heartbeatRequest.groupId) &&
           Objects.equals(nodeId, heartbeatRequest.nodeId) &&
           Objects.equals(interval, heartbeatRequest.interval) &&
           Objects.equals(mgtApiUrl, heartbeatRequest.mgtApiUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(product, groupId, nodeId, interval, mgtApiUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HeartbeatRequest {\n");

    sb.append("    product: ").append(toIndentedString(product)).append("\n");
    sb.append("    groupId: ").append(toIndentedString(groupId)).append("\n");
    sb.append("    nodeId: ").append(toIndentedString(nodeId)).append("\n");
    sb.append("    interval: ").append(toIndentedString(interval)).append("\n");
    sb.append("    mgtApiUrl: ").append(toIndentedString(mgtApiUrl)).append("\n");
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
