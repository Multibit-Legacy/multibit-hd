package org.multibit.hd.ui.views.wizards.welcome;

import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Storage of state for the "send bitcoin" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RestoreWalletTimestampPanelModel extends AbstractWizardPanelModel {

  private final EnterSeedPhraseModel enterSeedPhraseModel;
  private final ConfirmPasswordModel confirmPasswordModel;

  /**
   * @param panelName            The panel name
   * @param enterSeedPhraseModel The "enter seed phrase" component model
   * @param confirmPasswordModel The "confirm credentials" component model
   */
  public RestoreWalletTimestampPanelModel(
    String panelName,
    EnterSeedPhraseModel enterSeedPhraseModel,
    ConfirmPasswordModel confirmPasswordModel
  ) {
    super(panelName);
    this.enterSeedPhraseModel = enterSeedPhraseModel;
    this.confirmPasswordModel = confirmPasswordModel;
  }

  /**
   * @return The seed phrase model
   */
  public EnterSeedPhraseModel getEnterSeedPhraseModel() {
    return enterSeedPhraseModel;
  }

  /**
   * @return The credentials model
   */
  public ConfirmPasswordModel getConfirmPasswordModel() {
    return confirmPasswordModel;
  }

}
