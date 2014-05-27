package org.multibit.hd.ui.views.wizards.repair_wallet;

import com.google.bitcoin.core.Wallet;
import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.SSLManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Repair wallet: Show</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RepairWalletPanelView extends AbstractWizardPanelView<RepairWalletWizardModel, String> {

  // View components
  //private ModelAndView<WalletDetailModel, WalletDetailView> walletDetailMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public RepairWalletPanelView(AbstractWizard<RepairWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.REPAIR_WALLET_TITLE, AwesomeIcon.MEDKIT);

  }

  @Override
  public void newPanelModel() {

    setPanelModel("");

    // No wizard model
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    //walletDetailMaV = Components.newWalletDetailMaV(getPanelName());

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]" // Row constraints
    ));

    contentPanel.add(Labels.newRepairWalletNote());

  }

  @Override
  protected void initialiseButtons(AbstractWizard<RepairWalletWizardModel> wizard) {

    PanelDecorator.addCancelFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getFinishButton().requestFocusInWindow();
      }
    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    // Always call super() before hiding
    super.beforeHide(isExitCancel);

    if (!isExitCancel) {

      // Attempt to fix any SSL problems first
      try {
        SSLManager.INSTANCE.installMultiBitSSLCertificate(
          InstallationManager.getOrCreateApplicationDataDirectory(),
          InstallationManager.CA_CERTS_NAME,
          true
        );
      } catch (Exception e) {
        // TODO - put on UI
        ExceptionHandler.handleThrowable(e);
      }

      // TODO Move this off the EDT and into a ListenableFuture
      resetWalletAndResync();
    }

    return true;

  }

  /**
   * Reset the transactions of the current wallet and resync
   */
  private void resetWalletAndResync() {

    Optional<WalletSummary> currentWalletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

    if (currentWalletSummaryOptional.isPresent()) {

      WalletSummary currentWalletSummary = currentWalletSummaryOptional.get();
      Wallet currentWallet = currentWalletSummary.getWallet();

      // Work out the replay date
      DateTime replayDate = new DateTime(currentWallet.getEarliestKeyCreationTime() * 1000);

      // Clear all the transactions
      currentWallet.clearTransactions(0);

      // Fire a notification that 'a slow transaction has been seen' which will refresh anything listening for transactions
      CoreServices.uiEventBus.post(new SlowTransactionSeenEvent());

      // Create a wallet service
      CoreServices.getOrCreateWalletService(currentWalletSummary.getWalletId());

      // Start the Bitcoin network to synchronize
      try {
        CoreServices.getOrCreateBitcoinNetworkService().replayWallet(replayDate);

      } catch (Exception e) {
        // TODO - put on UI
        ExceptionHandler.handleThrowable(e);
      }

    }
  }


  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

}