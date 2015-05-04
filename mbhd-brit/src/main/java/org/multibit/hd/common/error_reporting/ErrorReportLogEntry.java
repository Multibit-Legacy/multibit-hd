package org.multibit.hd.common.error_reporting;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *  
 */
import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "@timestamp",
  "@version",
  "message",
  "logger_name",
  "thread_name",
  "level",
  "level_value"
})
public class ErrorReportLogEntry {

  @JsonProperty("@timestamp")
  private String timestamp;
  @JsonProperty("@version")
  private Integer version;
  @JsonProperty("message")
  private String message;
  @JsonProperty("logger_name")
  private String loggerName;
  @JsonProperty("thread_name")
  private String threadName;
  @JsonProperty("level")
  private String level;
  @JsonProperty("level_value")
  private Integer levelValue;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  public void setAdditionalProperties(Map<String, Object> additionalProperties) {
    this.additionalProperties = additionalProperties;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public Integer getLevelValue() {
    return levelValue;
  }

  public void setLevelValue(Integer levelValue) {
    this.levelValue = levelValue;
  }

  public String getLoggerName() {
    return loggerName;
  }

  public void setLoggerName(String loggerName) {
    this.loggerName = loggerName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getThreadName() {
    return threadName;
  }

  public void setThreadName(String threadName) {
    this.threadName = threadName;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }
}
