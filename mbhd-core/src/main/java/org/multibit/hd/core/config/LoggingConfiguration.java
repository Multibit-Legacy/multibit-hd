package org.multibit.hd.core.config;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.collect.Maps;
import org.multibit.hd.core.managers.InstallationManager;

import java.util.Locale;
import java.util.Map;
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

  private Level level = Level.WARN;

  private Map<String, Level> loggers = Maps.newHashMap();

  private ConsoleConfiguration console = new ConsoleConfiguration();

  private FileConfiguration file = new FileConfiguration();

  private SyslogConfiguration syslog = new SyslogConfiguration();

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Any unknown objects in the configuration go here (preserve order of insertion)
   */
  private Map<String, Object> other = Maps.newLinkedHashMap();

  /**
   * @return The map of any unknown objects in the configuration at this level
   */
  @JsonAnyGetter
  public Map<String, Object> any() {
    return other;
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    other.put(name, value);
  }

  public LoggingConfiguration() {

    loggers.put("org.multibit", Level.DEBUG);
    loggers.put("com.xeiam", Level.WARN);
    loggers.put("org.bitcoinj", Level.ERROR);
    loggers.put("PeerConnectionLog", Level.INFO);
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public Map<String, Level> getLoggers() {
    return loggers;
  }

  public void setLoggers(Map<String, Level> loggers) {
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

  /**
   * @return A deep copy of this object
   */
  public LoggingConfiguration deepCopy() {

    LoggingConfiguration configuration = new LoggingConfiguration();

    // Unknown properties
    for (Map.Entry<String, Object> entry : any().entrySet()) {
      configuration.set(entry.getKey(), entry.getValue());
    }

    // Known properties
    // Only configure the basics to match the properties file
    configuration.setLevel(getLevel());
    configuration.setLoggers(getLoggers());
    configuration.getFileConfiguration().setArchivedLogFilenamePattern(getFileConfiguration().getArchivedLogFilenamePattern());
    configuration.getFileConfiguration().setCurrentLogFilename(getFileConfiguration().getCurrentLogFilename());

    return configuration;
  }

  // Output configurations (console, file, system etc)

  public static class ConsoleConfiguration {

    private boolean enabled = true;

    private Level threshold = Level.ALL;

    private TimeZone timeZone = UTC;

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

    /**
     * Only produce file logs when not in test
     */
    private boolean enabled = !InstallationManager.unrestricted;

    private Level threshold = Level.ALL;

    private boolean archive = true;

    /**
     * Ensure we reference the users
     */
    private String currentLogFilename = InstallationManager.getOrCreateApplicationDataDirectory() + "/logs/multibit-hd.log";

    /**
     * Only a single log per day
     */
    private String archivedLogFilenamePattern=InstallationManager.getOrCreateApplicationDataDirectory() + "/logs/multibit-hd-%d{yyyy-MM-dd}.log.gz";

    /**
     * 14 allows for 2 weeks of intensive use
     */
    private int archivedFileCount = 14;

    /**
     * UTC in file logs ensure consistency when users post logs in
     */
    private TimeZone timeZone = UTC;

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
      public String toString() {
        return super.toString().replace("_", "+").toLowerCase(Locale.ENGLISH);
      }

      public static Facility parse(String facility) {
        return valueOf(facility.toUpperCase(Locale.ENGLISH).replace('+', '_'));
      }
    }

    private boolean enabled = false;

    private Level threshold = Level.ALL;

    private String host = "localhost";

    private Facility facility = Facility.LOCAL0;

    private TimeZone timeZone = UTC;

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
