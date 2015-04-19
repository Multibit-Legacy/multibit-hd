package org.multibit.hd.core.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.net.SyslogAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterAttachable;
import com.google.common.base.Optional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.multibit.hd.core.config.LoggingConfiguration;

import java.io.File;

/**
 * <p>Factory to provide the following to logging framework:</p>
 * <ul>
 * <li>Creation of various appenders for Logback</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class LogbackFactory {

  /**
   * The current file appender
   */
  private static FileAppender<ILoggingEvent> currentFileAppender = null;

  private LogbackFactory() { /* singleton */ }

  public static SyslogAppender buildSyslogAppender(
    LoggingConfiguration.SyslogConfiguration syslog,
    LoggerContext context,
    String name,
    String logFormat) {
    final SyslogAppender appender = new SyslogAppender();
    appender.setName(name);
    appender.setContext(context);
    appender.setSyslogHost(syslog.getHost());
    appender.setFacility(syslog.getFacility().toString());
    addThresholdFilter(appender, syslog.getThreshold());

    if (logFormat != null) {
      appender.setSuffixPattern(logFormat);
    }

    appender.start();

    return appender;
  }

  // There are restricted possibilities for the rolling file appender
  @SuppressFBWarnings({"BC_UNCONFIRMED_CAST"})
  public static FileAppender<ILoggingEvent> buildFileAppender(
    LoggingConfiguration.FileConfiguration fileConfiguration,
    LoggerContext context,
    String logFormat) {

    final LogFormatter formatter = new LogFormatter(context, fileConfiguration.getTimeZone());

    if (logFormat != null) {
      formatter.setPattern(logFormat);
    }
    formatter.start();

    final FileAppender<ILoggingEvent> appender = fileConfiguration.isArchive() ?
      new RollingFileAppender<ILoggingEvent>() :
      new FileAppender<ILoggingEvent>();

    appender.setAppend(true);
    appender.setContext(context);
    appender.setLayout(formatter);
    appender.setFile(fileConfiguration.getCurrentLogFilename());
    appender.setPrudent(false); // We don't expect multiple JVMs

    addThresholdFilter(appender, fileConfiguration.getThreshold());

    if (fileConfiguration.isArchive()) {

      final DefaultTimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> triggeringPolicy =
        new DefaultTimeBasedFileNamingAndTriggeringPolicy<>();
      triggeringPolicy.setContext(context);

      final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
      rollingPolicy.setContext(context);
      rollingPolicy.setFileNamePattern(fileConfiguration.getArchivedLogFilenamePattern());
      rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(triggeringPolicy);
      triggeringPolicy.setTimeBasedRollingPolicy(rollingPolicy);
      rollingPolicy.setMaxHistory(fileConfiguration.getArchivedFileCount());

      ((RollingFileAppender<ILoggingEvent>) appender).setRollingPolicy(rollingPolicy);
      ((RollingFileAppender<ILoggingEvent>) appender).setTriggeringPolicy(triggeringPolicy);

      rollingPolicy.setParent(appender);
      rollingPolicy.start();
    }

    appender.stop();
    appender.start();

    currentFileAppender = appender;

    return appender;
  }

  public static ConsoleAppender<ILoggingEvent> buildConsoleAppender(
    LoggingConfiguration.ConsoleConfiguration console,
    LoggerContext context,
    String logFormat) {
    final LogFormatter formatter = new LogFormatter(context, console.getTimeZone());
    if (logFormat != null) {
      formatter.setPattern(logFormat);
    }
    formatter.start();

    final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
    appender.setContext(context);
    appender.setLayout(formatter);
    addThresholdFilter(appender, console.getThreshold());
    appender.start();

    return appender;
  }

  /**
   * @return The current file appender logging file (useful for error reporting)
   */
  public static Optional<File> getCurrentLoggingFile() {

    if (currentFileAppender != null) {
      return Optional.of(new File(currentFileAppender.getFile()));
    }

    return Optional.absent();

  }

  private static void addThresholdFilter(FilterAttachable<ILoggingEvent> appender, Level threshold) {
    final ThresholdFilter filter = new ThresholdFilter();
    filter.setLevel(threshold.toString());
    filter.start();
    appender.addFilter(filter);
  }
}
