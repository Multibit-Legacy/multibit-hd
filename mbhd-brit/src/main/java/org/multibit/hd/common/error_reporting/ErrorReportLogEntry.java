package org.multibit.hd.common.error_reporting;

/**
 * <p>Value object to provide the following to error reporting system:</p>
 * <ul>
 * <li>Storage of a log entry as part of an error report</li>
 * </ul>
 *
 * @since 0.1.0
 *  
 */
import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "@timestamp",
  "level",
  "thread_name",
  "logger_name",
  "message",
  "stack_trace",
  "level_value",
  "@version"
})
public class ErrorReportLogEntry {

  @JsonProperty("@timestamp")
  private String timestamp;
  @JsonProperty("@version")
  private Integer version;
  @JsonProperty("message")
  private String message;
  @JsonProperty("stack_trace")
  private String stackTrace;
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

  public String getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(String stackTrace) {
    this.stackTrace = stackTrace;
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
