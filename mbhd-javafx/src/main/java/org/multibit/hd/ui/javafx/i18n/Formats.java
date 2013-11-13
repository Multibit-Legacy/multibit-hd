package org.multibit.hd.ui.javafx.i18n;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.javafx.config.I18NConfiguration;
import org.multibit.hd.ui.javafx.views.Stages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger log = LoggerFactory.getLogger(Formats.class);

  /**
   * <p>Provide a split representation for the balance display.</p>
   * <p>For example, 12345.6789 becomes "12,345.67", "89" </p>
   *
   * @param amount The amount as a plain number (no multipliers)
   *
   * @return The left [0] and right [0] components suitable for presentation as a balance
   */
  public static String[] formatBitcoinBalance(BigDecimal amount) {

    Preconditions.checkNotNull(amount, "'amount' must be present");

    I18NConfiguration configuration = Stages.getConfiguration().getI18NConfiguration();
    BitcoinSymbol symbol = Stages.getConfiguration().getBitcoinSymbol();

    BigDecimal symbolicAmount = amount.multiply(symbol.multiplier());

    Locale currentLocale = configuration.getLocale();

    DecimalFormatSymbols dfs = configureDecimalFormatSymbols(configuration, currentLocale);
    DecimalFormat format = configureBitcoinDecimalFormat(dfs);

    log.debug("Formatting symbolic amount: '{}' '{}'", symbolicAmount.toPlainString(), symbol);

    String formattedAmount = format.format(symbolicAmount);

    log.debug("Formatted '{}'", formattedAmount);

    int decimalIndex = formattedAmount.lastIndexOf(dfs.getDecimalSeparator());

    if (BitcoinSymbol.SATOSHI.equals(symbol)) {

      return new String[]{
        formattedAmount,
        ""
      };

    }

    // All other representations have a decimal

    Preconditions.checkState(decimalIndex > 0, "Require a leading zero for this representation");

    return new String[]{
      formattedAmount.substring(0, decimalIndex + 3), // 12,345.67 (significant figures)
      formattedAmount.substring(decimalIndex + 3) // 89 (lesser figures truncated )
    };

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
    format.setMaximumFractionDigits(8);
    format.setMinimumFractionDigits(0);
    format.setDecimalSeparatorAlwaysShown(false);

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

    if (configuration.getDecimalSeparator().isPresent()) {
      dfs.setDecimalSeparator(configuration.getDecimalSeparator().get());
    }
    if (configuration.getGroupingSeparator().isPresent()) {
      dfs.setGroupingSeparator(configuration.getGroupingSeparator().get());
    }

    return dfs;

  }
}
