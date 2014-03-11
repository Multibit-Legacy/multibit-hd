package org.multibit.hd.core.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.multibit.hd.core.config.Configurations;

import java.util.*;

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

  /**
   * A map of all common currency names and their appropriate ISO candidates
   */
  private final static Map<String, List<String>> isoCandidateMap;

  static {
    currencyLocaleMap = Maps.newTreeMap(new Comparator<Currency>() {

      @Override
      public int compare(Currency c1, Currency c2) {
        return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
      }
    });
    populateCurrencyLocaleMap();

    isoCandidateMap = Maps.newLinkedHashMap();

    populateIsoCandidateMap();
  }

  /**
   * Populate ISO candidate currencies and replace legacy entries
   */
  private static void populateIsoCandidateMap() {

    // Generate the ISO code from known entries (including legacy)
    for (Map.Entry<Currency, Locale> entry : currencyLocaleMap.entrySet()) {
      String isoCode = entry.getKey().getCurrencyCode();
      isoCandidateMap.put(isoCode, Lists.newArrayList(isoCode));
    }

    // Supersede legacy entries
    isoCandidateMap.remove("RUR");
    isoCandidateMap.put("RUB", Lists.newArrayList("RUR"));

    // Add non-standard codes and their ISO candidates (usually start with "X" for private codes)
    isoCandidateMap.put("XBT", Lists.newArrayList("BTC"));

  }

  /**
   * Populate currencies over all available locales (this ensure we can get the correct
   * symbol for the currency using its native locale)
   */
  private static void populateCurrencyLocaleMap() {

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

  /**
   * <p>Provides the ISO 4217 candidate code for the given input</p>
   * <ul>
   * <li>"BTC" is not ISO and its ISO candidate is "XBT"</li>
   * <li>"RUR" is legacy ISO and its ISO replacement is "RUB"</li>
   * <li>"USD" is ISO so no replacement occurs</li>
   * </ul>
   *
   * @param currency The mixed currency code
   *
   * @return The ISO code (or recognised candidate)
   */
  public static String isoCandidateFor(String currency) {

    if (isoCandidateMap.containsKey(currency)) {
      // The currency is ISO so no searching is required
      return currency;
    }

    // Iterate over all currencies looking for a match
    for (Map.Entry<String, List<String>> entry : isoCandidateMap.entrySet()) {

      // Search the non-standard codes
      List<String> nonStandard = entry.getValue();

      for (String nonIsoCode : nonStandard) {

        if (nonIsoCode.equalsIgnoreCase(currency)) {

          // Found a match so return the ISO code
          return entry.getKey();
        }

      }

    }

    // Must have failed to find a match here
    return currency;

  }

}
