package org.multibit.hd.ui.views.wizards.payments;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "payments" wizard:</p>
 * <ul>
 * <li>Storage of state for the "BIP70 payment request memo" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class BIP70PaymentRequestMemoPanelModel extends AbstractWizardPanelModel {

  public BIP70PaymentRequestMemoPanelModel(String panelName) {
    super(panelName);
  }
}
