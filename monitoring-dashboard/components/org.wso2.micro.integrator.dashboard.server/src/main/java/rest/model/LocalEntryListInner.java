package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

public class LocalEntryListInner   {
  private @Valid String entryName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public LocalEntryListInner entryName(String entryName) {
    this.entryName = entryName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("entryName")

  public String getEntryName() {
    return entryName;
  }
  public void setEntryName(String entryName) {
    this.entryName = entryName;
  }

  /**
   **/
  public LocalEntryListInner nodes(List<Object> nodes) {
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
    LocalEntryListInner localEntryListInner = (LocalEntryListInner) o;
    return Objects.equals(entryName, localEntryListInner.entryName) &&
        Objects.equals(nodes, localEntryListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entryName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocalEntryListInner {\n");

    sb.append("    entryName: ").append(toIndentedString(entryName)).append("\n");
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
