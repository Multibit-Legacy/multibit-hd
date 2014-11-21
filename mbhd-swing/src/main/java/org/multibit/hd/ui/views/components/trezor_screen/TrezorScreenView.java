package org.multibit.hd.ui.views.components.trezor_screen;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of a seed phrase display</li>
 * <li>Support for refresh and reveal operations</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class TrezorScreenView extends AbstractComponentView<TrezorScreenModel> {

  // View components
  private JLabel operationText;
  private JTextArea deviceDisplayTextArea;

  /**
   * @param model The model backing this view
   */
  public TrezorScreenView(TrezorScreenModel model) {
    super(model);
  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(new MigLayout(
      Panels.migXLayout(), // Layout
      "[]", // Columns
      "[]10[]" // Rows
    ));

    // Populate components
    operationText = Labels.newCommunicatingWithTrezor();
    deviceDisplayTextArea = TextBoxes.newTrezorV1Display();

    // Start the Trezor display as invisible until text is set
    deviceDisplayTextArea.setVisible(false);

    // Add to the panel
    panel.add(operationText, "align center,wrap");
    panel.add(deviceDisplayTextArea, "align center,wrap");

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    // Do nothing - components are read only
  }

  @Override
  public void updateModelFromView() {
    // Do nothing - the model is driving the view
  }

  /**
   * <p>Update the display with suitable text</p>
   *
   * @param key The message key defining the Trezor text
   */
  public void setDeviceText(MessageKey key) {

    this.deviceDisplayTextArea.setVisible(true);
    this.deviceDisplayTextArea.setText(Languages.safeText(key));

  }
}