package org.multibit.hd.ui.views.wizards.use_hardware_wallet;

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
public class UseHardwareWalletWizard extends AbstractHardwareWalletWizard<UseHardwareWalletWizardModel> {

  public UseHardwareWalletWizard(UseHardwareWalletWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      UseHardwareWalletState.SELECT_HARDWARE_ACTION.name(),
      new UseHardwareWalletSelectPanelView(this, UseHardwareWalletState.SELECT_HARDWARE_ACTION.name()));

    wizardViewMap.put(
      UseHardwareWalletState.BUY_DEVICE.name(),
      new UseHardwareWalletBuyDevicePanelView(this, UseHardwareWalletState.BUY_DEVICE.name()));

    wizardViewMap.put(
      UseHardwareWalletState.VERIFY_DEVICE.name(),
      new UseHardwareWalletVerifyDevicePanelView(this, UseHardwareWalletState.VERIFY_DEVICE.name()));

    wizardViewMap.put(
      UseHardwareWalletState.REQUEST_WIPE_DEVICE.name(),
      new UseHardwareWalletRequestWipeDevicePanelView(this, UseHardwareWalletState.REQUEST_WIPE_DEVICE.name()));

    wizardViewMap.put(
      UseHardwareWalletState.CONFIRM_WIPE_DEVICE.name(),
      new UseHardwareWalletConfirmWipeDevicePanelView(this, UseHardwareWalletState.CONFIRM_WIPE_DEVICE.name()));

    wizardViewMap.put(
      UseHardwareWalletState.ENTER_PIN.name(),
      new UseHardwareWalletEnterPinPanelView(this, UseHardwareWalletState.ENTER_PIN.name()));

    // Trezor report panel
    wizardViewMap.put(
       UseHardwareWalletState.USE_HARDWARE_WALLET_REPORT_PANEL.name(),
       new UseHardwareWalletReportPanelView(this, UseHardwareWalletState.USE_HARDWARE_WALLET_REPORT_PANEL.name()));

  }
}
