package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

public class MessageProcessorListInner   {
  private @Valid String processorName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public MessageProcessorListInner processorName(String processorName) {
    this.processorName = processorName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("processorName")

  public String getProcessorName() {
    return processorName;
  }
  public void setProcessorName(String processorName) {
    this.processorName = processorName;
  }

  /**
   **/
  public MessageProcessorListInner nodes(List<Object> nodes) {
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
    MessageProcessorListInner messageProcessorListInner = (MessageProcessorListInner) o;
    return Objects.equals(processorName, messageProcessorListInner.processorName) &&
        Objects.equals(nodes, messageProcessorListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(processorName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MessageProcessorListInner {\n");

    sb.append("    processorName: ").append(toIndentedString(processorName)).append("\n");
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
