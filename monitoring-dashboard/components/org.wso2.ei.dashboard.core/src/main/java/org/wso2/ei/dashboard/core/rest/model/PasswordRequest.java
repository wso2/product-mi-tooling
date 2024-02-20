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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Objects;

public class PasswordRequest {

    private @Valid String newPassword = null;
    private @Valid String confirmPassword = null;
    private @Valid String oldPassword = null;
    private @Valid String userId = null;

    /**
     **/
    public PasswordRequest newPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    @ApiModelProperty(value = "")
    @JsonProperty("newPassword")

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     **/
    public PasswordRequest confirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }

    @ApiModelProperty(value = "")
    @JsonProperty("confirmPassword")

    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     **/
    public PasswordRequest oldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        return this;
    }

    @ApiModelProperty(value = "")
    @JsonProperty("oldPassword")

    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     **/
    public PasswordRequest userId(String userId) {
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

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PasswordRequest passwordRequest = (PasswordRequest) o;
        return Objects.equals(newPassword, passwordRequest.newPassword) &&
                Objects.equals(confirmPassword, passwordRequest.confirmPassword) &&
                Objects.equals(oldPassword, passwordRequest.oldPassword) &&
                Objects.equals(userId, passwordRequest.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newPassword, confirmPassword, oldPassword, userId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PasswordRequest {\n");

        sb.append("    newPassword: ").append(toIndentedString(newPassword)).append("\n");
        sb.append("    confirmPassword: ").append(toIndentedString(confirmPassword)).append("\n");
        sb.append("    oldPassword: ").append(toIndentedString(oldPassword)).append("\n");
        sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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
