package org.multibit.hd.ui.javafx.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
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

  @JsonProperty
  private BitcoinSymbol bitcoinSymbol = BitcoinSymbol.FONT_AWESOME_ICON;

  @JsonProperty
  private Optional<Character> decimalSeparator = Optional.absent();

  private Optional<Character> groupingSeparator = Optional.absent();

  @JsonProperty
  private Locale locale = Locale.UK;

  @JsonProperty
  private LoggingConfiguration logging = new LoggingConfiguration();

  private boolean currencySymbolPrefixed = true;

  public LoggingConfiguration getLogging() {
    return logging;
  }

  public void setLogging(LoggingConfiguration logging) {
    this.logging = logging;
  }

  public BitcoinSymbol getBitcoinSymbol() {
    return bitcoinSymbol;
  }

  public void setBitcoinSymbol(BitcoinSymbol bitcoinSymbol) {
    this.bitcoinSymbol = bitcoinSymbol;
  }

  public Optional<Character> getDecimalSeparator() {
    return decimalSeparator;
  }

  public void setDecimalSeparator(Character separator) {
    this.decimalSeparator = Optional.fromNullable(separator);
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public Optional<Character> getGroupingSeparator() {
    return groupingSeparator;
  }

  /**
   * @return True if the currency symbol should be placed before the start of the numerical element (always read as left to right)
   */
  public boolean isCurrencySymbolPrefixed() {
    return currencySymbolPrefixed;
  }
}
