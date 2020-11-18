package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;
import javax.validation.Valid;

public class ApiUpdateRequestBody   {
  private @Valid String apiName = null;
  private @Valid String nodeId = null;
  private @Valid Boolean tracing = null;

  /**
   **/
  public ApiUpdateRequestBody apiName(String apiName) {
    this.apiName = apiName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("apiName")

  public String getApiName() {
    return apiName;
  }
  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  /**
   **/
  public ApiUpdateRequestBody nodeId(String nodeId) {
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
  public ApiUpdateRequestBody tracing(Boolean tracing) {
    this.tracing = tracing;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("tracing")

  public Boolean isTracing() {
    return tracing;
  }
  public void setTracing(Boolean tracing) {
    this.tracing = tracing;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiUpdateRequestBody apiUpdateRequestBody = (ApiUpdateRequestBody) o;
    return Objects.equals(apiName, apiUpdateRequestBody.apiName) &&
        Objects.equals(nodeId, apiUpdateRequestBody.nodeId) &&
        Objects.equals(tracing, apiUpdateRequestBody.tracing);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiName, nodeId, tracing);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiUpdateRequestBody {\n");

    sb.append("    apiName: ").append(toIndentedString(apiName)).append("\n");
    sb.append("    nodeId: ").append(toIndentedString(nodeId)).append("\n");
    sb.append("    tracing: ").append(toIndentedString(tracing)).append("\n");
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
