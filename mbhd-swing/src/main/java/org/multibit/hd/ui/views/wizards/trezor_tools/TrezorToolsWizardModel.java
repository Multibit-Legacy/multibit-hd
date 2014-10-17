package org.multibit.hd.ui.views.wizards.trezor_tools;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.*;
import org.bitcoinj.core.Wallet;
import org.joda.time.DateTime;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.ViewKey;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <p>Model object to provide the following to "Trezor tools" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class TrezorToolsWizardModel extends AbstractWizardModel<TrezorToolsState> {

  /**
   * Each Trezor tool runs in a separate executor
   */
  private final ListeningExecutorService walletExecutorService = SafeExecutors.newSingleThreadExecutor("repair-wallet");

  private Optional<Boolean> walletRepaired = Optional.absent();

  /**
   * @param state The state object
   */
  public TrezorToolsWizardModel(TrezorToolsState state) {
    super(state);
  }

  @Override
  public void showNext() {
    switch (state) {
      case SELECT_TREZOR_ACTION:
        state = TrezorToolsState.VERIFY_DEVICE;
        break;
      case VERIFY_DEVICE:
        state = TrezorToolsState.TREZOR_ACTION_REPORT;
        break;
      case WIPE_DEVICE:
        state = TrezorToolsState.TREZOR_ACTION_REPORT;
        break;
      default:
        throw new IllegalStateException("Cannot showNext with a state of " + state);
    }
  }

  /**
   * @return True if the wallet has been repaired, absent if still progressing
   */
  public Optional<Boolean> isWalletRepaired() {
    return walletRepaired;
  }


  /**
   * <p>Reset the transactions of the current wallet and resynchronize with the block chain</p>
   * <p>Reduced visibility for panel view</p>
   */
  void repairWallet() {

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

      // Clear all the transactions back to the genesis block
      currentWallet.clearTransactions(0);

      // Ensure we have a wallet service in place
      CoreServices.getOrCreateWalletService(currentWalletSummary.getWalletId());

      // Start the Bitcoin network synchronization operation
      ListenableFuture future = walletExecutorService.submit(new Callable<Boolean>() {

        @Override
        public Boolean call() throws Exception {

          CoreServices.getOrCreateBitcoinNetworkService().replayWallet(replayDate);
          return true;

        }

      });
      Futures.addCallback(future, new FutureCallback() {
        @Override
        public void onSuccess(@Nullable Object result) {

          // Do nothing this just means that the block chain download has begun

        }

        @Override
        public void onFailure(Throwable t) {

          // Have a failure
          walletRepaired = Optional.of(Boolean.FALSE);
          ViewEvents.fireComponentChangedEvent(getPanelName(), Optional.of(this));

        }
      });

    }
  }
}
