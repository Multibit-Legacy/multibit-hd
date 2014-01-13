package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_seed_phrase.DisplaySeedPhraseModel;
import org.multibit.hd.ui.views.components.display_seed_phrase.DisplaySeedPhraseView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

import javax.swing.*;
import java.util.List;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Create wallet from seed phrase display</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class CreateWalletSeedPhraseView extends AbstractWizardView<WelcomeWizardModel, List<String>> {

  private ModelAndView<DisplaySeedPhraseModel, DisplaySeedPhraseView> displaySeedPhraseMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public CreateWalletSeedPhraseView(AbstractWizard<WelcomeWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.CREATE_WALLET_SEED_PHRASE_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    SeedPhraseGenerator seedPhraseGenerator = getWizardModel().getSeedPhraseGenerator();

    displaySeedPhraseMaV = Components.newDisplaySeedPhraseMaV(seedPhraseGenerator);
    setPanelModel(displaySeedPhraseMaV.getModel().getValue());

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constrains
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Panels.newSeedPhraseWarning(), "grow,push,wrap");
    panel.add(displaySeedPhraseMaV.getView().newPanel(), "wrap");

    return panel;
  }

  @Override
  public void fireViewEvents() {
    // Do nothing
  }

  @Override
  public boolean updatePanelModel() {
    displaySeedPhraseMaV.getView().updateModel();
    return false;
  }

}
