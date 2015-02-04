package org.multibit.hd.ui.views.components.text_fields;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.multibit.hd.core.utils.Numbers;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.math.BigDecimal;

/**
 * <p>Input verifier to provide the following to UI:</p>
 * <ul>
 * <li>Parse decimal values</li>
 * <li>Apply theme colouring if the value is valid/invalid</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ThemeAwareDecimalInputVerifier extends InputVerifier {

  private final BigDecimal minValue;
  private final BigDecimal maxValue;

  private final Color invalidColor = Themes.currentTheme.invalidDataEntryBackground();
  private final Color validColor = Themes.currentTheme.dataEntryBackground();

  public ThemeAwareDecimalInputVerifier() {
    minValue = new BigDecimal(-Double.MAX_VALUE);
    maxValue = new BigDecimal(Double.MAX_VALUE);
  }

  /**
   * @param min Set the minimum value
   * @param max Set the maximum value
   *
   * @throws IllegalArgumentException
   */

  public ThemeAwareDecimalInputVerifier(Double min, Double max) {

    Preconditions.checkNotNull(min, "'min' must be present");
    Preconditions.checkNotNull(max, "'max' must be present");

    Preconditions.checkState(min.compareTo(max) == -1, "'min' must be less than max");

    minValue = new BigDecimal(min);
    maxValue = new BigDecimal(max);
  }

  public boolean verify(JComponent component) {

    String text = ((JTextComponent) component).getText();

    Optional<BigDecimal> value = Numbers.parseBigDecimal(text);

    if (value.isPresent()) {

      BigDecimal amount = value.get();

      if (amount.compareTo(minValue) == -1 || amount.compareTo(maxValue) == 1) {
        // Not in range
        component.setBackground(invalidColor);
        return false;
      } else {
        // Must be in range
        component.setBackground(validColor);
        return true;
      }
    } else {

      // Allow a blank to pass through for focus transition
      if (Strings.isNullOrEmpty(text)) {
        return true;
      }

      // Not a number
      component.setBackground(invalidColor);
      return false;
    }
  }

}
