package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

public class UserInner   {
  private @Valid String userId = null;
  private @Valid Boolean isAdmin = null;
  private @Valid List<String> roles = new ArrayList<String>();

  /**
   **/
  public UserInner userId(String userId) {
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
  public UserInner isAdmin(Boolean isAdmin) {
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

  /**
   **/
  public UserInner roles(List<String> roles) {
    this.roles = roles;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("roles")

  public List<String> getRoles() {
    return roles;
  }
  public void setRoles(List<String> roles) {
    this.roles = roles;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserInner userInner = (UserInner) o;
    return Objects.equals(userId, userInner.userId) &&
        Objects.equals(isAdmin, userInner.isAdmin) &&
        Objects.equals(roles, userInner.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, isAdmin, roles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInner {\n");

    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    isAdmin: ").append(toIndentedString(isAdmin)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
