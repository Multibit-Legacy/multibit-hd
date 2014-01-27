package org.multibit.hd.ui.views.components.text_fields;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.utils.Numbers;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

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
public class ThemedDecimalInputVerifier extends InputVerifier {

  private final Double minValue;
  private final Double maxValue;

  private final Color invalidColor = Themes.currentTheme.invalidDataEntryBackground();
  private final Color validColor = Themes.currentTheme.dataEntryBackground();

  public ThemedDecimalInputVerifier() {
    minValue = -Double.MAX_VALUE;
    maxValue = Double.MAX_VALUE;
  }

  /**
   * @param min Set the minimum value
   * @param max Set the maximum value
   *
   * @throws IllegalArgumentException
   */

  public ThemedDecimalInputVerifier(Double min, Double max) {

    Preconditions.checkNotNull(min, "'min' must be present");
    Preconditions.checkNotNull(max, "'max' must be present");

    Preconditions.checkState(min.compareTo(max) == -1, "'min' must be less than max");

    minValue = min;
    maxValue = max;
  }

  public boolean verify(JComponent component) {

    String text = ((JTextComponent) component).getText();

    Optional<Double> value = Numbers.parseDouble(text);

    if (value.isPresent()) {

      if (value.get().compareTo(minValue) == -1 || value.get().compareTo(maxValue) == 1) {
        component.setBackground(invalidColor);
        return false;
      }
    }

    // Must be OK to be here
    component.setBackground(validColor);

    return true;
  }

}
