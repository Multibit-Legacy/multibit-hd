package org.multibit.hd.ui.views.wizards.empty_wallet;

import org.multibit.hd.ui.views.components.enter_recipient.EnterRecipientModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "empty wallet" wizard:</p>
 * <ul>
 * <li>Storage of state for the "enter details" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EmptyWalletEnterDetailsPanelModel extends AbstractWizardPanelModel {

  // TODO Add password MaV support
  private final EnterRecipientModel enterRecipientModel;

  /**
   * @param panelName           The panel name
   * @param enterRecipientModel The "enter recipient" component model
   */
  public EmptyWalletEnterDetailsPanelModel(
    String panelName,
    EnterRecipientModel enterRecipientModel
  ) {
    super(panelName);
    this.enterRecipientModel = enterRecipientModel;
  }

  /**
   * @return The recipient model
   */
  public EnterRecipientModel getEnterRecipientModel() {
    return enterRecipientModel;
  }

}
