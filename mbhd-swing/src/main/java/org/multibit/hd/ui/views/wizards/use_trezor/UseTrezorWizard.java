package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "use Trezor" wizard:</p>
 * <ol>
 * <li>Enter PIN</li>
 * </ol>
 *
 * @since 0.0.1
 *  
 */
public class UseTrezorWizard extends AbstractHardwareWalletWizard<UseTrezorWizardModel> {

  public UseTrezorWizard(UseTrezorWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      UseTrezorState.SELECT_TREZOR_ACTION.name(),
      new UseTrezorSelectPanelView(this, UseTrezorState.SELECT_TREZOR_ACTION.name()));

    // Transitional view to kick off the process of authenticating to Trezor
    wizardViewMap.put(
      UseTrezorState.REQUEST_CIPHER_KEY.name(),
      new UseTrezorRequestCipherKeyPanelView(this, UseTrezorState.REQUEST_CIPHER_KEY.name()));

    wizardViewMap.put(UseTrezorState.BUY_TREZOR.name(), new UseTrezorBuyTrezorPanelView(this, UseTrezorState.BUY_TREZOR.name()));

    wizardViewMap.put(UseTrezorState.VERIFY_TREZOR.name(), new UseTrezorVerifyDevicePanelView(this, UseTrezorState.VERIFY_TREZOR.name()));

    wizardViewMap.put(UseTrezorState.WIPE_TREZOR.name(), new UseTrezorWipeDevicePanelView(this, UseTrezorState.WIPE_TREZOR.name()));

    wizardViewMap.put(
      UseTrezorState.ENTER_PIN.name(),
      new UseTrezorEnterPinPanelView(this, UseTrezorState.ENTER_PIN.name()));

    // TODO - "no PIN" panel


    // Ask user to confirm 'Encrypt MultiBit HD unlock text'
    wizardViewMap.put(
      UseTrezorState.PRESS_CONFIRM_FOR_UNLOCK.name(),
      new UseTrezorPressConfirmForEncryptCodePanelView(this, UseTrezorState.PRESS_CONFIRM_FOR_UNLOCK.name()));

    // Trezor report panel
    wizardViewMap.put(
       UseTrezorState.USE_TREZOR_REPORT_PANEL.name(),
       new UseTrezorReportPanelView(this, UseTrezorState.USE_TREZOR_REPORT_PANEL.name()));

  }
}
