package org.multibit.hd.core.config;

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

  private LanguageConfiguration languageConfiguration = new LanguageConfiguration();

  private ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();

  private BitcoinConfiguration bitcoinConfiguration = new BitcoinConfiguration();

  private String propertiesVersion = "0.0.1";

  /**
   * <p>Shortcut to the language configuration locale</p>
   *
   * @return The current locale
   */
  public Locale getLocale() {
    return getLanguageConfiguration().getLocale();
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
   * @return The language configuration
   */
  public LanguageConfiguration getLanguageConfiguration() {
    return languageConfiguration;
  }

  public void setLanguageConfiguration(LanguageConfiguration languageConfiguration) {
    this.languageConfiguration = languageConfiguration;
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

  /**
   * @return A deep copy of this configuration
   */
  public Configuration deepCopy() {

    LanguageConfiguration language = getLanguageConfiguration().deepCopy();
    ApplicationConfiguration app = getApplicationConfiguration().deepCopy();
    BitcoinConfiguration bitcoin = getBitcoinConfiguration().deepCopy();
    LoggingConfiguration logging = getLoggingConfiguration().deepCopy();

    Configuration configuration = new Configuration();

    // Bind the copies
    configuration.setApplicationConfiguration(app);
    configuration.setLanguageConfiguration(language);
    configuration.setBitcoinConfiguration(bitcoin);
    configuration.setLoggingConfiguration(logging);

    // Copy top level properties
    configuration.setPropertiesVersion(getPropertiesVersion());

    return configuration;
  }
}
