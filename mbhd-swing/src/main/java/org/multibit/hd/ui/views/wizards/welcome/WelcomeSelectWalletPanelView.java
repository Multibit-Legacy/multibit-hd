package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Select how wallet is to be accessed (create/restore/hardware/switch)</li>
 * </ul>
 *
 * @since 0.0.1
 */

public class WelcomeSelectWalletPanelView extends AbstractWizardPanelView<WelcomeWizardModel, WelcomeWizardState> implements ActionListener {

  // Model
  private WelcomeWizardState currentSelection;

  private final boolean isTrezorHardWallet;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public WelcomeSelectWalletPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SELECT_WALLET_TITLE, AwesomeIcon.MAGIC);

    isTrezorHardWallet = WelcomeWizardMode.TREZOR.equals(getWizardModel().getMode());
  }

  @Override
  public void newPanelModel() {

    // Determine the initial selection
    if (isTrezorHardWallet) {
      currentSelection = TREZOR_CREATE_WALLET_PREPARATION;
    } else {
      currentSelection = CREATE_WALLET_PREPARATION;
    }

    setPanelModel(currentSelection);

    // Bind this to the wizard model
    getWizardModel().setSelectWalletChoice(currentSelection);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]" // Row constraints
      ));

    if (isTrezorHardWallet) {
      contentPanel.add(
        Panels.newHardwareWalletSelector(
          getWizardModel().getMode(),
          this,
          currentSelection.name(), // Relies on create being default
          WELCOME_SELECT_WALLET.name(), // Triggers a transition to credentials
          RESTORE_WALLET_SEED_PHRASE.name() // Triggers a transition to backups
        ), "wrap");
    } else {
      contentPanel.add(
        Panels.newWalletSelector(
          getWizardModel().getMode(),
          this,
          currentSelection.name(), // Relies on create being default
          WELCOME_SELECT_WALLET.name(), // Triggers a transition to credentials
          RESTORE_PASSWORD_SEED_PHRASE.name(),
          RESTORE_WALLET_SEED_PHRASE.name()
        ), "wrap");
    }
  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    if (wizard.getWizardModel().isRestoring()) {
      // Do not allow a return to the credentials wizard
      // The logic is too complex to justify the operation
      // It is much easier to just have the user either
      // "exit and restart" or "use existing wallet" instead
      PanelDecorator.addExitCancelNext(this, wizard);
    } else {
      PanelDecorator.addExitCancelPreviousNext(this, wizard);
    }

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the "next" button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public boolean beforeShow() {

    return true;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Next has been clicked

    setPanelModel(currentSelection);

    // Bind this to the wizard model
    getWizardModel().setSelectWalletChoice(currentSelection);

    // The welcome wizard overrides the Next button behaviour to simulate a Finish
    // if this is a "use existing wallet" selection

  }

  /**
   * <p>Handle the "select wallet" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JRadioButton source = (JRadioButton) e.getSource();

    currentSelection = WelcomeWizardState.valueOf(source.getActionCommand());

  }
}
