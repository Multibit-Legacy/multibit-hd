package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.util.List;

import static org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState.CONFIRM_WALLET_SEED_PHRASE;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Confirm wallet seed phrase display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class ConfirmWalletSeedPhraseView extends AbstractWizardView<WelcomeWizardModel, List<String>> {

  private ModelAndView<EnterSeedPhraseModel, EnterSeedPhraseView> enterSeedPhraseMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public ConfirmWalletSeedPhraseView(AbstractWizard<WelcomeWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.CONFIRM_WALLET_SEED_PHRASE_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    enterSeedPhraseMaV = Components.newEnterSeedPhraseMaV(WelcomeWizardState.CONFIRM_WALLET_SEED_PHRASE.name());
    setPanelModel(enterSeedPhraseMaV.getModel().getValue());

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0,hidemode 1", // Layout constraints
      "[]", // Column constraints
      "[][]" // Row constraints
    ));

    panel.add(Panels.newConfirmSeedPhrase(), "wrap");
    panel.add(enterSeedPhraseMaV.getView().newPanel(), "wrap");

    return panel;
  }

  @Override
  public void fireViewEvents() {
    ViewEvents.fireWizardButtonEnabledEvent(CONFIRM_WALLET_SEED_PHRASE.name(), WizardButton.NEXT, false);
  }

  @Override
  public boolean updatePanelModel() {
    enterSeedPhraseMaV.getView().updateModel();
    return true;
  }

}
