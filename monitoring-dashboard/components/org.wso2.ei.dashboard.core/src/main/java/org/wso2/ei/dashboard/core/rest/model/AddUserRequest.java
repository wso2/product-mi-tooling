package org.wso2.ei.dashboard.core.rest.model;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddUserRequest   {
  private @Valid String userId = null;
  private @Valid String domain = null;
  private @Valid String password = null;
  private @Valid Boolean isAdmin = null;

  /**
   **/
  public AddUserRequest userId(String userId) {
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
  public AddUserRequest domain(String domain) {
    this.domain = domain;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("domain")

  public String getDomain() {
    return domain;
  }
  public void setDomain(String domain) {
    this.domain = domain;
  }

  /**
   **/
  public AddUserRequest password(String password) {
    this.password = password;
    return this;
  }


  @ApiModelProperty(value = "")
  @JsonProperty("password")

  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   **/
  public AddUserRequest isAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
    return this;
  }


  @ApiModelProperty(value = "")
  @JsonProperty("isAdmin")

  public Boolean isIsAdmin() {
    return isAdmin;
  }
  public void setIsAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddUserRequest addUserRequest = (AddUserRequest) o;
    return Objects.equals(userId, addUserRequest.userId) &&
           Objects.equals(domain, addUserRequest.domain) &&
           Objects.equals(password, addUserRequest.password) &&
           Objects.equals(isAdmin, addUserRequest.isAdmin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, domain, password, isAdmin);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddUserRequest {\n");

    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    isAdmin: ").append(toIndentedString(isAdmin)).append("\n");
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
