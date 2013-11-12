package org.multibit.hd.ui.javafx.config;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>Configuration to provide the following to logging framework:</p>
 * <ul>
 * <li>Configuration of console logger</li>
 * <li>Configuration of file logger</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
@SuppressWarnings("UnusedDeclaration")
public class LoggingConfiguration {

  static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  @JsonProperty
  private Level level = Level.INFO;

  @JsonProperty
  private String[] loggers;

  @JsonProperty
  private ConsoleConfiguration console = new ConsoleConfiguration();

  @JsonProperty
  private FileConfiguration file = new FileConfiguration();

  @JsonProperty
  private SyslogConfiguration syslog = new SyslogConfiguration();

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public String[] getLoggers() {
    return loggers;
  }

  @SuppressWarnings("unchecked")
  public void setLoggers(String[] loggers) {
    this.loggers = loggers;
  }

  public ConsoleConfiguration getConsoleConfiguration() {
    return console;
  }

  public void setConsoleConfiguration(ConsoleConfiguration config) {
    this.console = config;
  }

  public FileConfiguration getFileConfiguration() {
    return file;
  }

  public void setFileConfiguration(FileConfiguration config) {
    this.file = config;
  }

  public SyslogConfiguration getSyslogConfiguration() {
    return syslog;
  }

  public void setSyslogConfiguration(SyslogConfiguration config) {
    this.syslog = config;
  }

  // Output configurations (console, file, system etc)

  public static class ConsoleConfiguration {

    @JsonProperty
    private boolean enabled = true;

    @JsonProperty
    private Level threshold = Level.ALL;

    @JsonProperty
    private TimeZone timeZone = UTC;

    @JsonProperty
    private String logFormat;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public Level getThreshold() {
      return threshold;
    }

    public void setThreshold(Level threshold) {
      this.threshold = threshold;
    }

    public TimeZone getTimeZone() {
      return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
      this.timeZone = timeZone;
    }

    public String getLogFormat() {
      return logFormat;
    }

    public void setLogFormat(String logFormat) {
      this.logFormat = logFormat;
    }
  }

  public static class FileConfiguration {

    @JsonProperty
    private boolean enabled = false;

    @JsonProperty
    private Level threshold = Level.ALL;

    @JsonProperty
    private String currentLogFilename;

    @JsonProperty
    private boolean archive = true;

    @JsonProperty
    private String archivedLogFilenamePattern;

    @JsonProperty
    private int archivedFileCount = 5;

    @JsonProperty
    private TimeZone timeZone = UTC;

    @JsonProperty
    private String logFormat;

    public boolean isValidArchiveConfiguration() {
      return !enabled || !archive || (archivedLogFilenamePattern != null);
    }

    public boolean isConfigured() {
      return !enabled || (currentLogFilename != null);
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public Level getThreshold() {
      return threshold;
    }

    public void setThreshold(Level level) {
      this.threshold = level;
    }

    public String getCurrentLogFilename() {
      return currentLogFilename;
    }

    public void setCurrentLogFilename(String filename) {
      this.currentLogFilename = filename;
    }

    public boolean isArchive() {
      return archive;
    }

    public void setArchive(boolean archive) {
      this.archive = archive;
    }

    public int getArchivedFileCount() {
      return archivedFileCount;
    }

    public void setArchivedFileCount(int count) {
      this.archivedFileCount = count;
    }

    public String getArchivedLogFilenamePattern() {
      return archivedLogFilenamePattern;
    }

    public void setArchivedLogFilenamePattern(String pattern) {
      this.archivedLogFilenamePattern = pattern;
    }

    public TimeZone getTimeZone() {
      return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
      this.timeZone = timeZone;
    }

    public String getLogFormat() {
      return logFormat;
    }

    public void setLogFormat(String logFormat) {
      this.logFormat = logFormat;
    }
  }

  public static class SyslogConfiguration {

    public enum Facility {
      AUTH, AUTHPRIV, DAEMON, CRON, FTP, LPR, KERN, MAIL, NEWS, SYSLOG, USER, UUCP,
      LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5, LOCAL6, LOCAL7;

      @Override
      @JsonValue
      public String toString() {
        return super.toString().replace("_", "+").toLowerCase(Locale.ENGLISH);
      }

      @JsonCreator
      public static Facility parse(String facility) {
        return valueOf(facility.toUpperCase(Locale.ENGLISH).replace('+', '_'));
      }
    }

    @JsonProperty
    private boolean enabled = false;

    @JsonProperty
    private Level threshold = Level.ALL;

    @JsonProperty
    private String host = "localhost";

    @JsonProperty
    private Facility facility = Facility.LOCAL0;

    @JsonProperty
    private TimeZone timeZone = UTC;

    @JsonProperty
    private String logFormat;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public Level getThreshold() {
      return threshold;
    }

    public void setThreshold(Level threshold) {
      this.threshold = threshold;
    }

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public Facility getFacility() {
      return facility;
    }

    public void setFacility(Facility facility) {
      this.facility = facility;
    }

    public TimeZone getTimeZone() {
      return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
      this.timeZone = timeZone;
    }

    public String getLogFormat() {
      return logFormat;
    }

    public void setLogFormat(String logFormat) {
      this.logFormat = logFormat;
    }
  }

}
