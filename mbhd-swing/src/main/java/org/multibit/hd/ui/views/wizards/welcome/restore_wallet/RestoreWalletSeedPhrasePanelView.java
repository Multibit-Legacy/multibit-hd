package org.multibit.hd.ui.views.wizards.welcome.restore_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.brit.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseView;
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
 * <li>Restore wallet from seed phrase and timestamp</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */

public class RestoreWalletSeedPhrasePanelView extends AbstractWizardPanelView<WelcomeWizardModel, List<String>> {

  private ModelAndView<EnterSeedPhraseModel, EnterSeedPhraseView> enterSeedPhraseMaV;

  private SeedPhraseGenerator generator = new Bip39SeedPhraseGenerator();

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public RestoreWalletSeedPhrasePanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.RESTORE_WALLET_SEED_PHRASE_TITLE, AwesomeIcon.KEY);

  }

  @Override
  public void newPanelModel() {

    // Do not ask for timestamp until necessary
    enterSeedPhraseMaV = Components.newEnterSeedPhraseMaV(getPanelName(), false, true);
    setPanelModel(enterSeedPhraseMaV.getModel().getValue());

    getWizardModel().setRestoreWalletEnterSeedPhraseModel(enterSeedPhraseMaV.getModel());

    // Register components
    registerComponents(enterSeedPhraseMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migLayout("fill,insets 0,hidemode 1"),
      "[]", // Column constraints
      "[][]" // Row constraints
    ));

    contentPanel.add(Panels.newRestoreFromSeedPhrase(), "wrap");
    contentPanel.add(enterSeedPhraseMaV.getView().newComponentPanel(), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void afterShow() {

    enterSeedPhraseMaV.getView().requestInitialFocus();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Fire the decision events
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, isNextEnabled());

    // Fire "seed phrase verification" event
    ViewEvents.fireVerificationStatusChangedEvent(getPanelName() + ".seedphrase", isNextEnabled());
  }

  /**
   * @return True if the "next" button should be enabled
   */
  private boolean isNextEnabled() {

    // Get the user data
    String timestamp = enterSeedPhraseMaV.getModel().getSeedTimestamp();
    List<String> seedPhrase = enterSeedPhraseMaV.getModel().getSeedPhrase();

    // Attempt to parse the timestamp if present
    boolean timestampIsValid = false;
    if (Strings.isNullOrEmpty(timestamp)) {
      // User has not entered any timestamp so assume it's lost
      timestampIsValid = true;
    } else {
      try {
        Dates.parseSeedTimestamp(timestamp);
        timestampIsValid = true;
      } catch (IllegalArgumentException e) {
        // Do nothing
      }
    }

    // Perform a more comprehensive test on the seed phrase
    boolean seedPhraseIsValid = generator.isValid(seedPhrase);

    return timestampIsValid && seedPhraseIsValid;


  }

}
