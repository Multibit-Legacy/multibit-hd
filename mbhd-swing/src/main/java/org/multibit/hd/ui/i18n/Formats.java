package org.multibit.hd.ui.i18n;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.I18NConfiguration;

import java.math.BigDecimal;
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
   * @param amount The Bitcoin amount without a symbolic multiplier
   *
   * @return The left [0] and right [0] components suitable for presentation as a balance with no symbolic decoration
   */
  public static String[] formatRawBitcoinAmountAsSymbolic(BigDecimal amount) {

    Preconditions.checkNotNull(amount, "'amount' must be present");

    I18NConfiguration configuration = Configurations.currentConfiguration.getI18NConfiguration();

    Locale currentLocale = configuration.getLocale();

    DecimalFormatSymbols dfs = configureDecimalFormatSymbols(configuration, currentLocale);
    DecimalFormat format = configureBitcoinDecimalFormat(dfs);

    // Apply formatting to the symbolic amount
    String formattedAmount = format.format(amount.multiply(BitcoinSymbol.current().multiplier()));

    // The Satoshi symbol does not have decimals
    if (BitcoinSymbol.SATOSHI.equals(BitcoinSymbol.current())) {

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
   * @param amount The amount as a plain number (no multipliers)
   *
   * @return The local currency representation with no symbolic decoration
   */
  public static String formatLocalAmount(BigDecimal amount) {

    I18NConfiguration configuration = Configurations.currentConfiguration.getI18NConfiguration();

    Locale currentLocale = configuration.getLocale();

    DecimalFormatSymbols dfs = configureDecimalFormatSymbols(configuration, currentLocale);
    DecimalFormat format = configureLocalDecimalFormat(dfs);

    return format.format(amount);

  }

  /**
   * @param dfs The decimal format symbols
   *
   * @return A decimal format suitable for Bitcoin balance representation
   */
  private static DecimalFormat configureBitcoinDecimalFormat(DecimalFormatSymbols dfs) {

    DecimalFormat format = new DecimalFormat();

    format.setDecimalFormatSymbols(dfs);

    format.setMaximumIntegerDigits(16);
    format.setMinimumIntegerDigits(1);

    format.setMaximumFractionDigits(BitcoinSymbol.current().decimalPlaces());
    format.setMinimumFractionDigits(BitcoinSymbol.current().decimalPlaces());

    format.setDecimalSeparatorAlwaysShown(false);

    return format;
  }

  /**
   * @param dfs The decimal format symbols
   *
   * @return A decimal format suitable for local currency balance representation
   */
  private static DecimalFormat configureLocalDecimalFormat(DecimalFormatSymbols dfs) {

    DecimalFormat format = new DecimalFormat();

    format.setDecimalFormatSymbols(dfs);

    format.setMinimumIntegerDigits(1);
    format.setMaximumFractionDigits(Configurations.currentConfiguration.getI18NConfiguration().getLocalDecimalPlaces());
    format.setMinimumFractionDigits(Configurations.currentConfiguration.getI18NConfiguration().getLocalDecimalPlaces());

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
