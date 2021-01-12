/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.wso2.ei.dashboard.core.rest.model;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class LogConfigInner   {
  private @Valid String loggerName = null;
  private @Valid String className = null;
public enum LevelEnum {

    FALSE(String.valueOf("false")), TRACE(String.valueOf("Trace")), DEBUG(String.valueOf("DEBUG")), INFO(String.valueOf("INFO")), WARN(String.valueOf("WARN")), ERROR(String.valueOf("ERROR")), FATAL(String.valueOf("FATAL"));


    private String value;

    LevelEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static LevelEnum fromValue(String v) {
        for (LevelEnum b : LevelEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  private @Valid LevelEnum level = null;

  /**
   **/
  public LogConfigInner loggerName(String loggerName) {
    this.loggerName = loggerName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("loggerName")

  public String getLoggerName() {
    return loggerName;
  }
  public void setLoggerName(String loggerName) {
    this.loggerName = loggerName;
  }

  /**
   **/
  public LogConfigInner className(String className) {
    this.className = className;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("className")

  public String getClassName() {
    return className;
  }
  public void setClassName(String className) {
    this.className = className;
  }

  /**
   **/
  public LogConfigInner level(LevelEnum level) {
    this.level = level;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("level")

  public LevelEnum getLevel() {
    return level;
  }
  public void setLevel(LevelEnum level) {
    this.level = level;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogConfigInner logConfigInner = (LogConfigInner) o;
    return Objects.equals(loggerName, logConfigInner.loggerName) &&
        Objects.equals(className, logConfigInner.className) &&
        Objects.equals(level, logConfigInner.level);
  }

  @Override
  public int hashCode() {
    return Objects.hash(loggerName, className, level);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogConfigInner {\n");
    
    sb.append("    loggerName: ").append(toIndentedString(loggerName)).append("\n");
    sb.append("    className: ").append(toIndentedString(className)).append("\n");
    sb.append("    level: ").append(toIndentedString(level)).append("\n");
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
