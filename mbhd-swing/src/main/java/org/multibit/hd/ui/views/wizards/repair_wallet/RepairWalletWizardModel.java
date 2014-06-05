package org.multibit.hd.ui.views.wizards.repair_wallet;

import com.google.bitcoin.core.Wallet;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.*;
import org.joda.time.DateTime;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.SlowTransactionSeenEvent;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.SSLManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.ViewKey;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <p>Model object to provide the following to "repair wallet" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class RepairWalletWizardModel extends AbstractWizardModel<RepairWalletState> {

  /**
   * Repair wallet requires a separate executor
   */
  private final ListeningExecutorService replayExecutorService = SafeExecutors.newSingleThreadExecutor("repair-wallet");

  /**
   * @param state The state object
   */
  public RepairWalletWizardModel(RepairWalletState state) {
    super(state);
  }

  @Override
  public void showNext() {

    switch (state) {
      case REPAIR_WALLET:
        state = RepairWalletState.REPAIR_WALLET_REPORT;
        break;
    }
  }


  /**
   * Reset the transactions of the current wallet and resynchronize with the block chain
   */
  protected void resetWalletAndResync() {

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

    // TODO Consider a deferred hide or separate report page
    resetWalletAndResync();

    Optional<WalletSummary> currentWalletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

    if (currentWalletSummaryOptional.isPresent()) {

      WalletSummary currentWalletSummary = currentWalletSummaryOptional.get();
      Wallet currentWallet = currentWalletSummary.getWallet();

      // Work out the replay date
      final DateTime replayDate = new DateTime(currentWallet.getEarliestKeyCreationTime() * 1000);

      // Hide the header view
      ViewEvents.fireViewChangedEvent(ViewKey.HEADER, false);

      // Allow time the UI to update
      Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

      // Clear all the transactions
      currentWallet.clearTransactions(0);

      // Fire a notification that 'a slow transaction has been seen' which will refresh anything listening for transactions
      CoreServices.uiEventBus.post(new SlowTransactionSeenEvent());

      // Create a wallet service
      CoreServices.getOrCreateWalletService(currentWalletSummary.getWalletId());

      // Start the Bitcoin network synchronization operation
      ListenableFuture future = replayExecutorService.submit(new Callable<Boolean>() {

        @Override
        public Boolean call() throws Exception {

          CoreServices.getOrCreateBitcoinNetworkService().replayWallet(replayDate);
          return true;
        }

      });
      Futures.addCallback(future, new FutureCallback() {
        @Override
        public void onSuccess(@Nullable Object result) {

          // Show the header view
          ViewEvents.fireViewChangedEvent(ViewKey.HEADER, true);

        }

        @Override
        public void onFailure(Throwable t) {
          // TODO Update the UI showing failure

          // Show the header view
          ViewEvents.fireViewChangedEvent(ViewKey.HEADER, true);
        }
      });

    }
  }

}
