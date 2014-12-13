package org.multibit.hd.ui.views.wizards.welcome.create_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.display_seed_phrase.DisplaySeedPhraseModel;
import org.multibit.hd.ui.views.components.display_seed_phrase.DisplaySeedPhraseView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;

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

    super(wizard, panelName, MessageKey.CREATE_WALLET_SEED_PHRASE_TITLE, AwesomeIcon.KEY);

  }

  @Override
  public void newPanelModel() {

    SeedPhraseGenerator seedPhraseGenerator = getWizardModel().getSeedPhraseGenerator();

    displaySeedPhraseMaV = Components.newDisplaySeedPhraseMaV(seedPhraseGenerator);
    setPanelModel(displaySeedPhraseMaV.getModel().getValue());

    getWizardModel().setCreateWalletSeedPhrase(displaySeedPhraseMaV.getModel().getSeedPhrase());
    getWizardModel().setActualSeedTimestamp(displaySeedPhraseMaV.getModel().getSeedTimestamp());

    // Register components
    registerComponents(displaySeedPhraseMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[][]" // Row constraints
    ));

    // Warning notes
    contentPanel.add(Labels.newNoteLabel(MessageKey.SEED_WARNING_NOTE_1, null), MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(Labels.newNoteLabel(MessageKey.SEED_WARNING_NOTE_2, null), MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(Labels.newNoteLabel(MessageKey.SEED_WARNING_NOTE_3, null), MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(Labels.newNoteLabel(MessageKey.SEED_WARNING_NOTE_4, null), MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(displaySeedPhraseMaV.getView().newComponentPanel(), MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the "next" button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        displaySeedPhraseMaV.getView().requestInitialFocus();
        // Ensure there is a new seed phrase each time to strongly urge the use of pen and paper
        displaySeedPhraseMaV.getView().newSeedPhrase(displaySeedPhraseMaV.getModel().getCurrentSeedSize());
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Update the wizard model with the latest seed information
    getWizardModel().setCreateWalletSeedPhrase(displaySeedPhraseMaV.getModel().getSeedPhrase());
    getWizardModel().setActualSeedTimestamp(displaySeedPhraseMaV.getModel().getSeedTimestamp());

  }

}
