package org.multibit.hd.ui.views.wizards.welcome.restore_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordModel;
import org.multibit.hd.ui.views.components.confirm_password.ConfirmPasswordView;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseModel;
import org.multibit.hd.ui.views.components.enter_seed_phrase.EnterSeedPhraseView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;

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
  private ModelAndView<ConfirmPasswordModel, ConfirmPasswordView> confirmPasswordMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public RestoreWalletTimestampPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.MAGIC, MessageKey.RESTORE_WALLET_TIMESTAMP_TITLE, null);

  }

  @Override
  public void newPanelModel() {

    // Do not ask for seed phrase (we already have it)
    enterSeedPhraseMaV = Components.newEnterSeedPhraseMaV(getPanelName(), true, false);
    confirmPasswordMaV = Components.newConfirmPasswordMaV(getPanelName());

    // Create a panel model for the information
    RestoreWalletTimestampPanelModel panelModel = new RestoreWalletTimestampPanelModel(
      getPanelName(),
      enterSeedPhraseMaV.getModel(),
      confirmPasswordMaV.getModel()
    );
    setPanelModel(panelModel);

    getWizardModel().setRestoreWalletEnterTimestampModel(enterSeedPhraseMaV.getModel());
    getWizardModel().setRestoreWalletConfirmPasswordModel(confirmPasswordMaV.getModel());

    // Register components
    registerComponents(confirmPasswordMaV, enterSeedPhraseMaV);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migLayout("fill,insets 0,hidemode 1"),
      "[]", // Column constraints
      "[][][][]" // Row constraints
    ));

    // Add to the panel
    contentPanel.add(Labels.newRestoreFromTimestampNote(), "grow,push,wrap");
    contentPanel.add(enterSeedPhraseMaV.getView().newComponentPanel(), "wrap");

    contentPanel.add(Labels.newRestorePasswordNote(), "grow,push,wrap");
    contentPanel.add(confirmPasswordMaV.getView().newComponentPanel(), "wrap");

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

    // No need to update the wizard it has the references

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      isNextEnabled()
    );

  }

  /**
   * A blank or empty timestamp is valid for panel navigation but does not show a 'Verified' text
   * @return True if the "next" button should be enabled
   */
  private boolean isNextEnabled() {

    boolean isPasswordValid = confirmPasswordMaV.getModel().comparePasswords();

    String timestamp = enterSeedPhraseMaV.getModel().getSeedTimestamp();

    // Is the timestamp present ? (i.e. some text has been entered)
    boolean isTimestampPresent = !Strings.isNullOrEmpty(timestamp);

    // Work out if timestamp is valid
    boolean isTimestampValid = false;
    try {
      if (timestamp != null) {
        Dates.parseSeedTimestamp(timestamp);
        isTimestampValid = true;
      }
    } catch (IllegalArgumentException e) {
      // Do nothing
    }

    final boolean finalIsTimestampPresentAndValid = isTimestampPresent && isTimestampValid;

    // Fire the "timestamp verified" event
    ViewEvents.fireVerificationStatusChangedEvent(getPanelName() + ".timestamp", finalIsTimestampPresentAndValid);

    // Next is enabled if (the timestamp is missing or is valid) and the password is valid
    return (!isTimestampPresent || isTimestampValid) && isPasswordValid;

  }

}
