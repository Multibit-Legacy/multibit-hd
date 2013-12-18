package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.wizards.AbstractWizard;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI for "Send Bitcoin":</p>
 * <ol>
 * <li>Enter amount</li>
 * <li>Confirm details</li>
 * <li>Watch progress</li>
 * </ol>
 *
 * @since 0.0.1
 *         
 */
public class SendBitcoinWizard extends AbstractWizard {

  @Override
  protected void addWizardContent(JPanel wizardPanel) {

    wizardPanel.add(new SendBitcoinEnterAmountPanel(this), "Enter amount");
    wizardPanel.add(new SendBitcoinConfirmSendPanel(this), "Confirm");
    wizardPanel.add(new SendBitcoinProgressPanel(this), "Progress");

  }

}
