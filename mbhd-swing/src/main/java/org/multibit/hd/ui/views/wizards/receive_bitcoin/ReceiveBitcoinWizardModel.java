package org.multibit.hd.ui.views.wizards.receive_bitcoin;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "exit" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ReceiveBitcoinWizardModel extends AbstractWizardModel<ReceiveBitcoinState> {

  /**
   * @param state The state object
   */
  public ReceiveBitcoinWizardModel(ReceiveBitcoinState state) {
    super(state);
  }
}
