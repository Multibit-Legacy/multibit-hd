package org.multibit.hd.ui.javafx.config;

import ch.qos.logback.classic.Level;
import com.google.common.base.Preconditions;
import org.multibit.hd.ui.javafx.i18n.BitcoinSymbol;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.multibit.hd.ui.javafx.config.Configurations.*;

/**
 * <p>Adapter to provide the following to application:</p>
 * <ul>
 * <li>Creates a Configuration from the given Properties performing validation</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ConfigurationAdapter {

  private final Properties properties;
  private final Configuration configuration = new Configuration();

  public ConfigurationAdapter(Properties properties) {
    this.properties = properties;
  }

  /**
   * @return A new Configuration based on the properties
   */
  public Configuration adapt() {

    for (Map.Entry<Object, Object> entry : properties.entrySet()) {

      String key = (String) entry.getKey();
      String value = (String) entry.getValue();

      // Application

      // Bitcoin
      if (BITCOIN_SYMBOL.equalsIgnoreCase(key)) {
        configuration.setBitcoinSymbol(BitcoinSymbol.valueOf(value));
      }

      // Internationalisation
      if (I18N_LOCALE.equalsIgnoreCase(key)) {
        configuration.getI18NConfiguration().setLocale(new Locale(value));
      }
      if (I18N_DECIMAL_SEPARATOR.equalsIgnoreCase(key)) {
        configuration.getI18NConfiguration().setDecimalSeparator(value.charAt(0));
      }
      if (I18N_GROUPING_SEPARATOR.equalsIgnoreCase(key)) {
        configuration.getI18NConfiguration().setGroupingSeparator(value.charAt(0));
      }

      // Logging
      if (key.startsWith(LOGGING)) {
        adaptLogging(key, value);
      }

    }

    return configuration;
  }

  /**
   * @param key The key
   * @param value The value
   */
  private void adaptLogging(String key, String value) {

    Preconditions.checkNotNull(key, "'key' must be present");
    Preconditions.checkNotNull(value, "'value' must be present");

    LoggingConfiguration logging = configuration.getLogging();

    if (LOGGING_LEVEL.equalsIgnoreCase(key)) {
      logging.setLevel(Level.valueOf(value));
    }
    if (LOGGING_FILE.equalsIgnoreCase(key)) {
      logging.getFileConfiguration().setCurrentLogFilename(value);
    }
    if (LOGGING_ARCHIVE.equalsIgnoreCase(key)) {
      logging.getFileConfiguration().setArchivedLogFilenamePattern(value);
    }
    if (key.startsWith(LOGGING_PACKAGE_PREFIX)) {
      String packageName = key.substring(LOGGING_PACKAGE_PREFIX.length());
      logging.getLoggers().put(packageName, Level.valueOf(value));
    }
  }

}
