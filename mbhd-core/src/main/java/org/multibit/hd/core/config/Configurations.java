package org.multibit.hd.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Optional;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.managers.InstallationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * <p>Utility to provide the following to configuration:</p>
 * <ul>
 * <li>Default configuration</li>
 * <li>Default configuration</li>
 * <li>Read/write configuration files</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class Configurations {

  private static final Logger log = LoggerFactory.getLogger(Configurations.class);

  /**
   * The current runtime configuration (preserved across soft restarts)
   */
  public static Configuration currentConfiguration;

  /**
   * The previous configuration (preserved across soft restarts)
   */
  public static Configuration previousConfiguration;

  /**
   * Utilities have private constructors
   */
  private Configurations() {
  }

  /**
   * @return A new default configuration based on the UK locale
   */
  public static Configuration newDefaultConfiguration() {

    return new Configuration();

  }

  /**
   * <p>Handle the process of switching to a new configuration</p>
   *
   * <p>Provides locale and event notification of the change</p>
   *
   * @param newConfiguration The new configuration
   */
  public static synchronized void switchConfiguration(Configuration newConfiguration) {

    // Keep track of the previous configuration
    previousConfiguration = currentConfiguration;

    // Ensure the new configuration has the same frame bounds since MainView won't have updated it
    newConfiguration.getAppearance().setLastFrameBounds(currentConfiguration.getAppearance().getLastFrameBounds());

    // Set the replacement
    currentConfiguration = newConfiguration;

    // Persist the changes
    persistCurrentConfiguration();

    // Update any JVM classes
    Locale.setDefault(currentConfiguration.getLocale());

    // Notify interested parties
    CoreEvents.fireConfigurationChangedEvent();

  }

  /**
   * <p>Persist the current configuration</p>
   *
   * <p>No locale change or event takes place (see {@link #switchConfiguration(Configuration)})</p>
   */
  public static synchronized void persistCurrentConfiguration() {

    // Persist the new configuration
    try (FileOutputStream fos = new FileOutputStream(InstallationManager.getConfigurationFile())) {

      Configurations.writeYaml(fos, Configurations.currentConfiguration);

    } catch (IOException e) {
      ExceptionHandler.handleThrowable(e);
    }
  }

  /**
   * <p>Reads the YAML from the given input stream</p>
   *
   * @param is    The input stream to use (not closed)
   * @param clazz The expected root class from the YAML
   *
   * @return The configuration data (<code>Configuration</code>, <code>Wallet Summary</code> etc) if present
   */
  public static synchronized <T> Optional<T> readYaml(InputStream is, Class<T> clazz) {

    log.debug("Reading configuration data...");

    Optional<T> configuration;

    // Read the external configuration
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    try {
      configuration = Optional.fromNullable(mapper.readValue(is, clazz));
    } catch (IOException e) {
      log.warn(e.getMessage());
      configuration = Optional.absent();
    }
    if (configuration == null) {
      log.warn("YAML was not read.");
    }

    return configuration;

  }

  /**
   * <p>Writes the YAML to the application directory</p>
   *
   * @param os            The output stream to use (not closed)
   * @param configuration The configuration to write as YAML
   */
  public static synchronized <T> void writeYaml(OutputStream os, T configuration) {

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    try {
      mapper.writeValue(os, configuration);
    } catch (IOException e) {
      ExceptionHandler.handleThrowable(e);
    }

  }

}
