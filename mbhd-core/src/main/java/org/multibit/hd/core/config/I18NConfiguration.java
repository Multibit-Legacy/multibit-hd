package org.multibit.hd.core.config;

import com.google.common.base.Preconditions;
import org.joda.money.CurrencyUnit;
import org.multibit.hd.core.utils.CurrencyUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * <p>Configuration to provide the following to logging framework:</p>
 * <ul>
 * <li>Configuration of internationalisation</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
@SuppressWarnings("UnusedDeclaration")
public class I18NConfiguration {

  private Character decimalSeparator;

  private Character groupingSeparator;

  private Locale locale;

  private boolean currencySymbolLeading = true;

  private CurrencyUnit localCurrencyUnit;

  private String localCurrencySymbol;

  // Use 4 as the default as it is common in forex representations
  private int localDecimalPlaces = 4;

  public I18NConfiguration() {

    this(Locale.getDefault());

  }

  /**
   * @param locale The locale on which to set defaults
   */
  public I18NConfiguration(Locale locale) {
    this.locale = locale;

    // Get the decimal and grouping separators for the given locale
    DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
    DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();

    decimalSeparator = symbols.getDecimalSeparator();
    groupingSeparator = symbols.getGroupingSeparator();

    localCurrencyUnit = CurrencyUnit.getInstance(locale);
    localCurrencySymbol = CurrencyUtils.symbolFor(locale);

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

  /**
   * @return The locale
   */
  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
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

  /**
   * @return A deep copy of this object
   */
  public I18NConfiguration deepCopy() {

    I18NConfiguration i18n = new I18NConfiguration();

    i18n.setCurrencySymbolLeading(isCurrencySymbolLeading());
    i18n.setLocale(getLocale());
    i18n.setDecimalSeparator(getDecimalSeparator());
    i18n.setGroupingSeparator(getGroupingSeparator());
    i18n.setLocalDecimalPlaces(getLocalDecimalPlaces());
    i18n.setLocalCurrencyUnit(getLocalCurrencyUnit());
    i18n.setLocalCurrencySymbol(getLocalCurrencySymbol());

    return i18n;

  }

  /**
   * @param value The string representation of the locale (e.g. "en_GB" etc)
   */
  public void setLocale(String value) {

    Preconditions.checkNotNull(value, "'value' must be present");

    String[] parameters = value.split("_");

    Preconditions.checkState(parameters.length > 0, "'value' must not be empty");

    final Locale newLocale;

    switch (parameters.length) {
      case 1:
        newLocale = new Locale(parameters[0]);
        break;
      case 2:
        newLocale = new Locale(parameters[0], parameters[1]);
        break;
      case 3:
        newLocale = new Locale(parameters[0], parameters[1], parameters[2]);
        break;
      default:
        throw new IllegalArgumentException("Unknown locale descriptor: " + value);
    }

    setLocale(newLocale);

  }
}
