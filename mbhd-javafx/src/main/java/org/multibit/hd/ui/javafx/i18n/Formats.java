package org.multibit.hd.ui.javafx.i18n;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.javafx.config.Configuration;
import org.multibit.hd.ui.javafx.views.Stages;

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
   * <p>Provide a split representation for the balance display.</p>
   * <p>For example, 12345.6789 becomes "12,345.67", "89" </p>
   *
   * @param amount The amount
   *
   * @return The left [0] and right [0] components suitable for presentation as a balance
   */
  public static String[] formatBitcoinBalance(BigDecimal amount) {

    Preconditions.checkNotNull(amount, "'amount' must be present");

    Configuration configuration = Stages.getConfiguration();
    Locale currentLocale = configuration.getLocale();

    DecimalFormatSymbols dfs = new DecimalFormatSymbols(currentLocale);

    if (configuration.getDecimalSeparator().isPresent()) {
      dfs.setDecimalSeparator(configuration.getDecimalSeparator().get());
    }
    if (configuration.getGroupingSeparator().isPresent()) {
      dfs.setGroupingSeparator(configuration.getDecimalSeparator().get());
    }

    DecimalFormat format = new DecimalFormat();
    format.setDecimalFormatSymbols(dfs);
    format.setMaximumIntegerDigits(8);
    format.setMinimumIntegerDigits(1);
    format.setMinimumFractionDigits(4);
    format.setMaximumFractionDigits(8);
    format.setMinimumFractionDigits(4);
    format.setDecimalSeparatorAlwaysShown(true);

    String formattedAmount = format.format(amount);

    int decimalIndex = formattedAmount.lastIndexOf(dfs.getDecimalSeparator());

    Preconditions.checkState(decimalIndex > 0, "'decimalIndex' is incorrect");

    return new String[]{
      formattedAmount.substring(0, decimalIndex + 3), // 12,345.67 (significant figures)
      formattedAmount.substring(decimalIndex + 3) // 89 (lesser figures truncated )
    };
  }
}
