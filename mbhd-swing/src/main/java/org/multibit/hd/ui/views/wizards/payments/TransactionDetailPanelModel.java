package org.multibit.hd.ui.views.wizards.payments;

import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "payments" wizard:</p>
 * <ul>
 * <li>Storage of state for the "show transaction detail" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class TransactionDetailPanelModel extends AbstractWizardPanelModel {

  public TransactionDetailPanelModel(String panelName) {
    super(panelName);
  }
}
