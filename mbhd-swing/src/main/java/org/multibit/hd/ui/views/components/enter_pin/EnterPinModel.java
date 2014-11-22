package org.multibit.hd.ui.views.components.enter_pin;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Entry of Trezor PIN</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class EnterPinModel implements Model<String> {

  private static final Logger log = LoggerFactory.getLogger(EnterPinModel.class);

  /**
   * The PIN as an ordered list of button positions (0 1 2 on top row, 3 4 5 on middle row, 6, 7, 8 on bottom row)
   */
  private List<Integer> buttonPositionsPressed = new ArrayList<>();

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public EnterPinModel(String panelName) {
    this.panelName = panelName;
  }

  /**
   * @return The panel name that this component is associated with
   */
  public String getPanelName() {
    return panelName;
  }

  /**
   * @return The button positions of the PIN using a numerical keyboard layout (1 bottom left, 9 top right)
   */
  @Override
  public String getValue() {
    StringBuilder builder = new StringBuilder();
    for (Integer buttonPosition : buttonPositionsPressed) {
      builder.append(buttonPosition.toString());
    }
    return builder.toString();
  }

  @Override
  public void setValue(String value) {
    Preconditions.checkNotNull(value, "'value' must be present");

    buttonPositionsPressed = new ArrayList<>();
    for (int i = 0; i < value.length(); i++) {
      buttonPositionsPressed.add(Integer.parseInt(value.substring(i, i)));
    }
  }

//  public void setPin(char[] pin) {
//
//    this.pin = Optional.of(pin);
//
//    // Alert the panel model that a component has changed
//    ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
//  }

  public void addButtonPressed(int buttonPosition) {
    log.debug("Saw a button pressed at position " + buttonPosition);
    buttonPositionsPressed.add(buttonPosition);

    // Alert the panel model that a component has changed
    ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
  }

  public void removeLastButtonPressed() {
    if (!buttonPositionsPressed.isEmpty()) {
      buttonPositionsPressed.remove(buttonPositionsPressed.size() - 1);

      // Alert the panel model that a component has changed
      ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
    }
  }
}
