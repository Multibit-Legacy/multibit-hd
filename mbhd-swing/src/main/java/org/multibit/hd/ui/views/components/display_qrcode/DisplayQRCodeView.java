package org.multibit.hd.ui.views.components.display_qrcode;

import com.google.common.base.Optional;
import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.ui.utils.ClipboardUtils;
import org.multibit.hd.ui.utils.QRCodes;
import org.multibit.hd.ui.views.components.AbstractComponentView;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;

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


  /**
   * @param model The model backing this view
   */
  public DisplayQRCodeView(DisplayQRCodeModel model) {
    super(model);

  }

  @Override
  public JPanel newComponentPanel() {

    panel = Panels.newRoundedPanel();

    qrCodeImage = QRCodes.generateQRCode(getModel().get().getValue(), 2);

    // Add to the panel
    // Bug in JDK 1.7 on Mac prevents clipboard image copy
    // Possibly fixed in JDK 1.8
    if (!OSUtils.isMac()) {
      panel.add(Buttons.newCopyButton(getCopyClipboardAction()), "align left,push");
    }
    panel.add(Buttons.newPanelCloseButton(getClosePopoverAction()), "align right,shrink,wrap");
    panel.add(Labels.newImageLabel(qrCodeImage), "span 2,grow,push,wrap");

    // Panel needs to be this size to allow for largest Bitcoin URI
    panel.setSize(350, 350);

    return panel;

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

        Panels.hideLightBoxPopover();

      }

    };
  }


  @Override
  public void updateModelFromView() {
    // Do nothing the model is updated from key release events
  }

}
