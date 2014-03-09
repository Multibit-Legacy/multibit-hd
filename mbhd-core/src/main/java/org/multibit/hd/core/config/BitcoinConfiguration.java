package org.multibit.hd.core.config;

import org.joda.money.CurrencyUnit;
import org.multibit.hd.core.utils.CurrencyUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * <p>Configuration to provide the following to application:</p>
 * <ul>
 * <li>Configuration of Bitcoin related items</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class BitcoinConfiguration {

  /**
   * Start with MICON since it provides pleasing amounts and iconography
   */
  private String bitcoinSymbol = "MICON";

  private Character decimalSeparator;
  private Character groupingSeparator;

  private boolean currencySymbolLeading = true;

  private CurrencyUnit localCurrencyUnit;

  private String localCurrencySymbol;
  private int localDecimalPlaces;

  /**
   * Start with Bitstamp since it provides USD (the global reserve currency)
   */
  private String exchangeKey = "BITSTAMP";

  /**
   * <p>Default constructor uses the default locale</p>
   */
  public BitcoinConfiguration() {
    this(Locale.getDefault());
  }

  /**
   * @param locale The locale to use
   */
  public BitcoinConfiguration(Locale locale) {

    // Get the decimal and grouping separators for the given locale
    DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
    DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();

    decimalSeparator = symbols.getDecimalSeparator();
    groupingSeparator = symbols.getGroupingSeparator();

    // TODO Avoid switching local currency here
    // Fix by splitting language, currency and display configurations
    localDecimalPlaces = decimalFormat.getMinimumFractionDigits();
    localCurrencyUnit = CurrencyUnit.getInstance(locale);
    localCurrencySymbol = CurrencyUtils.symbolFor(locale);

  }

  /**
   * @return A deep copy of this object
   */
  public BitcoinConfiguration deepCopy() {

    BitcoinConfiguration configuration = new BitcoinConfiguration();

    configuration.setBitcoinSymbol(getBitcoinSymbol());
    configuration.setCurrencySymbolLeading(isCurrencySymbolLeading());
    configuration.setDecimalSeparator(getDecimalSeparator());
    configuration.setGroupingSeparator(getGroupingSeparator());
    configuration.setLocalDecimalPlaces(getLocalDecimalPlaces());
    configuration.setLocalCurrencyUnit(getLocalCurrencyUnit());
    configuration.setLocalCurrencySymbol(getLocalCurrencySymbol());

    return configuration;
  }

  /**
   * @return The Bitcoin symbol to use (compatible with BitcoinSymbol)
   */
  public String getBitcoinSymbol() {
    return bitcoinSymbol;
  }

  public void setBitcoinSymbol(String bitcoinSymbol) {
    this.bitcoinSymbol = bitcoinSymbol;
  }

  /**
   * @return The exchange key (e.g. "BITSTAMP") providing access to the current exchange rate provider
   */
  public String getExchangeKey() {
    return exchangeKey;
  }

  public void setExchangeKey(String exchangeKey) {
    this.exchangeKey = exchangeKey;
  }

  /**
   * @return The decimal separator
   */
  public Character getDecimalSeparator() {
    return decimalSeparator;
  }

  public void setDecimalSeparator(Character separator) {
    this.decimalSeparator = separator;
  }

  /**
   * @return The grouping separator
   */
  public Character getGroupingSeparator() {
    return groupingSeparator;
  }

  public void setGroupingSeparator(Character groupingSeparator) {
    this.groupingSeparator = groupingSeparator;
  }


  public void setCurrencySymbolLeading(boolean currencySymbolLeading) {
    this.currencySymbolLeading = currencySymbolLeading;
  }

  /**
   * @return True if the currency symbol should lead the numerical element which is always read left to right
   */
  public boolean isCurrencySymbolLeading() {
    return currencySymbolLeading;
  }

  /**
   * @return The number of decimal places to show for the local currency
   */
  public int getLocalDecimalPlaces() {
    return localDecimalPlaces;
  }

  public void setLocalDecimalPlaces(int localDecimalPlaces) {
    this.localDecimalPlaces = localDecimalPlaces;
  }

  /**
   * @return The local currency unit (e.g. USD, GBP etc for use with local currencies in Joda Money)
   */
  public CurrencyUnit getLocalCurrencyUnit() {
    return localCurrencyUnit;
  }

  /**
   * @param localCurrencyUnit The local currency unit
   */
  public void setLocalCurrencyUnit(CurrencyUnit localCurrencyUnit) {
    this.localCurrencyUnit = localCurrencyUnit;
  }

  /**
   * @return The local currency symbol (e.g. "$", "£" etc)
   */
  public String getLocalCurrencySymbol() {
    return localCurrencySymbol;
  }

  /**
   * @param localCurrencySymbol The local currency symbol
   */
  public void setLocalCurrencySymbol(String localCurrencySymbol) {
    this.localCurrencySymbol = localCurrencySymbol;
  }

}
