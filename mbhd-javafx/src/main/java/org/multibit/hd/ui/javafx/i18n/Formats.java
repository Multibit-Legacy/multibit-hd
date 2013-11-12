package org.multibit.hd.ui.javafx.i18n;

import org.multibit.hd.ui.javafx.config.Configuration;
import org.multibit.hd.ui.javafx.views.Stages;

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


  public static String formatCurrency(String amount) {

    Configuration configuration = Stages.getConfiguration();
    Locale currentLocale = configuration.getLocale();

    DecimalFormatSymbols dfs = new DecimalFormatSymbols(currentLocale);

    if (configuration.getDecimalSeparator().isPresent()) {
      dfs.setDecimalSeparator(configuration.getDecimalSeparator().get());
    }

    DecimalFormat nf = new DecimalFormat();
    nf.setDecimalFormatSymbols(dfs);
    nf.setMaximumIntegerDigits(8);
    nf.setMinimumIntegerDigits(1);
    nf.setMinimumFractionDigits(4);
    nf.setMaximumFractionDigits(8);
    nf.setMinimumFractionDigits(4);
    nf.setDecimalSeparatorAlwaysShown(true);

    return null;
  }
}
