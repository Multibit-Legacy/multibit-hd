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

  @JsonProperty
  private Locale locale;

  @JsonProperty
  private LoggingConfiguration logging = new LoggingConfiguration();

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
}
