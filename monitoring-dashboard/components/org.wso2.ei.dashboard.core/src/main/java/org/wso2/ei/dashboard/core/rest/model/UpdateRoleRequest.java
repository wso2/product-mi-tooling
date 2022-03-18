/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateRoleRequest   {
  private @Valid String userId = null;
  private @Valid List<String> removedRoles = new ArrayList<String>();
  private @Valid List<String> addedRoles = new ArrayList<String>();

  /**
   **/
  public UpdateRoleRequest userId(String userId) {
    this.userId = userId;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("userId")

  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   **/
  public UpdateRoleRequest removedRoles(List<String> removedRoles) {
    this.removedRoles = removedRoles;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("removedRoles")

  public List<String> getRemovedRoles() {
    return removedRoles;
  }
  public void setRemovedRoles(List<String> removedRoles) {
    this.removedRoles = removedRoles;
  }

  /**
   **/
  public UpdateRoleRequest addedRoles(List<String> addedRoles) {
    this.addedRoles = addedRoles;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("addedRoles")

  public List<String> getAddedRoles() {
    return addedRoles;
  }
  public void setAddedRoles(List<String> addedRoles) {
    this.addedRoles = addedRoles;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateRoleRequest updateRoleRequest = (UpdateRoleRequest) o;
    return Objects.equals(userId, updateRoleRequest.userId) &&
        Objects.equals(removedRoles, updateRoleRequest.removedRoles) &&
        Objects.equals(addedRoles, updateRoleRequest.addedRoles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, removedRoles, addedRoles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRoleRequest {\n");
    
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    removedRoles: ").append(toIndentedString(removedRoles)).append("\n");
    sb.append("    addedRoles: ").append(toIndentedString(addedRoles)).append("\n");
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
