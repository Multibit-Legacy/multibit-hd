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
public class ConfigurationReadAdapter {

  private final Properties properties;
  private final Configuration configuration = new Configuration();

  public ConfigurationReadAdapter(Properties properties) {
    this.properties = properties;
  }

  /**
   * @return A new Configuration based on the properties
   */
  public Configuration adapt() {

    for (Map.Entry<Object, Object> entry : properties.entrySet()) {

      String key = (String) entry.getKey();
      String value = (String) entry.getValue();

      Preconditions.checkNotNull(key, "'key' must be present");
      Preconditions.checkNotNull(value, "'value' must be present");

      // Application

      // Bitcoin
      adaptBitcoin(key, value);

      // Internationalisation
      adaptI18N(key, value);

      // Logging
      if (key.startsWith(LOGGING)) {
        adaptLogging(key, value);
      }

    }

    return configuration;
  }

  private void adaptBitcoin(String key, String value) {

    if (BITCOIN_SYMBOL.equalsIgnoreCase(key)) {
      configuration.getBitcoinConfiguration().setBitcoinSymbol(BitcoinSymbol.valueOf(value));
    }

  }

  private void adaptI18N(String key, String value) {

    if (I18N_LOCALE.equalsIgnoreCase(key)) {
      configuration.getI18NConfiguration().setLocale(new Locale(value));
    }
    if (I18N_DECIMAL_SEPARATOR.equalsIgnoreCase(key)) {
      configuration.getI18NConfiguration().setDecimalSeparator(value.charAt(0));
    }
    if (I18N_GROUPING_SEPARATOR.equalsIgnoreCase(key)) {
      configuration.getI18NConfiguration().setGroupingSeparator(value.charAt(0));
    }
    if (I18N_IS_CURRENCY_PREFIXED.equalsIgnoreCase(key)) {
      configuration.getI18NConfiguration().setCurrencySymbolPrefixed(Boolean.valueOf(value));
    }

  }

  /**
   * @param key The key
   * @param value The value
   */
  private void adaptLogging(String key, String value) {

    LoggingConfiguration logging = configuration.getLoggingConfiguration();

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
