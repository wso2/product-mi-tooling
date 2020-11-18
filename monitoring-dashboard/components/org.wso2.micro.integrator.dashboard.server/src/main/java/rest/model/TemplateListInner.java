package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

public class TemplateListInner   {
  private @Valid String templateName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public TemplateListInner templateName(String templateName) {
    this.templateName = templateName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("templateName")

  public String getTemplateName() {
    return templateName;
  }
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  /**
   **/
  public TemplateListInner nodes(List<Object> nodes) {
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
    TemplateListInner templateListInner = (TemplateListInner) o;
    return Objects.equals(templateName, templateListInner.templateName) &&
        Objects.equals(nodes, templateListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(templateName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TemplateListInner {\n");

    sb.append("    templateName: ").append(toIndentedString(templateName)).append("\n");
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
