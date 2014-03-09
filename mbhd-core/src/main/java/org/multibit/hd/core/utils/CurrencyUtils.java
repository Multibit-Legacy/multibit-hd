package org.multibit.hd.core.utils;

import com.google.common.collect.Maps;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.multibit.hd.core.config.Configurations;

import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;

/**
 * <p>Utility to provide the following to low level currency operations:</p>
 * <ul>
 * <li>Bridge methods between Bitcoin and Joda Money</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CurrencyUtils {

  public static final CurrencyUnit BTC = CurrencyUnit.of("BTC");

  /**
   * A map of all available currencies for available locales
   */
  private final static SortedMap<Currency, Locale> currencyLocaleMap;

  static {
    currencyLocaleMap = Maps.newTreeMap(new Comparator<Currency>() {

      @Override
      public int compare(Currency c1, Currency c2) {
        return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
      }
    });

    // Iterate over all the available locales
    for (Locale locale : Locale.getAvailableLocales()) {
      try {
        // Store the currency for each locale
        Currency currency = Currency.getInstance(locale);
        currencyLocaleMap.put(currency, locale);
      } catch (IllegalArgumentException e) {
        // Do nothing - locale is likely too general (e.g. "ar")
      }
    }
  }

  /**
   * A zero amount for the current local currency
   */
  public static BigMoney ZERO = currentZero();

  /**
   * @return A zero amount in the current local currency (prefer {@link CurrencyUtils#ZERO} to maintain consistency with BigDecimal etc)
   */
  public static BigMoney currentZero() {

    return BigMoney.zero(currentUnit());
  }

  /**
   * @return The current local currency unit from the configuration
   */
  public static CurrencyUnit currentUnit() {

    return Configurations.currentConfiguration.getBitcoinConfiguration().getLocalCurrencyUnit();

  }

  /**
   * @return The current local currency ISO-4217 3 letter code (e.g. "USD")
   */
  public static String currentCode() {

    return currentUnit().getCode();

  }

  /**
   * @return The current local currency symbol (e.g. "£", "$" etc)
   */
  public static String currentSymbol() {

    String currentCode = currentCode();

    return symbolFor(currentCode);

  }

  /**
   * @param currencyCode The 3 letter ISO 4217 currency code (e.g. "GBP", "USD" etc)
   *
   * @return The currency symbol appropriate for the given currency code in the current locale
   */
  public static String symbolFor(String currencyCode) {

    Currency currency = Currency.getInstance(currencyCode);

    return currency.getSymbol(currencyLocaleMap.get(currency));

  }

  /**
   * @param locale The locale to use
   *
   * @return The currency symbol appropriate for the locale (e.g. "$")
   */
  public static String symbolFor(Locale locale) {

    Currency currency = Currency.getInstance(locale);

    // Not all currencies are known to all JVMs
    if (currencyLocaleMap.containsKey(currency)) {
      return currency.getSymbol(currencyLocaleMap.get(currency));
    } else {
      // Make best guess from JVM
      return currency.getSymbol();
    }

  }

  /**
   * @param locale The locale to use
   *
   * @return The currency code appropriate for the locale (e.g "USD")
   */
  public static String codeFor(Locale locale) {

    Currency currency = Currency.getInstance(locale);

    return currency.getCurrencyCode();
  }
}
