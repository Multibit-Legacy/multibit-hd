package org.multibit.hd.ui.javafx.config;

import org.multibit.hd.ui.javafx.i18n.BitcoinSymbol;

import java.util.Locale;

/**
 * <p>Value object to provide the following to controllers:</p>
 * <ul>
 * <li>Access to runtime configuration</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Configuration {

  private BitcoinSymbol bitcoinSymbol = BitcoinSymbol.ICON;

  private LoggingConfiguration logging = new LoggingConfiguration();

  private I18NConfiguration i18nConfiguration = new I18NConfiguration();

  /**
   * @return The logging configuration
   */
  public LoggingConfiguration getLogging() {
    return logging;
  }

  public void setLogging(LoggingConfiguration logging) {
    this.logging = logging;
  }

  public I18NConfiguration getI18NConfiguration() {
    return i18nConfiguration;
  }

  public void setI18NConfiguration(I18NConfiguration i18nConfiguration) {
    this.i18nConfiguration = i18nConfiguration;
  }

  /**
   * @return The Bitcoin symbol to use
   */
  public BitcoinSymbol getBitcoinSymbol() {
    return bitcoinSymbol;
  }

  public void setBitcoinSymbol(BitcoinSymbol bitcoinSymbol) {
    this.bitcoinSymbol = bitcoinSymbol;
  }

  /**
   * <p>Shortcut to the i18n configuration</p>
   *
   * @return The current locale
   */
  public Locale getLocale() {
    return getI18NConfiguration().getLocale();
  }
}
