package org.multibit.hd.core.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;

import java.util.Currency;
import java.util.Locale;
import java.util.Map;

/**
 * <p>Value object to provide the following to controllers:</p>
 * <ul>
 * <li>Access to runtime configuration</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {

  private String configurationVersion = "1";

  private LanguageConfiguration language = new LanguageConfiguration();

  private ApplicationConfiguration application = new ApplicationConfiguration();

  private BitcoinConfiguration bitcoin = new BitcoinConfiguration();

  private SoundConfiguration sound = new SoundConfiguration();

  private WalletConfiguration wallet = new WalletConfiguration();

  @JsonIgnore
  private LoggingConfiguration logging = new LoggingConfiguration();

  /**
   * True if the user has accepted the licence agreement
   */
  private boolean licenceAccepted = false;

  /**
   * True if TOR should be used for Bitcoin connections
   */
  private boolean tor = false;

  /**
   * Any unknown objects in the configuration go here (preserve order of insertion)
   */
  private Map<String, Object> other = Maps.newLinkedHashMap();

  /**
   * @return The map of any unknown objects in the configuration
   */
  @JsonAnyGetter
  public Map<String, Object> any() {
    return other;
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    other.put(name, value);
  }

  /**
   * <p>Shortcut to the language configuration locale</p>
   *
   * @return The current locale
   */
  @JsonIgnore
  public Locale getLocale() {
    return getLanguage().getLocale();
  }

  /**
   * <p>Shortcut to the application version</p>
   *
   * @return The current version
   */
  @JsonIgnore
  public String getVersion() {
    return getApplication().getVersion();
  }

  /**
   * <p>Shortcut to the local currency</p>
   *
   * @return The current local currency
   */
  @JsonIgnore
  public Currency getLocalCurrency() {
    return Currency.getInstance(getBitcoin().getLocalCurrencyCode());
  }

  /**
   * @return The logging configuration
   */
  public LoggingConfiguration getLogging() {
    return logging;
  }

  public void setLogging(LoggingConfiguration logging) {
    this.logging = logging;
  }

  /**
   * @return The Bitcoin configuration
   */
  public BitcoinConfiguration getBitcoin() {
    return bitcoin;
  }

  public void setBitcoin(BitcoinConfiguration bitcoin) {
    this.bitcoin = bitcoin;
  }

  /**
   * @return The language configuration
   */
  public LanguageConfiguration getLanguage() {
    return language;
  }

  public void setLanguage(LanguageConfiguration language) {
    this.language = language;
  }

  /**
   * @return The application configuration
   */
  public ApplicationConfiguration getApplication() {
    return application;
  }

  public void setApplication(ApplicationConfiguration application) {
    this.application = application;
  }

  /**
   * @return The sound configuration
   */
  public SoundConfiguration getSound() {
    return sound;
  }

  public void setSound(SoundConfiguration sound) {
    this.sound = sound;
  }

  /**
   * @return The wallet configuration
   */
  public WalletConfiguration getWallet() {
    return wallet;
  }

  public void setWallet(WalletConfiguration wallet) {
    this.wallet = wallet;
  }

  /**
   * @return The properties file version
   */
  public String getConfigurationVersion() {
    return configurationVersion;
  }

  public void setConfigurationVersion(String configurationVersion) {
    this.configurationVersion = configurationVersion;
  }

  /**
   * @return True if the user has accepted the licence agreement
   */
  public boolean isLicenceAccepted() {
    return licenceAccepted;
  }

  public void setLicenceAccepted(boolean licenceAccepted) {
    this.licenceAccepted = licenceAccepted;
  }

  //////////////// Labs properties are added to the top configuration before being allocated to a sub-section ///////////////////////

  /**
   * TODO Move out of "labs"
   *
   * @return True if TOR should be used for communications
   */
  public boolean isTor() {
    return tor;
  }

  public void setTor(boolean tor) {
    this.tor = tor;
  }

  /**
   * @return A deep copy of this configuration
   */
  public Configuration deepCopy() {

    LanguageConfiguration language = getLanguage().deepCopy();
    ApplicationConfiguration app = getApplication().deepCopy();
    SoundConfiguration sound = getSound().deepCopy();
    WalletConfiguration wallet = getWallet().deepCopy();
    BitcoinConfiguration bitcoin = getBitcoin().deepCopy();
    LoggingConfiguration logging = getLogging().deepCopy();

    Configuration configuration = new Configuration();

    // Bind the copies
    configuration.setApplication(app);
    configuration.setSound(sound);
    configuration.setWallet(wallet);
    configuration.setLanguage(language);
    configuration.setBitcoin(bitcoin);
    configuration.setLogging(logging);

    // Copy top level properties
    configuration.setConfigurationVersion(getConfigurationVersion());
    configuration.setLicenceAccepted(isLicenceAccepted());

    // Labs properties
    configuration.setTor(isTor());

    return configuration;
  }

}
