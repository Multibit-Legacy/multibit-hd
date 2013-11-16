package org.multibit.hd.ui.javafx.config;

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

  private boolean currencySymbolPrefixed = true;

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

  public void setCurrencySymbolPrefixed(boolean currencySymbolPrefixed) {
    this.currencySymbolPrefixed = currencySymbolPrefixed;
  }

  /**
   * @return True if the currency symbol should be placed before the start of the numerical element (always read as left to right)
   */
  public boolean isCurrencySymbolPrefixed() {
    return currencySymbolPrefixed;
  }

  /**
   * @return A deep copy of this object
   */
  public I18NConfiguration deepCopy() {

    I18NConfiguration i18n = new I18NConfiguration();

    i18n.setCurrencySymbolPrefixed(isCurrencySymbolPrefixed());
    i18n.setLocale(getLocale());
    i18n.setDecimalSeparator(getDecimalSeparator().orNull());
    i18n.setGroupingSeparator(getGroupingSeparator().orNull());

    return i18n;

  }
}
