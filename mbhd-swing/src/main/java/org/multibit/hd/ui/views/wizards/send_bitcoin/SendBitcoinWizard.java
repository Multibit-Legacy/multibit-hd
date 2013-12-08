package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.multibit.hd.ui.views.wizards.AbstractWizard;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin sequence</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class SendBitcoinWizard extends AbstractWizard {

  public SendBitcoinWizard() {

    getContentPanel().setSize(400, 400);

    getContentPanel().add(new SendBitcoinEnterAmountPanel(this), "Send Bitcoin");
    getContentPanel().add(new SendBitcoinConfirmSendPanel(this), "Confirm Send");
  }

}
