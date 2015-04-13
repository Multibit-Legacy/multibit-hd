package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;

/**
 * <p>Wizard to provide the following to UI for "empty wallet":</p>
 * <ol>
 * <li>Show progress report</li>
 * </ol>
 *
 * @since 0.0.1
 */
public class EmptyWalletWizard extends AbstractHardwareWalletWizard<EmptyWalletWizardModel> {

  public EmptyWalletWizard(EmptyWalletWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      EmptyWalletState.EMPTY_WALLET_ENTER_DETAILS.name(),
      new EmptyWalletEnterDetailsPanelView(this, EmptyWalletState.EMPTY_WALLET_ENTER_DETAILS.name())
    );

    wizardViewMap.put(
      EmptyWalletState.EMPTY_WALLET_CONFIRM.name(),
      new EmptyWalletConfirmPanelView(this, EmptyWalletState.EMPTY_WALLET_CONFIRM.name())
    );

    wizardViewMap.put(
      EmptyWalletState.EMPTY_WALLET_ENTER_PIN_FROM_CONFIRM_TREZOR.name(),
      new EmptyWalletEnterPinPanelView(this, EmptyWalletState.EMPTY_WALLET_ENTER_PIN_FROM_CONFIRM_TREZOR.name()));

    wizardViewMap.put(
      EmptyWalletState.EMPTY_WALLET_CONFIRM_TREZOR.name(),
      new EmptyWalletConfirmTrezorPanelView(this, EmptyWalletState.EMPTY_WALLET_CONFIRM_TREZOR.name()));

    wizardViewMap.put(
      EmptyWalletState.EMPTY_WALLET_REPORT.name(),
      new EmptyWalletReportPanelView(this, EmptyWalletState.EMPTY_WALLET_REPORT.name())
    );

  }

}
