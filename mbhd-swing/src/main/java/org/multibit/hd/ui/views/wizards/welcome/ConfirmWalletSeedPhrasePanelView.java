package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
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

public class ConfirmWalletSeedPhrasePanelView extends AbstractWizardPanelView<WelcomeWizardModel, List<String>> {

  private ModelAndView<EnterSeedPhraseModel, EnterSeedPhraseView> enterSeedPhraseMaV;

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public ConfirmWalletSeedPhrasePanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.CONFIRM_WALLET_SEED_PHRASE_TITLE);

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    enterSeedPhraseMaV = Components.newEnterSeedPhraseMaV(WelcomeWizardState.CONFIRM_WALLET_SEED_PHRASE.name());
    setPanelModel(enterSeedPhraseMaV.getModel().getValue());

    getWizardModel().setEnterSeedPhraseModel(enterSeedPhraseMaV.getModel());

  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0,hidemode 1", // Layout constraints
      "[]", // Column constraints
      "[][]" // Row constraints
    ));

    panel.add(Panels.newConfirmSeedPhrase(), "wrap");
    panel.add(enterSeedPhraseMaV.getView().newComponentPanel(), "wrap");

    return panel;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    List<String> actualSeedPhrase = getWizardModel().getActualSeedPhrase();
    DateTime actualSeedTimestamp = getWizardModel().getActualSeedTimestamp();

    List<String> userSeedPhrase = enterSeedPhraseMaV.getModel().getSeedPhrase();
    DateTime userSeedTimestamp = enterSeedPhraseMaV.getModel().getSeedTimestamp();

    boolean result = actualSeedPhrase.equals(userSeedPhrase) && actualSeedTimestamp.equals(userSeedTimestamp);

    // Fire the decision events (requires knowledge of the previous panel data)
    ViewEvents.fireWizardButtonEnabledEvent(CONFIRM_WALLET_SEED_PHRASE.name(), WizardButton.NEXT, result);
    ViewEvents.fireVerificationStatusChangedEvent(CONFIRM_WALLET_SEED_PHRASE.name(), result);

  }
}
