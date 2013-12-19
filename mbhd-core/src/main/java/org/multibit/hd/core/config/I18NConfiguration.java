package org.multibit.hd.core.config;

import com.google.common.base.Optional;

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

  private Optional<Character> decimalSeparator = Optional.absent();

  private Optional<Character> groupingSeparator = Optional.absent();

  private Locale locale = Locale.UK;

  private boolean currencySymbolLeading = true;

  /**
   * @return The decimal separator
   */
  public Optional<Character> getDecimalSeparator() {
    return decimalSeparator;
  }

  public void setDecimalSeparator(Character separator) {
    this.decimalSeparator = Optional.fromNullable(separator);
  }

  /**
   * @return The grouping separator
   */
  public Optional<Character> getGroupingSeparator() {
    return groupingSeparator;
  }

  public void setGroupingSeparator(Character groupingSeparator) {
    this.groupingSeparator = Optional.fromNullable(groupingSeparator);
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
   * @return A deep copy of this object
   */
  public I18NConfiguration deepCopy() {

    I18NConfiguration i18n = new I18NConfiguration();

    i18n.setCurrencySymbolLeading(isCurrencySymbolLeading());
    i18n.setLocale(getLocale());
    i18n.setDecimalSeparator(getDecimalSeparator().orNull());
    i18n.setGroupingSeparator(getGroupingSeparator().orNull());

    return i18n;

  }
}
