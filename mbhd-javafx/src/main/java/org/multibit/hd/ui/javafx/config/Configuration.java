package org.multibit.hd.ui.javafx.config;

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

  private LoggingConfiguration loggingConfiguration = new LoggingConfiguration();

  private I18NConfiguration i18nConfiguration = new I18NConfiguration();

  private ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();

  private BitcoinConfiguration bitcoinConfiguration = new BitcoinConfiguration();

  private String propertiesVersion = "0.0.1";

  /**
   * <p>Shortcut to the i18n configuration</p>
   *
   * @return The current locale
   */
  public Locale getLocale() {
    return getI18NConfiguration().getLocale();
  }

  /**
   * @return The logging configuration
   */
  public LoggingConfiguration getLoggingConfiguration() {
    return loggingConfiguration;
  }

  public void setLoggingConfiguration(LoggingConfiguration loggingConfiguration) {
    this.loggingConfiguration = loggingConfiguration;
  }

  /**
   * @return The Bitcoin configuration
   */
  public BitcoinConfiguration getBitcoinConfiguration() {
    return bitcoinConfiguration;
  }

  public void setBitcoinConfiguration(BitcoinConfiguration bitcoinConfiguration) {
    this.bitcoinConfiguration = bitcoinConfiguration;
  }

  /**
   * @return The internationalisation configuration
   */
  public I18NConfiguration getI18NConfiguration() {
    return i18nConfiguration;
  }

  public void setI18NConfiguration(I18NConfiguration i18nConfiguration) {
    this.i18nConfiguration = i18nConfiguration;
  }

  /**
   * @return The application configuration
   */
  public ApplicationConfiguration getApplicationConfiguration() {
    return applicationConfiguration;
  }

  public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
    this.applicationConfiguration = applicationConfiguration;
  }

  /**
   * @return The properties file version
   */
  public String getPropertiesVersion() {
    return propertiesVersion;
  }

  public void setPropertiesVersion(String propertiesVersion) {
    this.propertiesVersion = propertiesVersion;
  }
}
