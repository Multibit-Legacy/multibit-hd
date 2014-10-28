package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "Send Bitcoin":</p>
 * <ol>
 * <li>Enter amount (or empty entirely)</li>
 * <li>Confirm details</li>
 * <li>Report progress</li>
 * </ol>
 *
 * @since 0.0.1
 *         
 */
public class SendBitcoinWizard extends AbstractWizard<SendBitcoinWizardModel> {

  public SendBitcoinWizard(SendBitcoinWizardModel model) {
    super(model, false, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      SendBitcoinState.SEND_ENTER_AMOUNT.name(),
      new SendBitcoinEnterAmountPanelView(this, SendBitcoinState.SEND_ENTER_AMOUNT.name()));
    wizardViewMap.put(
      SendBitcoinState.SEND_CONFIRM_AMOUNT.name(),
      new SendBitcoinConfirmPanelView(this, SendBitcoinState.SEND_CONFIRM_AMOUNT.name()));
    wizardViewMap.put(
      SendBitcoinState.SEND_CONFIRM_TREZOR.name(),
      new SendBitcoinConfirmTrezorPanelView(this, SendBitcoinState.SEND_CONFIRM_TREZOR.name()));
    wizardViewMap.put(
      SendBitcoinState.SEND_REPORT.name(),
      new SendBitcoinReportPanelView(this, SendBitcoinState.SEND_REPORT.name()));
  }

}
