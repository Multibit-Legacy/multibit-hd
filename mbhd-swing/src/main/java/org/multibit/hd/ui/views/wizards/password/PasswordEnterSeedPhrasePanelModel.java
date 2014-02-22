package org.multibit.hd.ui.views.wizards.password;

import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "password" wizard:</p>
 * <ul>
 * <li>Storage of state for the "enter seed phrase" panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PasswordEnterSeedPhrasePanelModel extends AbstractWizardPanelModel {

  private final EnterSeedPhraseModel enterSeedPhraseModel;

  public PasswordEnterSeedPhrasePanelModel(String panelName, EnterSeedPhraseModel enterSeedPhraseModel) {
    super(panelName);

    this.enterSeedPhraseModel = enterSeedPhraseModel;

  }

  /**
   * @return The "enter seed phrase" model
   */
  public EnterSeedPhraseModel getEnterSeedPhraseModel() {
    return enterSeedPhraseModel;
  }

}
