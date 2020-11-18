package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

public class SequenceListInner   {
  private @Valid String sequenceName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public SequenceListInner sequenceName(String sequenceName) {
    this.sequenceName = sequenceName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("sequenceName")

  public String getSequenceName() {
    return sequenceName;
  }
  public void setSequenceName(String sequenceName) {
    this.sequenceName = sequenceName;
  }

  /**
   **/
  public SequenceListInner nodes(List<Object> nodes) {
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
    SequenceListInner sequenceListInner = (SequenceListInner) o;
    return Objects.equals(sequenceName, sequenceListInner.sequenceName) &&
        Objects.equals(nodes, sequenceListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sequenceName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SequenceListInner {\n");

    sb.append("    sequenceName: ").append(toIndentedString(sequenceName)).append("\n");
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
