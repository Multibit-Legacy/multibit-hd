package org.multibit.hd.ui.views.components.display_address;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.utils.ClipboardUtils;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Presentation of Bitcoin address</li>
 * <li>Support for clipboard copy operation</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class DisplayBitcoinAddressView extends AbstractComponentView<DisplayBitcoinAddressModel> {

  private JTextField bitcoinAddress;

  /**
   * @param model The model backing this view
   */
  public DisplayBitcoinAddressView(DisplayBitcoinAddressModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newPanel(new MigLayout(
      "insets 0", // Layout
      "[][]", // Columns
      "[]" // Rows
    ));

    // Populate the Bitcoin address
    bitcoinAddress = TextBoxes.newDisplayBitcoinAddress(getModel().get().getValue());

    // Configure the actions
    Action copyClipboardAction = getCopyClipboardAction();

    // Add to the panel
    panel.add(bitcoinAddress, "grow,push");
    panel.add(Buttons.newCopyButton(copyClipboardAction));

    return panel;

  }

  @Override
  public void requestInitialFocus() {

    if (bitcoinAddress != null) {
      bitcoinAddress.requestFocusInWindow();
    }

  }

  /**
   * @return A new action for copying the model to the clipboard
   */
  private Action getCopyClipboardAction() {

    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        ClipboardUtils.copyStringToClipboard(getModel().get().getValue());

      }

    };
  }

  @Override
  public void updateModelFromView() {
    // Do nothing the model is updated from key release events
  }

}
