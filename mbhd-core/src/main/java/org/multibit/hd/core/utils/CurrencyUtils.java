package org.multibit.hd.core.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.multibit.hd.core.config.Configurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>Utility to provide the following to low level currency operations:</p>
 * <ul>
 * <li>Bridge methods between Bitcoin and JVM ISO 4217 currencies</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class CurrencyUtils {

  private static final Logger log = LoggerFactory.getLogger(CurrencyUtils.class);

  /**
   * A map of all available currencies for available locales
   */
  private final static SortedMap<Locale, Currency> localeCurrencyMap;

  /**
   * A map of all common currency names and their appropriate ISO candidates
   */
  private final static Map<String, List<String>> isoCandidateMap;

  static {
    localeCurrencyMap = Maps.newTreeMap(new Comparator<Locale>() {

      @Override
      public int compare(Locale l1, Locale l2) {
        return l1.toString().compareTo(l2.toString());
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
    for (Map.Entry<Locale, Currency> entry : localeCurrencyMap.entrySet()) {
      String isoCode = entry.getValue().getCurrencyCode();
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
        // Key on locale to ensure uniqueness
        Currency currency = Currency.getInstance(locale);
        localeCurrencyMap.put(locale, currency);
      } catch (IllegalArgumentException e) {
        // Do nothing - locale is likely too general (e.g. "ar")
      }
    }
  }

  /**
   * @return The current local currency symbol (e.g. "£", "$" etc)
   */
  public static String currentSymbol() {

    String currentCode = Configurations.currentConfiguration.getLocalCurrency().getCurrencyCode();

    return symbolFor(currentCode);

  }

  /**
   * @param isoCode The 3 letter ISO 4217 currency code (e.g. "GBP", "USD" etc)
   *
   * @return The currency symbol appropriate for the given currency code in the current locale
   */
  public static String symbolFor(String isoCode) {

    for (Map.Entry<Locale, Currency> entry : localeCurrencyMap.entrySet()) {
      if (entry.getValue().getCurrencyCode().equalsIgnoreCase(isoCode)) {
        String symbol = entry.getValue().getSymbol(entry.getKey());
        if (!isoCode.equals(symbol)) {
          return symbol;
        }
      }
    }

    // Must have failed to find it in the JVM so attempt to find it in the ISO 4217 spec

    switch (isoCode) {
      case "ALL":
        return "\u004c\u0065\u006b";
      case "AFN":
        return "\u0060b";
      case "ARS":
        return "\u0024";
      case "AWG":
        return "\u0192";
      case "AUD":
        return "\u0024";
      case "AZN":
        return "\u043c\u0430\u043d";
      case "BSD":
        return "\u0024";
      case "BBD":
        return "\u0024";
      case "BYR":
        return "\u002e";
      case "BZD":
        return "\u0042\u005a\u0024";
      case "BMD":
        return "\u0024";
      case "BOB":
        return "\u0024\u0062";
      case "BAM":
        return "\u004b\u004d";
      case "BWP":
        return "\u0050";
      case "BGN":
        return "\u043b\u0432";
      case "BRL":
        return "\u0052\u0024";
      case "BND":
        return "\u0024";
      case "KHR":
        return "\u17db";
      case "CAD":
        return "\u0024";
      case "KYD":
        return "\u0024";
      case "CLP":
        return "\u0024";
      case "CNY":
        return "\u00a5";
      case "COP":
        return "\u0024";
      case "CRC":
        return "\u20a1";
      case "HRK":
        return "\u006b\u006e";
      case "CUP":
        return "\u20b1";
      case "CZK":
        return "\u004b\u010d";
      case "DKK":
        return "\u006b\u0072";
      case "DOP":
        return "\u0052\u0044\u0024";
      case "XCD":
        return "\u0024";
      case "EGP":
        return "\u00a3";
      case "SVC":
        return "\u0024";
      case "EEK":
        return "\u006b\u0072";
      case "EUR":
        return "\u20ac";
      case "FKP":
        return "\u00a3";
      case "FJD":
        return "\u0024";
      case "GHC":
        return "\u00a2";
      case "GIP":
        return "\u00a3";
      case "GTQ":
        return "\u0051";
      case "GGP":
        return "\u00a3";
      case "GYD":
        return "\u0024";
      case "HNL":
        return "\u004c";
      case "HKD":
        return "\u0024";
      case "HUF":
        return "\u0046\u0074";
      case "ISK":
        return "\u006b\u0072";
      case "INR":
        return "\u20B9";
      case "IDR":
        return "\u0052\u0070";
      case "IRR":
        return "\ufdfc";
      case "IMP":
        return "\u00a3";
      case "ILS":
        return "\u20aa";
      case "JMD":
        return "\u004a\u0024";
      case "JPY":
        return "\u00a5";
      case "JEP":
        return "\u00a3";
      case "KZT":
        return "\u043b\u0432";
      case "KPW":
        return "\u20a9";
      case "KRW":
        return "\u20a9";
      case "KGS":
        return "\u043b\u0432";
      case "LAK":
        return "\u20ad";
      case "LVL":
        return "\u004c\u0073";
      case "LBP":
        return "\u00a3";
      case "LRD":
        return "\u0024";
      case "LTL":
        return "\u004c\u0074";
      case "MKD":
        return "\u0434\u0435\u043d";
      case "MYR":
        return "\u0052\u004d";
      case "MUR":
        return "\u20a8";
      case "MXN":
        return "\u0024";
      case "MNT":
        return "\u20ae";
      case "MZN":
        return "\u004d\u0054";
      case "NAD":
        return "\u0024";
      case "NPR":
        return "\u20a8";
      case "ANG":
        return "\u0192";
      case "NZD":
        return "\u0024";
      case "NIO":
        return "\u0043\u0024";
      case "NGN":
        return "\u20a6";
      case "NOK":
        return "\u006b\u0072";
      case "OMR":
        return "\ufdfc";
      case "PKR":
        return "\u20a8";
      case "PAB":
        return "\u0042\u002f\u002e";
      case "PYG":
        return "\u0047\u0073";
      case "PEN":
        return "\u0053\u002f\u002e";
      case "PHP":
        return "\u20b1";
      case "PLN":
        return "\u007a\u0142";
      case "QAR":
        return "\ufdfc";
      case "RON":
        return "\u006c\u0065\u0069";
      case "RUB":
        return "\u0440\u0443\u0431";
      case "SHP":
        return "\u00a3";
      case "SAR":
        return "\ufdfc";
      case "RSD":
        return "\u0414\u0438\u043d\u002e";
      case "SCR":
        return "\u20a8";
      case "SGD":
        return "\u0024";
      case "SBD":
        return "\u0024";
      case "SOS":
        return "\u0053";
      case "ZAR":
        return "\u0052";
      case "LKR":
        return "\u20a8";
      case "SEK":
        return "\u006b\u0072";
      case "SRD":
        return "\u0024";
      case "SYP":
        return "\u00a3";
      case "TWD":
        return "\u0024";
      case "THB":
        return "\u0e3f";
      case "TTD":
        return "\u0054\u0054\u0024";
      case "TRY":
        return "\u20ba";
      case "TRL":
        return "\u20a4";
      case "TVD":
        return "\u0024";
      case "UAH":
        return "\u20b4";
      case "GBP":
        return "\u00a3";
      case "USD":
        return "\u0024";
      case "UYU":
        return "\u0024\u0055";
      case "UZS":
        return "\u043b\u0432";
      case "VEF":
        return "\u0042\u0073";
      case "VND":
        return "\u20ab";
      case "YER":
        return "\ufdfc";
      case "ZWD":
        return "\u005a\u0024";
      default:
        return isoCode;
    }

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

    Preconditions.checkNotNull(currency, "'currency' must be present");

    log.debug("Searching for ISO candidate for '{}'", currency);

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
