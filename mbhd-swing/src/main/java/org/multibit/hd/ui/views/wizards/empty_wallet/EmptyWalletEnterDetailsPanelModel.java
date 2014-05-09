package org.multibit.hd.ui.views.wizards.empty_wallet;

import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
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

  private final EnterRecipientModel enterRecipientModel;
  private final EnterPasswordModel enterPasswordModel;

  /**
   * @param panelName           The panel name
   * @param enterRecipientModel The "enter recipient" component model
   * @param enterPasswordModel  The "enter password" component model
   */
  public EmptyWalletEnterDetailsPanelModel(
    String panelName,
    EnterRecipientModel enterRecipientModel,
    EnterPasswordModel enterPasswordModel) {

    super(panelName);

    this.enterRecipientModel = enterRecipientModel;
    this.enterPasswordModel = enterPasswordModel;
  }

  /**
   * @return The recipient model
   */
  public EnterRecipientModel getEnterRecipientModel() {
    return enterRecipientModel;
  }

  /**
   * @return The "enter password" model
   */
  public EnterPasswordModel getEnterPasswordModel() {
    return enterPasswordModel;
  }

}
