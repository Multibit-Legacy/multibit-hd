package org.multibit.hd.core.config;

import com.google.common.base.Optional;

/**
 * <p>Enum to provide the following to Configuration API:</p>
 * <ul>
 * <li>All supported configuration properties</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum ConfigurationKey {

  // Application configuration
  APP_VERSION("app.version"),
  APP_CURRENT_WALLET_FILENAME("app.current-wallet-filename"),
  APP_CURRENT_THEME("app.current-theme"),
  APP_CURRENT_APP_DIRECTORY("app.current-app-directory"),
  APP_BITCOIN_URI_HANDLING("app.bitcoin-uri-handling"),
  APP_RESTORE_LAYOUT("app.restore-layout"),
  APP_FRAME_BOUNDS("app.frame-bounds"),
  APP_SIDEBAR_WIDTH("app.sidebar-width"),
  APP_CURRENT_SCREEN("app.current-screen"),

  // Sound configuration
  SOUND_ALERT("sound.alert"),
  SOUND_RECEIVE("sound.receive"),

  // Bitcoin configuration
  BITCOIN_SYMBOL("bitcoin.symbol"),
  BITCOIN_DECIMAL_SEPARATOR("bitcoin.decimal-separator"),
  BITCOIN_GROUPING_SEPARATOR("bitcoin.grouping-separator"),
  BITCOIN_IS_CURRENCY_LEADING("bitcoin.is-leading"),
  BITCOIN_LOCAL_DECIMAL_PLACES("bitcoin.local-decimal-places"),
  BITCOIN_LOCAL_CURRENCY_CODE("bitcoin.local-currency-code"),
  BITCOIN_LOCAL_CURRENCY_SYMBOL("bitcoin.local-currency-symbol"),
  BITCOIN_CURRENT_EXCHANGE("bitcoin.exchange-name"),
  BITCOIN_EXCHANGE_PUBLIC_KEYS("bitcoin.exchange-public-keys"),

  // Language configuration
  LANGUAGE_LOCALE("language.locale"),

  // Logging configuration
  LOGGING_PACKAGE_PREFIX("logging.package."),
  LOGGING_ARCHIVE("logging.archive"),
  LOGGING_FILE("logging.file"),
  LOGGING_LEVEL("logging.level")

  // End of enum
  ;

  private final String key;

  /**
   * @param key The key for the property file
   */
  ConfigurationKey(String key) {
    this.key = key;
  }

  /**
   * @return The property key to use
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key The key for the property file
   *
   * @return The matching configuration key enum entry, absent if not recognised
   */
  public static Optional<ConfigurationKey> fromKey(String key) {

    for (ConfigurationKey configurationKey : values()) {
      if (configurationKey.getKey().equalsIgnoreCase(key)) {
        return Optional.of(configurationKey);
      }
    }

    // Unknown configuration key (could be from a future file)
    return Optional.absent();

  }

}
