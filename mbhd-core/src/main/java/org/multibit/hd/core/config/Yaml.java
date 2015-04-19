package org.multibit.hd.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Optional;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Utility to provide the following to application:</p>
 * <ul>
 * <li>Reading and writing YAML files</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Yaml {

  private static final Logger log = LoggerFactory.getLogger(Yaml.class);

  private static ObjectMapper mapper = null;

  /**
   * Utilities have private constructors
   */
  private Yaml() {
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
    log.debug("Reading YAML data...");

    Optional<T> value;

    // Read the external configuration
    if (mapper == null) {
      mapper = new ObjectMapper(new YAMLFactory());
    }

    try {
      value = Optional.fromNullable(mapper.readValue(is, clazz));
    } catch (IOException e) {
      log.warn(e.getMessage());
      value = Optional.absent();
    }
    if (value == null) {
      log.warn("YAML was not read.");
    }

    return value;
  }

  /**
   * <p>Writes the YAML to the application directory</p>
   *
   * @param os            The output stream to use (not closed)
   * @param configuration The configuration to write as YAML
   */
  public static synchronized <T> void writeYaml(OutputStream os, T configuration) {
    log.debug("Writing YAML data...");

    if (mapper == null) {
      mapper = new ObjectMapper(new YAMLFactory());
    }

    try {
      mapper.writeValue(os, configuration);
    } catch (IOException e) {
      ExceptionHandler.handleThrowable(e);
    }
  }
}
