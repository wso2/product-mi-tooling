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

public class CAppArtifactCollection {
  private @Valid List<String> faultyArtifacts = new ArrayList<String>();

  /**
   **/
  public CAppArtifactCollection faultyArtifacts(List<String> faultyArtifacts) {
    this.faultyArtifacts = faultyArtifacts;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("faultyArtifacts")

  public List<String> getFaultyArtifacts() {
    return faultyArtifacts;
  }

  public void setFaultyArtifacts(List<String> faultyArtifacts) {
    this.faultyArtifacts = faultyArtifacts;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CAppArtifactCollection cappArtifactCollection = (CAppArtifactCollection) o;
    return Objects.equals(faultyArtifacts, cappArtifactCollection.faultyArtifacts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(faultyArtifacts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CAppArtifactCollection {\n");

    sb.append("    faultyArtifacts: ").append(toIndentedString(faultyArtifacts)).append("\n");
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
