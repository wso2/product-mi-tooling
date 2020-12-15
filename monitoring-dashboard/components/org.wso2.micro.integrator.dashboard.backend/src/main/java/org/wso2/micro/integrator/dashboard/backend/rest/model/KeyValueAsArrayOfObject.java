package org.wso2.micro.integrator.dashboard.backend.rest.model;

import java.util.ArrayList;
import java.util.List;
import org.wso2.micro.integrator.dashboard.backend.rest.model.KeyValue;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class KeyValueAsArrayOfObject   {
  private @Valid String key = null;
  private @Valid List<KeyValue> value = new ArrayList<KeyValue>();

  /**
   **/
  public KeyValueAsArrayOfObject key(String key) {
    this.key = key;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("key")

  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }

  /**
   **/
  public KeyValueAsArrayOfObject value(List<KeyValue> value) {
    this.value = value;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("value")

  public List<KeyValue> getValue() {
    return value;
  }
  public void setValue(List<KeyValue> value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KeyValueAsArrayOfObject keyValueAsArrayOfObject = (KeyValueAsArrayOfObject) o;
    return Objects.equals(key, keyValueAsArrayOfObject.key) &&
        Objects.equals(value, keyValueAsArrayOfObject.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class KeyValueAsArrayOfObject {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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
