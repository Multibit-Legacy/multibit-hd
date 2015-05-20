package org.multibit.hd.core.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>Utility to provide the following to application:</p>
 * <ul>
 * <li>Reading and writing JSON files</li>
 * </ul>
 *
 * @since 0.1.0
 *  
 */
public class Json {

  private static final Logger log = LoggerFactory.getLogger(Json.class);

  private static ObjectMapper mapper = null;

  /**
   * Utilities have private constructors
   */
  private Json() {
  }

  /**
   * <p>Reads the JSON from the given input stream</p>
   *
   * @param payload The JSON
   * @param clazz   The expected root class from the JSON
   *
   * @return The object mapped to the JSON if present
   */
  public static synchronized <T> Optional<T> readJson(byte[] payload, Class<T> clazz) {
    log.trace("Reading JSON data...");

    Optional<T> value;

    // Read the external configuration
    if (mapper == null) {
      mapper = new ObjectMapper(new JsonFactory());
    }

    try {
      value = Optional.fromNullable(mapper.readValue(payload, clazz));
    } catch (IOException e) {
      log.warn(e.getMessage());
      value = Optional.absent();
    }
    if (value == null) {
      log.warn("JSON was not read.");
    }

    return value;
  }

  /**
   * <p>Writes the JSON to the application directory</p>
   *
   * @param os            The output stream to use (not closed)
   * @param configuration The configuration to write as JSON
   */
  public static synchronized <T> void writeJson(OutputStream os, T configuration) {
    log.debug("Writing JSON data...");

    if (mapper == null) {
      mapper = new ObjectMapper(new JsonFactory());
    }

    try {
      mapper.writeValue(os, configuration);
    } catch (IOException e) {
      ExceptionHandler.handleThrowable(e);
    }
  }

}
