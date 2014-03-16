package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.components.display_message.DisplayInfoMessageModel;
import org.multibit.hd.ui.views.components.display_message.DisplayInfoMessageView;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeModel;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeView;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertModel;
import org.multibit.hd.ui.views.components.display_security_alert.DisplaySecurityAlertView;
import org.multibit.hd.ui.views.components.enter_yes_no.EnterYesNoModel;
import org.multibit.hd.ui.views.components.enter_yes_no.EnterYesNoView;

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
   * <p>An "enter Yes/No" model and view displays a popover with the following features:</p>
   * <ul>
   * <li>Button to close the light box popover</li>
   * <li>Label field indicating the choice, with No and Close being the "take me to safety" option</li>
   * <li>Yes and No buttons</li>
   * </ul>
   *
   * @param panelName The underlying panel name for this popover
   *
   * @return A new "yes/no" model and view
   */
  public static ModelAndView<EnterYesNoModel, EnterYesNoView> newEnterYesNoPopoverMaV(String panelName) {

    EnterYesNoModel model = new EnterYesNoModel(panelName);
    EnterYesNoView view = new EnterYesNoView(model);

    return new ModelAndView<>(model, view);

  }

  /**
   * <p>A "display QR" model and view displays a QR code with the following features:</p>
   * <ul>
   * <li>Image field showing a QR code</li>
   * <li>Button to copy the QR code image to the Clipboard</li>
   * <li>Button to close the light box popover</li>
   * <li></li>
   * </ul>
   *
   * @param panelName The underlying panel name for this popover
   *
   * @return A new "display QR code" model and view
   */
  public static ModelAndView<DisplayQRCodeModel, DisplayQRCodeView> newDisplayQRCodePopoverMaV(String panelName) {

    DisplayQRCodeModel model = new DisplayQRCodeModel(panelName);
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
   * @param panelName The underlying panel name for this popover
   *
   * @return A new "display security alert" model and view
   */
  public static ModelAndView<DisplaySecurityAlertModel, DisplaySecurityAlertView> newDisplaySecurityPopoverMaV(String panelName) {

    DisplaySecurityAlertModel model = new DisplaySecurityAlertModel(panelName);
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
   * @param panelName The underlying panel name for this popover
   *
   * @return A new "display info message" model and view
   */
  public static ModelAndView<DisplayInfoMessageModel, DisplayInfoMessageView> newDisplayInfoPopoverMaV(String panelName) {

    DisplayInfoMessageModel model = new DisplayInfoMessageModel(panelName);
    DisplayInfoMessageView view = new DisplayInfoMessageView(model);

    return new ModelAndView<>(model, view);

  }

}
