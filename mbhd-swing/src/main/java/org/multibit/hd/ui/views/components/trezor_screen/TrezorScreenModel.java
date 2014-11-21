package org.multibit.hd.ui.views.components.trezor_screen;

import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Show the message displayed on the Trezor screen</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class TrezorScreenModel implements Model<String> {

  private String displayText;

  public TrezorScreenModel() {
  }

  @Override
  public String getValue() {
    return displayText;
  }

  @Override
  public void setValue(String value) {
    this.displayText = value;
  }

}
