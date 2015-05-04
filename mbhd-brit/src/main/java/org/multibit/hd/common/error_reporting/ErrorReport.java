package org.multibit.hd.common.error_reporting;

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
public class ErrorReport {

  private String osName;
  private String osVersion;
  private boolean a64Bit;
  private String appVersion;
  private String userNotes;

  // The error report log entries
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

  public void set64Bit(boolean a64Bit) {
    this.a64Bit = a64Bit;
  }

  public boolean isA64Bit() {
    return a64Bit;
  }

  public void setA64Bit(boolean a64Bit) {
    this.a64Bit = a64Bit;
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
