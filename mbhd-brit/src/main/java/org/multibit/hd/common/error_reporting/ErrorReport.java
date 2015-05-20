package org.multibit.hd.common.error_reporting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * <p>Value object to provide the following to error reporting system:</p>
 * <ul>
 * <li>Storage of a user error report</li>
 * </ul>
 *
 * @since 0.1.0
 *  
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "os_name",
  "os_version",
  "os_arch",
  "app_version",
  "user_notes",
  "log_entries"
})
public class ErrorReport {

  @JsonProperty("os_name")
  private String osName;
  @JsonProperty("os_version")
  private String osVersion;
  @JsonProperty("os_arch")
  private String osArch;
  @JsonProperty("app_version")
  private String appVersion;
  @JsonProperty("user_notes")
  private String userNotes;

  // The error report log entries
  @JsonProperty("log_entries")
  private List<ErrorReportLogEntry> logEntries = Lists.newArrayList();

  public void setOsName(String osName) {
    this.osName = osName;
  }

  public String getOsName() {
    return osName;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public String getOsArch() {
    return osArch;
  }

  public void setOsArch(String osArch) {
    this.osArch = osArch;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setUserNotes(String userNotes) {
    this.userNotes = userNotes;
  }

  public String getUserNotes() {
    return userNotes;
  }

  public List<ErrorReportLogEntry> getLogEntries() {
    return logEntries;
  }

  public void setLogEntries(List<ErrorReportLogEntry> logEntries) {
    this.logEntries = logEntries;
  }
}
