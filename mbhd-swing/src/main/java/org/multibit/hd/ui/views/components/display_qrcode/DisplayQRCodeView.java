package org.multibit.hd.ui.views.components.display_qrcode;

import com.google.common.base.Optional;
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

    panel = Panels.newRoundedPanel();

    qrCodeImage = QRCodes.generateQRCode(getModel().get().getValue(), 3);

    panelCloseButton = Buttons.newPanelCloseButton(getClosePopoverAction());

    // Ensure it is accessible
    panelCloseButton.setName("popover_"+ MessageKey.CLOSE.getKey());

    // Add to the panel
    // Bug in JDK 1.7 on Mac prevents clipboard image copy
    // Possibly fixed in JDK 1.8
    if (!OSUtils.isMac()) {
      panel.add(Buttons.newCopyButton(getCopyClipboardAction()), "align left,push");
    }

    // QR code image
    JLabel imageLabel = Labels.newImageLabel(qrCodeImage);

    // Ensure it is accessible
    AccessibilityDecorator.apply(imageLabel, MessageKey.QR_CODE);

    panel.add(panelCloseButton, "align right,shrink,wrap");
    panel.add(imageLabel, "span 2,grow,push,wrap");

    JLabel transactionLabel = Labels.newBlankLabel();
    transactionLabel.setText(getModel().get().getTransactionLabel());
    panel.add(transactionLabel, "align center,push,wrap");

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
        ViewEvents.fireWizardPopoverHideEvent(getModel().get().getPanelName(),true);

      }

    };
  }


  @Override
  public void updateModelFromView() {
    // Do nothing the model is updated from key release events
  }

}
