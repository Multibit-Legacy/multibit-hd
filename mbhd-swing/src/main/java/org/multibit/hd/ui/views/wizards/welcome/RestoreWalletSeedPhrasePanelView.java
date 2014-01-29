package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseSize;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
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

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public RestoreWalletSeedPhrasePanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.RESTORE_WALLET_SEED_PHRASE_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);
  }

  @Override
  public void newPanelModel() {

    enterSeedPhraseMaV = Components.newEnterSeedPhraseMaV(getPanelName(),true);
    setPanelModel(enterSeedPhraseMaV.getModel().getValue());

    getWizardModel().setRestoreWalletEnterSeedPhraseModel(enterSeedPhraseMaV.getModel());

  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0,hidemode 1", // Layout constraints
      "[]", // Column constraints
      "[][]" // Row constraints
    ));

    panel.add(Panels.newRestoreFromSeedPhrase(), "wrap");
    panel.add(enterSeedPhraseMaV.getView().newComponentPanel(), "wrap");

    return panel;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable

    // Enable the "next" button if the seed phrase has a valid size
    boolean seedPhraseSizeValid = SeedPhraseSize.isValid(enterSeedPhraseMaV.getModel().getValue().size());

    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      seedPhraseSizeValid
    );

  }
}
