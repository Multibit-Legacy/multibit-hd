package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.services.FeeService;
import org.multibit.hd.ui.languages.MessageKey;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.util.Hashtable;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Produce a slider</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class Sliders {

  /**
   * Resolution of a single tick of the slider, in satoshi
   */
  public static final int RESOLUTION = 2500;

  /**
   * Utilities have no public constructor
   */
  private Sliders() {
  }

  /**
   * <p>Create a new slider to adjust transaction fee</p>
   *
   * @param changeListener The change listener to respond to adjustments
   * @param initialPosition The initial position to choose on the slider, in satoshi
   */
  public static JSlider newAdjustTransactionFeeSlider(ChangeListener changeListener, long initialPosition) {
    // Resolution is RESOLUTION satoshis per tick
    int minimumPosition = (int) FeeService.MINIMUM_FEE_PER_KB.longValue()/RESOLUTION;
    int defaultPosition = (int)FeeService.DEFAULT_FEE_PER_KB.longValue()/RESOLUTION;
    int maximumPosition = (int)FeeService.MAXIMUM_FEE_PER_KB.longValue()/RESOLUTION;

    // Make sure feePerKB is normalised first so that it will be in range of the slider
    int currentPosition = (int)FeeService.normaliseRawFeePerKB(initialPosition).longValue()/RESOLUTION;
    JSlider feePerKBSlider = new JSlider(minimumPosition, maximumPosition,
            currentPosition);

    feePerKBSlider.setMajorTickSpacing(10);
    feePerKBSlider.setPaintTicks(true);

    // Create the label table
    Hashtable<Integer, JComponent> labelTable = new Hashtable<>();
    labelTable.put( minimumPosition, Labels.newLabel(MessageKey.LOWER));
    labelTable.put( defaultPosition, Labels.newDefaultNote());
    labelTable.put(maximumPosition, Labels.newLabel(MessageKey.HIGHER));
    feePerKBSlider.setLabelTable(labelTable);
    feePerKBSlider.setPaintLabels(true);

    feePerKBSlider.addChangeListener(changeListener);

    // Ensure it is accessible
    AccessibilityDecorator.apply(feePerKBSlider, MessageKey.ADJUST_TRANSACTION_FEE);

    return feePerKBSlider;
  }
}