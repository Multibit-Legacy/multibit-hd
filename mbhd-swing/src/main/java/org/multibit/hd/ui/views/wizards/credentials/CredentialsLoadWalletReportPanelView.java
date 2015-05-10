package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.events.WalletLoadEvent;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to load a wallet</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class CredentialsLoadWalletReportPanelView extends AbstractWizardPanelView<CredentialsWizardModel, String> {

  private static final Logger log = LoggerFactory.getLogger(CredentialsLoadWalletReportPanelView.class);

  // The status of whether the wallet loaded ok
  private JLabel walletLoadedStatusLabel;
  private boolean loadedOk = false;

  // The status of whether you are connected to the bitcoin network (i.e. at least one peer)
  private JLabel connectedStatusLabel;

  private boolean connected = false;

  // The status of synchronisation
  private JLabel synchronisationStatusLabel;

  // Alabel indicating that the wallet is ready to use
  private JLabel walletIsReadyToUseStatusLabel;

  private boolean startedSync = false;

  private WalletLoadEvent unprocessedWalletLoadEvent;


  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public CredentialsLoadWalletReportPanelView(AbstractWizard<CredentialsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.LOAD_WALLET_REPORT_TITLE, AwesomeIcon.SUN_O);

  }

  @Override
  public void newPanelModel() {

    String model = "TODO replace with a proper model";
    setPanelModel(model);

    // No need to bind this to the wizard model
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
            new MigLayout(
                    Panels.migXYLayout(),
                    "[][][]", // Column constraints
                    "[20]10[20]10[20]10[20]10" // Row constraints
            ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Initialise wallet load status to loading
    walletLoadedStatusLabel = Labels.newCoreStatusLabel(Optional.of(CoreMessageKey.WALLET_LOADING), new Object[0], Optional.<Boolean>absent());
    walletLoadedStatusLabel.setVisible(true);

    // Initialise connected status to invisible
    connectedStatusLabel = Labels.newCoreStatusLabel(Optional.<CoreMessageKey>absent(), new Object[0], Optional.<Boolean>absent());
    connectedStatusLabel.setVisible(false);

    // Initialise synchronisation status to invisible
    synchronisationStatusLabel = Labels.newCoreStatusLabel(Optional.<CoreMessageKey>absent(), new Object[0], Optional.<Boolean>absent());
    synchronisationStatusLabel.setVisible(false);

    // Initialise wallet is ready to use status to invisible
    walletIsReadyToUseStatusLabel = Labels.newCoreStatusLabel(Optional.<CoreMessageKey>absent(), new Object[0], Optional.<Boolean>absent());
    walletIsReadyToUseStatusLabel.setVisible(false);

    contentPanel.add(walletLoadedStatusLabel, "wrap");
    contentPanel.add(connectedStatusLabel, "wrap");
    contentPanel.add(synchronisationStatusLabel, "wrap");
    contentPanel.add(walletIsReadyToUseStatusLabel, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<CredentialsWizardModel> wizard) {

    Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();
    if (hardwareWalletService.isPresent() && hardwareWalletService.get().getContext().getFeatures().isPresent()) {
      // The hardware wallet is connected so we are guaranteed to succeed
      PanelDecorator.addExitFinish(this, wizard);
    } else {
      // There may be an issue so include Previous as an escape
      PanelDecorator.addExitPreviousFinish(this, wizard);
    }

    if (getPreviousButton() != null) {
      getPreviousButton().addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Clear any 'password failed' before moving back
          walletLoadedStatusLabel.setText("");
          walletLoadedStatusLabel.setIcon(null);
        }
      });
    }
  }

  @Override
  public void fireInitialStateViewEvents() {
    // Disable the previous button - it is enabled once the wallet fails loads
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.PREVIOUS, false);

    // Disable the finish button - it is enabled once the wallet loads
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, false);
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Override
  public boolean beforeShow() {
    return true;
  }

  @Override
  public void afterShow() {
    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                registerDefaultButton(getFinishButton());

                if (unprocessedWalletLoadEvent != null) {
                  onWalletLoadEvent(unprocessedWalletLoadEvent);
                }
              }
            });
  }

  @Subscribe
  public void onWalletLoadEvent(final WalletLoadEvent walletLoadEvent) {
    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                log.debug("Saw a wallet load event {}", walletLoadEvent);

                // Ensure the wallet balance is propagated out
                if (WalletManager.INSTANCE.getCurrentWalletBalance().isPresent()) {
                  ViewEvents.fireBalanceChangedEvent(
                          WalletManager.INSTANCE.getCurrentWalletBalance().get(),
                          WalletManager.INSTANCE.getCurrentWalletBalanceWithUnconfirmed().get(),
                          null,
                          Optional.<String>absent());
                }

                if (isInitialised()) {
                  unprocessedWalletLoadEvent = null;
                  if (walletLoadEvent.isWalletLoadWasSuccessful() || walletLoadEvent.getBackupLoaded().isPresent()) {
                    // Wallet loaded ok or rolling backup loaded
                    loadedOk = true;

                    if (walletLoadEvent.getBackupLoaded().isPresent()) {
                      // Indicate backup wallet was loaded with a cross
                      LabelDecorator.applyWrappingLabel(walletLoadedStatusLabel, Languages.safeText(CoreMessageKey.BACKUP_WALLET_WAS_LOADED));
                      LabelDecorator.applyStatusLabel(walletLoadedStatusLabel, Optional.of(Boolean.FALSE));
                    } else {
                      LabelDecorator.applyWrappingLabel(walletLoadedStatusLabel, Languages.safeText(CoreMessageKey.WALLET_LOADED_OK));
                      LabelDecorator.applyStatusLabel(walletLoadedStatusLabel, Optional.of(Boolean.TRUE));
                    }

                    // Enable the finish button
                    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

                    // Disable previous button - this is only there to enable bad password recovery
                    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.PREVIOUS, false);

                    // Indicate connecting
                    if (!connected) {
                      connectedStatusLabel.setVisible(true);
                      LabelDecorator.applyWrappingLabel(connectedStatusLabel, Languages.safeText(CoreMessageKey.CONNECTING_TO_BITCOIN_NETWORK));
                    }

                  } else {
                    // Wallet failed to load
                    if (walletLoadEvent.getWalletLoadMessageKey() != null) {
                      // Specific
                      LabelDecorator.applyWrappingLabel(walletLoadedStatusLabel, Languages.safeText(walletLoadEvent.getWalletLoadMessageKey()));
                    } else {
                      LabelDecorator.applyWrappingLabel(walletLoadedStatusLabel, Languages.safeText(CoreMessageKey.WALLET_FAILED_TO_LOAD));
                    }
                    LabelDecorator.applyStatusLabel(walletLoadedStatusLabel, Optional.of(Boolean.FALSE));

                    // Disable the finish button
                    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, false);

                    // Enable previous button
                    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.PREVIOUS, true);

                    // Set focus to previous button since it's the most likely action from here
                    getPreviousButton().requestFocusInWindow();

                  }

                } else {
                  unprocessedWalletLoadEvent = walletLoadEvent;
                }
              }
            });
  }

  @Subscribe
  public void onBitcoinChangeEvent(final BitcoinNetworkChangedEvent bitcoinNetworkChangedEvent) {

    if (!isInitialised()) {
      return;
    }
    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                // Extremely high volume
                log.trace("Saw a Bitcoin network changed event {}", bitcoinNetworkChangedEvent);
                BitcoinNetworkSummary summary = bitcoinNetworkChangedEvent.getSummary();

                switch (summary.getStatus()) {
                  case NOT_CONNECTED:
                    startedSync = false;
                    break;

                  case CONNECTING:
                    startedSync = false;
                    connectedStatusLabel.setVisible(true);
                    LabelDecorator.applyWrappingLabel(connectedStatusLabel, Languages.safeText(CoreMessageKey.CONNECTING_TO_BITCOIN_NETWORK));
                    break;

                  case CONNECTED:
                    connectedStatusLabel.setVisible(true);
                    LabelDecorator.applyWrappingLabel(connectedStatusLabel, Languages.safeText(CoreMessageKey.CONNECTED_TO_BITCOIN_NETWORK));
                    LabelDecorator.applyStatusLabel(connectedStatusLabel, Optional.of(Boolean.TRUE));

                    if (!startedSync) {
                      synchronisationStatusLabel.setVisible(true);
                      LabelDecorator.applyWrappingLabel(synchronisationStatusLabel, Languages.safeText(CoreMessageKey.PREPARING_TO_SYNCHRONISE));
                    }
                    break;

                  case DOWNLOADING_BLOCKCHAIN:
                    startedSync = true;
                    synchronisationStatusLabel.setVisible(true);
                    LabelDecorator.applyWrappingLabel(synchronisationStatusLabel, Languages.safeText(CoreMessageKey.SYNCHRONISING));
                    break;

                  case SYNCHRONIZED:
                    startedSync = true;
                    synchronisationStatusLabel.setVisible(true);
                    LabelDecorator.applyWrappingLabel(synchronisationStatusLabel, Languages.safeText(CoreMessageKey.SYNCHRONISED));
                    LabelDecorator.applyStatusLabel(synchronisationStatusLabel, Optional.of(Boolean.TRUE));

                    if (loadedOk) {
                      // Wallet is ready to use
                      walletIsReadyToUseStatusLabel.setVisible(true);
                      LabelDecorator.applyWrappingLabel(walletIsReadyToUseStatusLabel, Languages.safeText(CoreMessageKey.WALLET_IS_READY_TO_USE));
                      LabelDecorator.applyStatusLabel(walletIsReadyToUseStatusLabel, Optional.of(Boolean.TRUE));
                    }
                    break;

                  default:


                }

              }
            });
  }
}
