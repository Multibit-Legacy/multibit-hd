package org.multibit.hd.ui.views.components.text_fields;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.utils.Numbers;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.math.BigDecimal;

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
   * @param maxLength     The maximum length
   */
  public FormattedDecimalField(double min, double max, int decimalPlaces, int maxLength) {

    super();

    Preconditions.checkNotNull(min, "'min' must be present");
    Preconditions.checkNotNull(max, "'max' must be present");
    Preconditions.checkState(min < max, "'min' must be less than 'max'");

    Preconditions.checkState(decimalPlaces >= 0 && decimalPlaces < 15, "'decimalPlaces' must be in range [0,15)");

    setInputVerifier(new ThemeAwareDecimalInputVerifier(min, max));

    setBackground(Themes.currentTheme.dataEntryBackground());

    // Build number formatters
    NumberFormatter defaultFormatter = new NumberFormatter();
    defaultFormatter.setValueClass(BigDecimal.class);

    NumberFormatter displayFormatter = Numbers.newDisplayFormatter(decimalPlaces, maxLength);
    NumberFormatter editFormatter = Numbers.newEditFormatter(decimalPlaces, maxLength);

    setFormatterFactory(new DefaultFormatterFactory(
      defaultFormatter,
      displayFormatter,
      editFormatter
    ));

  }

}
