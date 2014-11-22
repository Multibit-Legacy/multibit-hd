package org.multibit.hd.ui.views.components.trezor_display;

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
public class TrezorDisplayModel implements Model<String> {

  private String displayText;

  private final String panelName;

  public TrezorDisplayModel(String panelName) {
    this.panelName = panelName;
  }

  @Override
  public String getValue() {
    return displayText;
  }

  @Override
  public void setValue(String value) {
    this.displayText = value;
  }

  public String getPanelName() {
    return panelName;
  }
}
