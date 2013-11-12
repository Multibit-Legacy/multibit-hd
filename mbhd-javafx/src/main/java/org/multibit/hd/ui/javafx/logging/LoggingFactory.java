package org.multibit.hd.ui.javafx.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.jmx.JMXConfigurator;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import org.multibit.hd.ui.javafx.config.LoggingConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
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

  public static void bootstrap() {

    // Initially configure for WARN+ console logging
    final LoggingConfiguration.ConsoleConfiguration console = new LoggingConfiguration.ConsoleConfiguration();
    console.setEnabled(true);
    console.setTimeZone(TimeZone.getDefault());
    console.setThreshold(Level.DEBUG);

    final Logger root = getCleanRoot();
    root.addAppender(LogbackFactory.buildConsoleAppender(console, root.getLoggerContext(), null));
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


    final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    try {
      final ObjectName objectName = new ObjectName("com.ir:type=Logging");
      if (!server.isRegistered(objectName)) {
        server.registerMBean(new JMXConfigurator(root.getLoggerContext(),
          server,
          objectName),
          objectName);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
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

    // Decode the array (effectively a map)
    String[] loggers = config.getLoggers();
    if (loggers != null) {
      for (String logger : loggers) {

        String[] tokens = logger.split(":");
        String key = tokens[0];
        Level level = Level.valueOf(tokens[1]);

        ((Logger) LoggerFactory.getLogger(key)).setLevel(level);
      }
    }

    return root;
  }

  private static Logger getCleanRoot() {
    final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    root.detachAndStopAllAppenders();
    return root;
  }
}

