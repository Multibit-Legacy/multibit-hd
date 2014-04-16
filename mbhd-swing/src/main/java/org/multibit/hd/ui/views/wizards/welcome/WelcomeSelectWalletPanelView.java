package org.multibit.hd.ui.views.wizards.welcome;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.select_wallet.SelectWalletModel;
import org.multibit.hd.ui.views.components.select_wallet.SelectWalletView;
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
  private ModelAndView<SelectWalletModel, SelectWalletView> selectWalletMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public WelcomeSelectWalletPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.SELECT_WALLET_TITLE, AwesomeIcon.BITCOIN);

  }

  @Override
  public void newPanelModel() {

    selectWalletMaV = Components.newSelectWalletMaV(getPanelName());

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
      RESTORE_WALLET_SEED_PHRASE.name(),
      SELECT_WALLET_HARDWARE.name(),
      SELECT_WALLET_SWITCH.name()
    ), "wrap");

    contentPanel.add(selectWalletMaV.getView().newComponentPanel(),"growx,wrap");

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
  public boolean beforeShow() {

    // Update the backup list with data from the wizard model
    selectWalletMaV.getModel().setWalletList(getWizardModel().getWalletList());
    selectWalletMaV.getView().updateViewFromModel();

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

    if (SELECT_WALLET_SWITCH.equals(currentSelection)) {

      // Enable the select wallet component
      selectWalletMaV.getView().setEnabled(true);

    } else {

      // Disable the select wallet component
      selectWalletMaV.getView().setEnabled(false);

    }

  }
}
