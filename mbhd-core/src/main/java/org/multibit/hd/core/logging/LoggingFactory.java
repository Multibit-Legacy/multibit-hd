package org.multibit.hd.core.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import org.multibit.hd.core.config.LoggingConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Map;
import java.util.TimeZone;

/**
 * <p>Factory to provide the following to logging framework:</p>
 * <ul>
 * <li>Initial bootstrap and configuration</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class LoggingFactory {

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(LoggingFactory.class);

  public static void bootstrap() {

    // Initially configure for DEBUG console logging
    final LoggingConfiguration.ConsoleConfiguration console = new LoggingConfiguration.ConsoleConfiguration();
    console.setEnabled(true);
    console.setTimeZone(TimeZone.getDefault());
    console.setThreshold(Level.DEBUG);

    final Logger root = getCleanRoot();
    root.addAppender(LogbackFactory.buildConsoleAppender(
        console,
        root.getLoggerContext(),
        null
      ));

    final LoggingConfiguration.FileConfiguration file = new LoggingConfiguration.FileConfiguration();
    root.addAppender(LogbackFactory.buildFileAppender(file, root.getLoggerContext(), null));

    // Add this to indicate that logging has bootstrapped with synchronous default settings
    log.info("LoggingFactory bootstrap completed.");

  }

  private final LoggingConfiguration config;
  private final String name;

  public LoggingFactory(LoggingConfiguration config, String name) {
    this.config = config;
    this.name = name;
  }

  public void configure() {

    hijackJDKLogging();

    final Logger root = configureLevels();

    final LoggingConfiguration.ConsoleConfiguration console = config.getConsoleConfiguration();
    if (console.isEnabled()) {
      root.addAppender(AsyncAppender.wrap(
        LogbackFactory.buildConsoleAppender(
          console,
          root.getLoggerContext(),
          console.getLogFormat())));
    }

    final LoggingConfiguration.FileConfiguration file = config.getFileConfiguration();
    if (file.isEnabled()) {
      root.addAppender(AsyncAppender.wrap(
        LogbackFactory.buildFileAppender(
          file,
          root.getLoggerContext(),
          file.getLogFormat())));
    }

    final LoggingConfiguration.SyslogConfiguration syslog = config.getSyslogConfiguration();
    if (syslog.isEnabled()) {
      root.addAppender(AsyncAppender.wrap(
        LogbackFactory.buildSyslogAppender(
          syslog,
          root.getLoggerContext(),
          name,
          syslog.getLogFormat())));
    }
  }

  private void hijackJDKLogging() {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  private Logger configureLevels() {

    final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    root.getLoggerContext().reset();

    final LevelChangePropagator propagator = new LevelChangePropagator();
    propagator.setContext(root.getLoggerContext());
    propagator.setResetJUL(true);

    root.getLoggerContext().addListener(propagator);

    root.setLevel(config.getLevel());

    // Decode the packages and levels
    for (Map.Entry<String, Level> entry : config.getLoggers().entrySet()) {
      ((Logger) LoggerFactory.getLogger(entry.getKey())).setLevel(entry.getValue());
    }

    return root;
  }

  private static Logger getCleanRoot() {
    final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    root.detachAndStopAllAppenders();
    return root;
  }
}

