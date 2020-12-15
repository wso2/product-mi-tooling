package org.wso2.micro.integrator.dashboard.backend.rest.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class TaskListInner   {
  private @Valid String taskName = null;
  private @Valid List<Object> nodes = new ArrayList<Object>();

  /**
   **/
  public TaskListInner taskName(String taskName) {
    this.taskName = taskName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("taskName")

  public String getTaskName() {
    return taskName;
  }
  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  /**
   **/
  public TaskListInner nodes(List<Object> nodes) {
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
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaskListInner taskListInner = (TaskListInner) o;
    return Objects.equals(taskName, taskListInner.taskName) &&
        Objects.equals(nodes, taskListInner.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taskName, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TaskListInner {\n");
    
    sb.append("    taskName: ").append(toIndentedString(taskName)).append("\n");
    sb.append("    nodes: ").append(toIndentedString(nodes)).append("\n");
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
