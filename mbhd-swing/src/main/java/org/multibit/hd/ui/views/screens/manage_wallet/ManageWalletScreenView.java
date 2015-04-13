package org.multibit.hd.ui.views.screens.manage_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.dto.WalletType;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletState;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the "manage wallet" detail display</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ManageWalletScreenView extends AbstractScreenView<ManageWalletScreenModel> {

  private static final Logger log = LoggerFactory.getLogger(ManageWalletScreenView.class);

  private JButton showEmptyWalletButton;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public ManageWalletScreenView(ManageWalletScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {
    MigLayout layout = new MigLayout(
      Panels.migXYDetailLayout(),
      "6[]6[]6[]6[]6", // Column constraints
      "6[]6[]6" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    JButton primaryButton = Buttons.newShowEditWalletButton(getShowEditWalletAction());

    showEmptyWalletButton = Buttons.newShowEmptyWalletButton(getShowEmptyWalletAction());

    // Initially show the button disabled - it is enabled when the network is synced
    showEmptyWalletButton.setEnabled(false);

    // Row 1
    contentPanel.add(primaryButton, MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newShowWalletDetailsButton(getShowWalletDetailsAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(showEmptyWalletButton, MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    // Repair is in top right for good visibility
    contentPanel.add(Buttons.newShowRepairWalletButton(getShowRepairWalletAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push,wrap");

    // Row 2
    contentPanel.add(Buttons.newShowHistoryScreenButton(getShowHistoryAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    WalletType walletType = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletType();

    if (WalletType.TREZOR_HARD_WALLET.equals(walletType)) {
      contentPanel.add(Buttons.newShowChangePinButton(getShowChangePinAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    } else {
      contentPanel.add(Buttons.newShowChangePasswordButton(getShowChangePasswordAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    }
    contentPanel.add(Buttons.newShowPaymentSettingsWizardButton(getShowPaymentSettingsAction()), MultiBitUI.LARGE_BUTTON_MIG + ",align center,push,wrap");

    setInitialised(true);

    return contentPanel;
  }

  @Override
   public boolean beforeShow() {
     // Ensure the empty wallet button is kept up to date
     Optional<BitcoinNetworkChangedEvent> changedEvent = CoreServices.getApplicationEventService().getLatestBitcoinNetworkChangedEvent();
     if (changedEvent.isPresent()) {
       updateEmptyButton(changedEvent.get());
     }
    return true;
  }

  @Override
  public void afterShow() {
    // Ensure the empty wallet button is kept up to date
    Optional<BitcoinNetworkChangedEvent> changedEvent = CoreServices.getApplicationEventService().getLatestBitcoinNetworkChangedEvent();
    if (changedEvent.isPresent()) {
      updateEmptyButton(changedEvent.get());
    }
  }

  /**
   * @param event The "Bitcoin network changed" event - one per block downloaded during synchronization
   */
  @Subscribe
  public void onBitcoinNetworkChangeEvent(final BitcoinNetworkChangedEvent event) {
    if (!isInitialised()) {
      return;
    }

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

    BitcoinNetworkSummary summary = event.getSummary();

    Preconditions.checkNotNull(summary.getSeverity(), "'severity' must be present");

    // Keep the UI response to a minimum due to the volume of these events
    updateEmptyButton(event);
  }

  /**
   * @return An action to show the "edit wallet" tool
   */
  private AbstractAction getShowEditWalletAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newEditWalletWizard().getWizardScreenHolder());
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

        Panels.showLightBox(Wizards.newWalletDetailsWizard().getWizardScreenHolder());
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
   * @return An action to show the "change PIN" tool
   */
  private AbstractAction getShowChangePinAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newChangePinWizard().getWizardScreenHolder());
      }
    };
  }

  /**
   * @return An action to show the "history" screen
   */
  private AbstractAction getShowHistoryAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Configurations.currentConfiguration.getAppearance().setCurrentScreen(Screen.HISTORY.name());
        ViewEvents.fireShowDetailScreenEvent(Screen.HISTORY);
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
   * @return An action to show the "payment settings" tool
   */
  private AbstractAction getShowPaymentSettingsAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newPaymentSettingsWizard().getWizardScreenHolder());
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

      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
      final File currentWalletSummaryFile = WalletManager.INSTANCE.getCurrentWalletSummaryFile(applicationDataDirectory).get();
      WalletManager.updateWalletSummary(currentWalletSummaryFile, walletSummary);
    }
  }

  /**
   * <p>Ensure that a wallet can only be emptied once synchronization has completed</p>
   *
   * @param event The "Bitcoin network changed" event - one per block downloaded during synchronization
   */
  private void updateEmptyButton(BitcoinNetworkChangedEvent event) {
    boolean newEnabled;
    boolean canChange = true;

    // NOTE: Show empty wallet is disabled when the network is not available
    // because it is possible that a second wallet is generating transactions using
    // addresses that this one has not displayed yet. This would lead to the same
    // address being used twice.
    switch (event.getSummary().getSeverity()) {
      case RED:
        // Enable on RED only if unrestricted (allows FEST tests without a network)
        newEnabled = InstallationManager.unrestricted;
        log.trace("Severity = red");
        break;
      case AMBER:
        // Enable on AMBER only if unrestricted
        newEnabled = InstallationManager.unrestricted;
        log.trace("Severity = AMBER, newEnabled = " + newEnabled);
        break;
      case GREEN:
        // Enable on GREEN
        newEnabled = true;
        log.trace("Severity = GREEN, newEnabled = " + true);
        break;
      case PINK:
      case EMPTY:
        // Maintain the status quo
        newEnabled = showEmptyWalletButton.isEnabled();
        canChange = false;
        break;
      default:
        // Unknown status
        throw new IllegalStateException("Unknown event severity " + event.getSummary().getSeverity());
    }

    log.trace("BitcoinNetworkChangedEvent = {}, showEmptyWalletButton.isEnabled() = {}, newEnabled = {}, canChange = {}", event, showEmptyWalletButton.isEnabled(), newEnabled, canChange);

    if (canChange) {
      final boolean finalNewEnabled = newEnabled;

      // If button is not enabled and the newEnabled is false don't do anything
      if (showEmptyWalletButton.isEnabled() || newEnabled) {
        SwingUtilities.invokeLater(
                new Runnable() {
                  @Override
                  public void run() {
                    log.debug("Changing button enable state, newEnabled = " + finalNewEnabled);
                    showEmptyWalletButton.setEnabled(finalNewEnabled);
                  }
                });
      }
    }
  }
}
