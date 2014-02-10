package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_seed_phrase.DisplaySeedPhraseModel;
import org.multibit.hd.ui.views.components.display_seed_phrase.DisplaySeedPhraseView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

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
public class CreateWalletSeedPhrasePanelView extends AbstractWizardPanelView<WelcomeWizardModel, List<String>> {

  private ModelAndView<DisplaySeedPhraseModel, DisplaySeedPhraseView> displaySeedPhraseMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public CreateWalletSeedPhrasePanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.CREATE_WALLET_SEED_PHRASE_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    SeedPhraseGenerator seedPhraseGenerator = getWizardModel().getSeedPhraseGenerator();

    displaySeedPhraseMaV = Components.newDisplaySeedPhraseMaV(seedPhraseGenerator);
    setPanelModel(displaySeedPhraseMaV.getModel().getValue());

    getWizardModel().setCreateWalletSeedPhrase(displaySeedPhraseMaV.getModel().getSeedPhrase());
    getWizardModel().setActualSeedTimestamp(displaySeedPhraseMaV.getModel().getSeedTimestamp());

  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constraints
      "[]", // Column constraints
      "[][]" // Row constraints
    ));

    panel.add(Panels.newSeedPhraseWarning(), "grow,push,wrap");
    panel.add(displaySeedPhraseMaV.getView().newComponentPanel(), "wrap");

    return panel;
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the "next" button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Update the wizard model with the latest seed information
    getWizardModel().setCreateWalletSeedPhrase(displaySeedPhraseMaV.getModel().getSeedPhrase());
    getWizardModel().setActualSeedTimestamp(displaySeedPhraseMaV.getModel().getSeedTimestamp());

  }

}
