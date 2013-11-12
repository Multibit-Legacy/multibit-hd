package org.multibit.hd.ui.javafx.logging;

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
import org.multibit.hd.ui.javafx.config.LoggingConfiguration;

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

  private LogbackFactory() { /* singleton */ }

  public static SyslogAppender buildSyslogAppender(LoggingConfiguration.SyslogConfiguration syslog,
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

  public static FileAppender<ILoggingEvent> buildFileAppender(LoggingConfiguration.FileConfiguration file,
                                                              LoggerContext context,
                                                              String logFormat) {
    final LogFormatter formatter = new LogFormatter(context, file.getTimeZone());

    if (logFormat != null) {
      formatter.setPattern(logFormat);
    }
    formatter.start();

    final FileAppender<ILoggingEvent> appender =
      file.isArchive() ? new RollingFileAppender<ILoggingEvent>() :
        new FileAppender<ILoggingEvent>();

    appender.setAppend(true);
    appender.setContext(context);
    appender.setLayout(formatter);
    appender.setFile(file.getCurrentLogFilename());
    appender.setPrudent(false);

    addThresholdFilter(appender, file.getThreshold());

    if (file.isArchive()) {

      final DefaultTimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> triggeringPolicy =
        new DefaultTimeBasedFileNamingAndTriggeringPolicy<>();
      triggeringPolicy.setContext(context);

      final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
      rollingPolicy.setContext(context);
      rollingPolicy.setFileNamePattern(file.getArchivedLogFilenamePattern());
      rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(triggeringPolicy);
      triggeringPolicy.setTimeBasedRollingPolicy(rollingPolicy);
      rollingPolicy.setMaxHistory(file.getArchivedFileCount());

      ((RollingFileAppender<ILoggingEvent>) appender).setRollingPolicy(rollingPolicy);
      ((RollingFileAppender<ILoggingEvent>) appender).setTriggeringPolicy(triggeringPolicy);

      rollingPolicy.setParent(appender);
      rollingPolicy.start();
    }

    appender.stop();
    appender.start();

    return appender;
  }

  public static ConsoleAppender<ILoggingEvent> buildConsoleAppender(LoggingConfiguration.ConsoleConfiguration console,
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

  private static void addThresholdFilter(FilterAttachable<ILoggingEvent> appender, Level threshold) {
    final ThresholdFilter filter = new ThresholdFilter();
    filter.setLevel(threshold.toString());
    filter.start();
    appender.addFilter(filter);
  }
}
