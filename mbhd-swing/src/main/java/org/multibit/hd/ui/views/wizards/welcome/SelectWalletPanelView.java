package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
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

public class SelectWalletPanelView extends AbstractWizardPanelView<WelcomeWizardModel, WelcomeWizardState> implements ActionListener {

  // Model
  private WelcomeWizardState currentSelection;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public SelectWalletPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.SELECT_WALLET_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void newPanelModel() {

    currentSelection = SELECT_BACKUP_LOCATION;
    setPanelModel(currentSelection);

    // Bind this to the wizard model
    getWizardModel().setSelectWalletChoice(currentSelection);
  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constraints
      "[]", // Column constraints
      "[]" // Row constraints
    ));

    panel.add(Panels.newWalletSelector(
      this,
      SELECT_BACKUP_LOCATION.name(),
      RESTORE_WALLET.name(),
      HARDWARE_WALLET.name(),
      SWITCH_WALLET.name()
    ), "wrap");

    return panel;
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the "next" button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    setPanelModel(currentSelection);


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
