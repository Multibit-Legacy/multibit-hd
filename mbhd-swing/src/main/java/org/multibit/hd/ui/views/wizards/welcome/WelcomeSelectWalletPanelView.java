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
 *  
 */

public class WelcomeSelectWalletPanelView extends AbstractWizardPanelView<WelcomeWizardModel, WelcomeWizardState> implements ActionListener {

  // Model
  private WelcomeWizardState currentSelection;

  // View components

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public WelcomeSelectWalletPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SELECT_WALLET_TITLE, AwesomeIcon.ELLIPSIS_V);

  }

  @Override
  public void newPanelModel() {

    currentSelection = CREATE_WALLET_SELECT_BACKUP_LOCATION;
    setPanelModel(currentSelection);

    // Bind this to the wizard model
    getWizardModel().setSelectWalletChoice(currentSelection);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]" // Row constraints
    ));

    contentPanel.add(Panels.newWalletSelector(
      this,
      CREATE_WALLET_SELECT_BACKUP_LOCATION.name(),
      RESTORE_PASSWORD_SEED_PHRASE.name(),
      RESTORE_WALLET_SEED_PHRASE.name(),
      SELECT_WALLET_HARDWARE.name()
    ), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    if (wizard.getWizardModel().isRestoring()) {
      // Do not allow a return to the password wizard
      // The logic is too complex to justify the operation
      // It is much easier to just have the user Exit and restart instead
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

    setPanelModel(currentSelection);

    // Bind this to the wizard model
    getWizardModel().setSelectWalletChoice(currentSelection);

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
