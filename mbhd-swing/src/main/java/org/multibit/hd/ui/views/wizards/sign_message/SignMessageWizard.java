package org.multibit.hd.ui.views.wizards.sign_message;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "sign message":</p>
 * <ol>
 * <li>Enter details</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class SignMessageWizard extends AbstractHardwareWalletWizard<SignMessageWizardModel> {

  public SignMessageWizard(SignMessageWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      SignMessageState.SIGN_MESSAGE_PASSWORD.name(),
      new SignMessagePasswordPanelView(this, SignMessageState.SIGN_MESSAGE_PASSWORD.name())
    );

    wizardViewMap.put(
      SignMessageState.SIGN_MESSAGE_HARDWARE.name(),
      new SignMessageHardwarePanelView(this, SignMessageState.SIGN_MESSAGE_HARDWARE.name())
    );

    wizardViewMap.put(
      SignMessageState.SIGN_MESSAGE_HARDWARE_ENTER_PIN.name(),
      new SignMessageEnterPinPanelView(this, SignMessageState.SIGN_MESSAGE_HARDWARE_ENTER_PIN.name())
    );

    wizardViewMap.put(
      SignMessageState.SIGN_MESSAGE_HARDWARE_CONFIRM_SIGN.name(),
      new SignMessageConfirmSignHardwarePanelView(this, SignMessageState.SIGN_MESSAGE_HARDWARE_CONFIRM_SIGN.name())
    );

  }

}
