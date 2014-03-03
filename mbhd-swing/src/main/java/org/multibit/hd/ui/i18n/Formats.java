package org.multibit.hd.ui.i18n;

import com.google.common.base.Preconditions;
import org.joda.money.BigMoney;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.I18NConfiguration;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.core.utils.Satoshis;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * <p>Utility to provide the following to controllers:</p>
 * <ul>
 * <li>Access to i18n formats for date/time and decimal data</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Formats {

  /**
   * <p>Provide a split representation for the Bitcoin balance display.</p>
   * <p>For example, 12345.6789 becomes "12,345.67", "89" </p>
   * <p>The amount will be adjusted by the symbolic multiplier from the current confiuration</p>
   *
   * @param satoshis             The amount in satoshis
   * @param i18nConfiguration    The I18NConfiguration to use as the basis for presentation
   * @param bitcoinConfiguration The Bitcoin configuration to use as the basis for the symbol
   *
   * @return The left [0] and right [1] components suitable for presentation as a balance with no symbolic decoration
   */
  public static String[] formatSatoshisAsSymbolic(
    BigInteger satoshis,
    I18NConfiguration i18nConfiguration,
    BitcoinConfiguration bitcoinConfiguration
  ) {

    Preconditions.checkNotNull(satoshis, "'satoshis' must be present");
    Preconditions.checkNotNull(i18nConfiguration, "'i18nConfiguration' must be present");

    Locale currentLocale = i18nConfiguration.getLocale();
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.of(bitcoinConfiguration.getBitcoinSymbol());

    DecimalFormatSymbols dfs = configureDecimalFormatSymbols(i18nConfiguration, currentLocale);
    DecimalFormat localFormat = configureBitcoinDecimalFormat(dfs, bitcoinSymbol);

    // Apply formatting to the symbolic amount
    String formattedAmount = localFormat.format(Satoshis.toSymbolicAmount(satoshis));

    // The Satoshi symbol does not have decimals
    if (BitcoinSymbol.SATOSHI.equals(bitcoinSymbol)) {

      return new String[]{
        formattedAmount,
        ""
      };

    }

    // All other representations require a decimal

    int decimalIndex = formattedAmount.lastIndexOf(dfs.getDecimalSeparator());

    if (decimalIndex == -1) {
      formattedAmount += dfs.getDecimalSeparator() + "00";
      decimalIndex = formattedAmount.lastIndexOf(dfs.getDecimalSeparator());
    }

    return new String[]{
      formattedAmount.substring(0, decimalIndex + 3), // 12,345.67 (significant figures)
      formattedAmount.substring(decimalIndex + 3) // 89 (lesser figures truncated )
    };

  }

  /**
   * <p>Provide a simple representation for a local currency amount.</p>
   *
   * @param amount            The amount as a plain number (no multipliers)
   * @param i18nConfiguration The I18NConfiguration to use as the basis for presentation
   *
   * @return The local currency representation with no symbolic decoration
   */
  public static String formatLocalAmount(BigMoney amount, I18NConfiguration i18nConfiguration) {

    if (amount == null) {
      return "";
    }

    Locale currentLocale = i18nConfiguration.getLocale();

    DecimalFormatSymbols dfs = configureDecimalFormatSymbols(i18nConfiguration, currentLocale);
    DecimalFormat localFormat = configureLocalDecimalFormat(dfs, i18nConfiguration);

    return localFormat.format(amount.getAmount());

  }

  /**
   * @param dfs The decimal format symbols
   *
   * @return A decimal format suitable for Bitcoin balance representation
   */
  private static DecimalFormat configureBitcoinDecimalFormat(DecimalFormatSymbols dfs, BitcoinSymbol bitcoinSymbol) {

    DecimalFormat format = new DecimalFormat();

    format.setDecimalFormatSymbols(dfs);

    format.setMaximumIntegerDigits(16);
    format.setMinimumIntegerDigits(1);

    format.setMaximumFractionDigits(bitcoinSymbol.decimalPlaces());
    format.setMinimumFractionDigits(bitcoinSymbol.decimalPlaces());

    format.setDecimalSeparatorAlwaysShown(false);

    return format;
  }

  /**
   * @param dfs               The decimal format symbols
   * @param i18nConfiguration The I18NConfiguration to use as the basis for presentation
   *
   * @return A decimal format suitable for local currency balance representation
   */
  private static DecimalFormat configureLocalDecimalFormat(DecimalFormatSymbols dfs, I18NConfiguration i18nConfiguration) {

    DecimalFormat format = new DecimalFormat();

    format.setDecimalFormatSymbols(dfs);

    format.setMinimumIntegerDigits(1);
    format.setMaximumFractionDigits(i18nConfiguration.getLocalDecimalPlaces());
    format.setMinimumFractionDigits(i18nConfiguration.getLocalDecimalPlaces());

    format.setDecimalSeparatorAlwaysShown(true);

    return format;
  }

  /**
   * @param configuration The internationalisation configuration
   * @param currentLocale The current locale
   *
   * @return The decimal format symbols to use based on the configuration and locale
   */
  private static DecimalFormatSymbols configureDecimalFormatSymbols(I18NConfiguration configuration, Locale currentLocale) {

    DecimalFormatSymbols dfs = new DecimalFormatSymbols(currentLocale);

    dfs.setDecimalSeparator(configuration.getDecimalSeparator());
    dfs.setGroupingSeparator(configuration.getGroupingSeparator());

    return dfs;

  }
}
