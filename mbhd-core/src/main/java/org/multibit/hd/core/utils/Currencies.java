package org.multibit.hd.core.utils;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.multibit.hd.core.config.Configurations;

/**
 * <p>Utility to provide the following to low level currency operations:</p>
 * <ul>
 * <li>Bridge methods between Bitcoin and Joda Money</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Currencies {

  public static final CurrencyUnit BTC = CurrencyUnit.of("BTC");

  /**
   * A zero amount for the current local currency
   */
  public static BigMoney ZERO = currentZero();

  /**
   * @return A zero amount in the current local currency (prefer {@link Currencies#ZERO} to maintain consistency with BigDecimal etc)
   */
  public static BigMoney currentZero() {

    CurrencyUnit currentUnit = Configurations.currentConfiguration.getI18NConfiguration().getLocalCurrencyUnit();

    return BigMoney.zero(currentUnit);
  }

  /**
   * @return The current local currency ISO-4217 3 letter code (e.g. "USD")
   */
  public static String currentCode() {

    CurrencyUnit currentUnit = Configurations.currentConfiguration.getI18NConfiguration().getLocalCurrencyUnit();

    return currentUnit.getCode();

  }
}
