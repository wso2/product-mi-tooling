package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

public class CAppListInner   {
  private @Valid String cAppName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public CAppListInner cAppName(String cAppName) {
    this.cAppName = cAppName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("cAppName")

  public String getCAppName() {
    return cAppName;
  }
  public void setCAppName(String cAppName) {
    this.cAppName = cAppName;
  }

  /**
   **/
  public CAppListInner nodes(List<Object> nodes) {
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CAppListInner cappListInner = (CAppListInner) o;
    return Objects.equals(cAppName, cappListInner.cAppName) &&
        Objects.equals(nodes, cappListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cAppName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CAppListInner {\n");

    sb.append("    cAppName: ").append(toIndentedString(cAppName)).append("\n");
    sb.append("    nodes: ").append(toIndentedString(nodes)).append("\n");
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
