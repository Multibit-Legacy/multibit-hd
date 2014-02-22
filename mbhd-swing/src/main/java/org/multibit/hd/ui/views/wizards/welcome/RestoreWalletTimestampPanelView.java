package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.panels.BackgroundPanel;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordModel;
import org.multibit.hd.ui.views.components.enter_password.EnterPasswordView;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Restore wallet from seed phrase with optional timestamp</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class RestoreWalletTimestampPanelView extends AbstractWizardPanelView<WelcomeWizardModel, RestoreWalletTimestampPanelModel> {

  private ModelAndView<EnterSeedPhraseModel, EnterSeedPhraseView> enterSeedPhraseMaV;
  private ModelAndView<EnterPasswordModel, EnterPasswordView> enterPasswordMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public RestoreWalletTimestampPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.RESTORE_WALLET_TIMESTAMP_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);
  }

  @Override
  public void newPanelModel() {

    // Do not ask for seed phrase (we already have it)
    enterSeedPhraseMaV = Components.newEnterSeedPhraseMaV(getPanelName(), true, false);
    enterPasswordMaV = Components.newEnterPasswordMaV(getPanelName());

    // Create a panel model for the information
    RestoreWalletTimestampPanelModel panelModel = new RestoreWalletTimestampPanelModel(
      getPanelName(),
      enterSeedPhraseMaV.getModel(),
      enterPasswordMaV.getModel()
    );
    setPanelModel(panelModel);

    getWizardModel().setRestoreWalletEnterTimestampModel(enterSeedPhraseMaV.getModel());
    getWizardModel().setRestoreWalletEnterPasswordModel(enterPasswordMaV.getModel());

  }

  @Override
  public JPanel newWizardViewPanel() {

    BackgroundPanel panel = Panels.newDetailBackgroundPanel(AwesomeIcon.MAGIC);

    panel.setLayout(new MigLayout(
      "fill,insets 0,hidemode 1", // Layout constraints
      "[]", // Column constraints
      "[][]" // Row constraints
    ));

    panel.add(Panels.newRestoreFromTimestamp(), "wrap");
    panel.add(enterSeedPhraseMaV.getView().newComponentPanel(), "wrap");
    panel.add(enterPasswordMaV.getView().newComponentPanel(), "wrap");

    return panel;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        enterSeedPhraseMaV.getView().requestInitialFocus();
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable

    // Enable the "next" button if the seed phrase has a valid size
    boolean timestampIsValid = false;
    try {
      Dates.parseSeedTimestamp(enterSeedPhraseMaV.getModel().getSeedTimestamp());
      timestampIsValid = true;
    } catch (IllegalArgumentException e) {
      // Do nothing
    }

    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      timestampIsValid
    );
    ViewEvents.fireVerificationStatusChangedEvent(
      getPanelName(),
      timestampIsValid
    );

  }
}
