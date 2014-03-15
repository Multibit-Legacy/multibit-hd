package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.components.display_message.DisplayInfoMessageModel;
import org.multibit.hd.ui.views.components.display_message.DisplayInfoMessageView;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeModel;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeView;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertModel;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertView;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Creation of complex components requiring a model and view suitable for use as popovers</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class Popovers {

  /**
   * <p>A "display QR" model and view displays a QR code with the following features:</p>
   * <ul>
   * <li>Image field showing a QR code</li>
   * <li>Button to copy the QR code image to the Clipboard</li>
   * <li>Button to close the light box popover</li>
   * <li></li>
   * </ul>
   *
   * @return A new "display Bitcoin address" model and view
   */
  public static ModelAndView<DisplayQRCodeModel, DisplayQRCodeView> newDisplayQRCodePopoverMaV() {

    DisplayQRCodeModel model = new DisplayQRCodeModel();
    DisplayQRCodeView view = new DisplayQRCodeView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "display security alert" model and view displays a security alert with the following features:</p>
   * <ul>
   * <li>Danger themed message panel</li>
   * <li>Button to close the light box popover</li>
   * </ul>
   *
   * @return A new "display security alert" model and view
   */
  public static ModelAndView<DisplaySecurityAlertModel, DisplaySecurityAlertView> newDisplaySecurityPopoverMaV() {

    DisplaySecurityAlertModel model = new DisplaySecurityAlertModel();
    DisplaySecurityAlertView view = new DisplaySecurityAlertView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "display info message" model and view displays a message with the following features:</p>
   * <ul>
   * <li>Success themed message panel</li>
   * <li>Button to close the light box popover</li>
   * </ul>
   *
   * @return A new "display info message" model and view
   */
  public static ModelAndView<DisplayInfoMessageModel, DisplayInfoMessageView> newDisplayInfoPopoverMaV() {

    DisplayInfoMessageModel model = new DisplayInfoMessageModel();
    DisplayInfoMessageView view = new DisplayInfoMessageView(model);

    return new ModelAndView<>(model, view);

  }

}
