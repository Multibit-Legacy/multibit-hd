package org.multibit.hd.ui.views.components.display_qrcode;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.ClipboardUtils;
import org.multibit.hd.ui.utils.QRCodes;
import org.multibit.hd.ui.views.components.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

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
public class DisplayQRCodeView extends AbstractComponentView<DisplayQRCodeModel> {

  private Optional<BufferedImage> qrCodeImage;

  private JButton panelCloseButton;

  /**
   * @param model The model backing this view
   */
  public DisplayQRCodeView(DisplayQRCodeModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newRoundedPanel(new MigLayout(
      Panels.migXPopoverLayout(),
      "[]",
      "[]5"
    ));

    qrCodeImage = QRCodes.generateQRCode(getModel().get().getValue(), 3);

    panelCloseButton = Buttons.newPanelCloseButton(getClosePopoverAction());

    // Ensure it is accessible
    panelCloseButton.setName("popover_" + MessageKey.CLOSE.getKey());

    // QR code image
    JLabel imageLabel = Labels.newImageLabel(qrCodeImage);

    // Ensure it is accessible
    AccessibilityDecorator.apply(imageLabel, MessageKey.QR_CODE);

    panel.add(panelCloseButton, "align right,shrink,wrap");

    // Determine if the copy button should be shown
    // Bug in JDK 1.7 on Mac prevents clipboard image copy
    // Possibly fixed in JDK 1.8
    boolean isCopyAvailable = !OSUtils.isMac();

    // Provide some descriptive text
    JLabel qrCodePopoverNote = Labels.newQRCodePopoverNote(isCopyAvailable);
    panel.add(qrCodePopoverNote, "push,align center,wrap");

    // Add the image filling as much space as possible
    panel.add(imageLabel, "align center,grow,push,wrap");

    // Place text immediately below it
    JLabel transactionLabel = Labels.newBlankLabel();
    transactionLabel.setText(getModel().get().getTransactionLabel());
    panel.add(transactionLabel, "align center,push,wrap");

    // Add to the panel to bottom right where it will be seen quickly
    if (isCopyAvailable) {
      panel.add(Buttons.newCopyButton(getCopyClipboardAction()), "align right,push");
    }

    // Set minimum size
    panel.setSize(MultiBitUI.POPOVER_MAX_WIDTH, MultiBitUI.POPOVER_MAX_HEIGHT);

    return panel;

  }

  @Override
  public void requestInitialFocus() {
    panelCloseButton.requestFocusInWindow();
  }

  /**
   * @return A new action for copying the QR code image to the clipboard
   */
  private Action getCopyClipboardAction() {

    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // Copy the image to the clipboard
        ClipboardUtils.copyImageToClipboard(qrCodeImage.get());

      }

    };
  }

  /**
   * @return A new action for closing the QR code popover
   */
  private Action getClosePopoverAction() {

    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.hideLightBoxPopoverIfPresent();

        // Issue the wizard popover hide event
        ViewEvents.fireWizardPopoverHideEvent(getModel().get().getPanelName(), true);

      }

    };
  }


  @Override
  public void updateModelFromView() {
    // Do nothing the model is updated from key release events
  }

}
