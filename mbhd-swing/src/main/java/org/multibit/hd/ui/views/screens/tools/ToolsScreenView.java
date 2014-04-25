package org.multibit.hd.ui.views.screens.tools;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletState;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the tools detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ToolsScreenView extends AbstractScreenView<ToolsScreenModel> {

  private JButton primaryButton;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public ToolsScreenView(ToolsScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      Panels.migXYLayout(),
      "[]10[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    primaryButton = Buttons.newShowEditWalletButton(getShowWalletDetailsAction());

    contentPanel.add(primaryButton, MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowChangePasswordButton(getShowChangePasswordAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push,wrap");

    contentPanel.add(Buttons.newShowRepairWalletButton(getShowRepairWalletAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowEmptyWalletButton(getShowEmptyWalletAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push,wrap");

    contentPanel.add(Buttons.newShowVerifyNetworkButton(getShowVerifyNetworkAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowWelcomeWizardButton(getShowWelcomeWizardAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push,wrap");

    return contentPanel;
  }

  /**
   * @return An action to show the "welcome wizard"
   */
  private AbstractAction getShowWelcomeWizardAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newClosingWelcomeWizard(WelcomeWizardState.WELCOME_SELECT_LANGUAGE).getWizardScreenHolder());
      }
    };
  }

  /**
   * @return An action to show the "wallet details" tool
   */
  private AbstractAction getShowWalletDetailsAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newEditWalletWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * @return An action to show the "change password" tool
   */
  private AbstractAction getShowChangePasswordAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newChangePasswordWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * @return An action to show the "verify network" tool
   */
  private AbstractAction getShowVerifyNetworkAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newVerifyNetworkWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * @return An action to show the "repair wallet" tool
   */
  private AbstractAction getShowRepairWalletAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newRepairWalletWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * @return An action to show the "empty wallet" tool
   */
  private AbstractAction getShowEmptyWalletAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newEmptyWalletWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * <p>Handle the transfer of data from a closing wizard</p>
   *
   * @param event The "wizard hide" event
   */
  @Subscribe
  public void onWizardHideEvent(WizardHideEvent event) {

    // Filter other events
    if (event.isExitCancel()) {
      return;
    }
    if (event.getPanelName().equals(EditWalletState.EDIT_WALLET.name())) {

      // Persist the data from the wizard model into wallet
      WalletSummary walletSummary = ((EditWalletWizardModel) event.getWizardModel()).getWalletSummary();

      // getScreenModel().getWalletService().updateWalletSummary(walletSummary);
      WalletManager.updateWalletSummary(walletSummary);

    }


  }


}
