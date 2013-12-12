package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.wizards.AbstractWizard;

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

  public SendBitcoinWizard() {

    super();

    getContentPanel().add(new SendBitcoinEnterAmountPanel(this), "Enter amount");
    getContentPanel().add(new SendBitcoinConfirmSendPanel(this), "Confirm");
    getContentPanel().add(new SendBitcoinProgressPanel(this), "Progress");

  }

}
