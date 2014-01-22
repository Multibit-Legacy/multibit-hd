package org.multibit.hd.ui.views.components.text_fields;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.views.components.DocumentMaxLengthFilter;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

/**
 * <p>Text field to provide the following to UI:</p>
 * <ul>
 * <li>Accepts decimal and integer values</li>
 * <li>Places upper and lower range limits (min/max)</li>
 * <li>Limits number of decimal places</li>
 * <li>Handles configured grouping and decimal characters for different locales</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class FormattedDecimalField extends JFormattedTextField {

  /**
   * @param min           The minimum value
   * @param max           The maximum value
   * @param decimalPlaces The number of decimal places to show (padding as required)
   * @param maxEditLength The maximum edit length
   */
  public FormattedDecimalField(double min, double max, int decimalPlaces, int maxEditLength) {

    super();

    Preconditions.checkNotNull(min, "'min' must be present");
    Preconditions.checkNotNull(max, "'max' must be present");
    Preconditions.checkState(min < max, "'min' must be less than 'max'");

    Preconditions.checkState(decimalPlaces >= 0 && decimalPlaces < 15, "'decimalPlaces' must be in range [0,15)");

    setInputVerifier(new ThemedDecimalInputVerifier(min, max));

    setBackground(Themes.currentTheme.dataEntryBackground());

    // Set the edit/display patterns (not localised)
    String displayPattern = "#,##0.000000000000000";
    String editPattern = "#.###############";

    // Identify the location of the decimal in the template before locale adjustments
    int decimalDisplayIndex = displayPattern.indexOf('.');
    int decimalEditIndex = editPattern.indexOf('.');

    // Adjust patterns to accommodate the required decimal places
    if (decimalPlaces > 0) {
      displayPattern = displayPattern.substring(0, decimalDisplayIndex + decimalPlaces + 1);
      editPattern = editPattern.substring(0, decimalEditIndex + decimalPlaces + 1);
    } else {
      displayPattern = displayPattern.substring(0, decimalDisplayIndex);
      editPattern = editPattern.substring(0, decimalEditIndex);
    }
    // Adjust edit/display formats to the current configuration
    char groupingSeparator = Configurations.currentConfiguration.getI18NConfiguration().getGroupingSeparator();
    char decimalSeparator = Configurations.currentConfiguration.getI18NConfiguration().getDecimalSeparator();

    // Use locale decimal formatting then override with current configuration
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator(decimalSeparator);
    symbols.setGroupingSeparator(groupingSeparator);
    symbols.setMonetaryDecimalSeparator(decimalSeparator);

    // Create a decimal format using the configured symbols for edit/display
    DecimalFormat displayDecimalFormat = new DecimalFormat(displayPattern, symbols);
    DecimalFormat editDecimalFormat = new DecimalFormat(editPattern, symbols);

    // Build number formatters
    NumberFormatter defaultFormatter = new NumberFormatter();
    defaultFormatter.setValueClass(Double.class);

    NumberFormatter displayFormatter = getNumberFormatter(displayDecimalFormat, maxEditLength);
    NumberFormatter editFormatter = getNumberFormatter(editDecimalFormat, maxEditLength);

    setFormatterFactory(new DefaultFormatterFactory(
      defaultFormatter,
      displayFormatter,
      editFormatter
    ));

  }

  /**
   * @param decimalFormat The decimal format appropriate for this locale
   *
   * @return A number formatter that is locale-aware and configured for doubles
   */
  private NumberFormatter getNumberFormatter(final DecimalFormat decimalFormat, final int maxEditLength) {

    // Create the number formatter with local-sensitive adjustments
    NumberFormatter displayFormatter = new NumberFormatter(decimalFormat) {

      // The max input length for the given symbol
      DocumentFilter documentFilter = new DocumentMaxLengthFilter(maxEditLength);

      @Override
      public Object stringToValue(String text) throws ParseException {
        // RU locale (and others) requires a non-breaking space for a grouping separator
        text = text.replace(' ', '\u00a0');
        return super.stringToValue(text);
      }

      @Override
      protected DocumentFilter getDocumentFilter() {
        return documentFilter;
      }

    };
    displayFormatter.setValueClass(Double.class);
    return displayFormatter;
  }

}
