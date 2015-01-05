package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.events.WalletLoadEvent;
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

    super(wizard, panelName, MessageKey.LOAD_WALLET_REPORT_TITLE, AwesomeIcon.FILE_TEXT);

  }

  @Override
  public void newPanelModel() {

    String model = "TODO replace with a proper model";
    setPanelModel(model);

    // No need to bind this to the wizard model
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
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

    PanelDecorator.addExitRestoreFinish(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Disable the finish button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

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

                getFinishButton().requestFocusInWindow();

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
                if (isInitialised()) {
                  unprocessedWalletLoadEvent = null;
                  if (walletLoadEvent.isWalletLoadWasSuccessful()) {
                    // Wallet loaded ok
                    loadedOk = true;
                    LabelDecorator.applyWrappingLabel(walletLoadedStatusLabel, Languages.safeText(CoreMessageKey.WALLET_LOADED_OK));
                    LabelDecorator.applyStatusLabel(walletLoadedStatusLabel, Optional.of(Boolean.TRUE));
                  } else {
                    // Wallet failed to load
                    LabelDecorator.applyWrappingLabel(walletLoadedStatusLabel, Languages.safeText(CoreMessageKey.WALLET_FAILED_TO_LOAD));
                    LabelDecorator.applyStatusLabel(walletLoadedStatusLabel, Optional.of(Boolean.FALSE));
                  }
                  if (!connected) {
                    connectedStatusLabel.setVisible(true);
                    LabelDecorator.applyWrappingLabel(connectedStatusLabel, Languages.safeText(CoreMessageKey.CONNECTING_TO_BITCOIN_NETWORK));
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

                log.debug("Saw a Bitcoin network changed event {}", bitcoinNetworkChangedEvent);
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
                      LabelDecorator.applyStatusLabel(walletIsReadyToUseStatusLabel, Optional.of(Boolean.TRUE));                    }
                    break;

                  default:


                }
//                 if (summary.getPeerCount().isPresent()) {
//                   // Peer count information
//                   if (summary.getPeerCount().get() > 0) {
//                     // Connected
//                     connected = true;
//                     connectedStatusLabel.setVisible(true);
//                     LabelDecorator.applyWrappingLabel(connectedStatusLabel, Languages.safeText(CoreMessageKey.CONNECTED_TO_BITCOIN_NETWORK));
//                     LabelDecorator.applyStatusLabel(connectedStatusLabel, Optional.of(Boolean.TRUE));
//
//                     if (!startedSync) {
//                       synchronisationStatusLabel.setVisible(true);
//                       LabelDecorator.applyWrappingLabel(synchronisationStatusLabel, Languages.safeText(CoreMessageKey.PREPARING_TO_SYNCHRONISE));
//                     }
//                   }
//                 }
//                 if (summary.getBlocksLeft() > -1) {
//                   // block information
//                   synchronisationStatusLabel.setVisible(true);
//                   if (summary.getBlocksLeft() == 0) {
//                     // Synchronised
//                     LabelDecorator.applyWrappingLabel(synchronisationStatusLabel, Languages.safeText(CoreMessageKey.SYNCHRONISED));
//                   } else {
//                     // Synchronising
//                     startedSync = true;
//                     LabelDecorator.applyWrappingLabel(synchronisationStatusLabel, Languages.safeText(CoreMessageKey.SYNCHRONISING));
//                   }
//                 }
              }
            });
  }
}
