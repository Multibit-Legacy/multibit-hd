package org.multibit.hd.core.config;

import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;

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

  private Locale locale = Locale.getDefault();

  private boolean currencySymbolLeading = true;

  private CurrencyUnit localCurrencyUnit;

  // Use 4 as the default as it is common in forex representations
  private int localDecimalPlaces = 4;

  public I18NConfiguration() {

    // Get the decimal and grouping separators for the current locale
    DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
    DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
    decimalSeparator = symbols.getDecimalSeparator();
    groupingSeparator = symbols.getGroupingSeparator();

    try {
      localCurrencyUnit = CurrencyUnit.getInstance(locale);
    } catch (IllegalCurrencyException e) {

    }

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
   * @return The local currency unit
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

    return i18n;

  }
}
