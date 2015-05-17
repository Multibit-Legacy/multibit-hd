package org.multibit.hd.ui.views.wizards.repair_wallet;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.*;
import org.bitcoinj.core.Wallet;
import org.joda.time.DateTime;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.dto.WalletType;
import org.multibit.hd.core.managers.HttpsManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.views.ViewKey;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.swing.*;
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
 */
public class RepairWalletWizardModel extends AbstractWizardModel<RepairWalletState> {

  private static final Logger log = LoggerFactory.getLogger(RepairWalletWizardModel.class);

  /**
   * Repair wallet requires a separate executor
   */
  private final ListeningExecutorService walletExecutorService = SafeExecutors.newSingleThreadExecutor("repair-wallet");
  private final ListeningExecutorService cacertsExecutorService = SafeExecutors.newSingleThreadExecutor("repair-cacerts");

  private Optional<Boolean> walletRepaired = Optional.absent();
  private Optional<Boolean> cacertsRepaired = Optional.absent();

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
      default:
        throw new IllegalStateException("Unexpected state:" + state);
    }
  }

  /**
   * @return True if the wallet has been repaired, absent if still progressing
   */
  public Optional<Boolean> isWalletRepaired() {
    return walletRepaired;
  }

  /**
   * @return True if the CA certificates have been repaired, absent if still progressing
   */
  public Optional<Boolean> isCacertsRepaired() {
    return cacertsRepaired;
  }

  /**
   * <p>Install the CA certificates</p>
   * <p>Reduced visibility for panel view</p>
   */
  void installCACertificates() {
    log.debug("Starting refresh of SSL certs...");

    ListenableFuture cacertsFuture = cacertsExecutorService.submit(
      new Runnable() {
        @Override
        public void run() {
          HttpsManager.INSTANCE.installCACertificates(
            InstallationManager.getOrCreateApplicationDataDirectory(),
            InstallationManager.CA_CERTS_NAME,
            null, // Use default host list
            true // Force loading
          );

        }
      });
    Futures.addCallback(
      cacertsFuture, new FutureCallback() {
        @Override
        public void onSuccess(@Nullable Object result) {
          log.debug("SSL certs refreshed successfully");
          cacertsRepaired = Optional.of(Boolean.TRUE);
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                ViewEvents.fireComponentChangedEvent(getPanelName(), Optional.of(this));
              }
            });
        }

        @Override
        public void onFailure(Throwable t) {
          log.debug("SSL certs were NOT refreshed successfully");
          cacertsRepaired = Optional.of(Boolean.FALSE);
          SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                ViewEvents.fireComponentChangedEvent(getPanelName(), Optional.of(this));
              }
            });
        }
      });

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
      long earliestKeyCreationTime = currentWallet.getEarliestKeyCreationTime();
      log.debug("currentWallet.getEarliestKeyCreationTime(): {}", earliestKeyCreationTime);

      final DateTime replayDate = new DateTime(earliestKeyCreationTime * 1000);

      log.debug("Replay of wallet will be from: {}", replayDate);

      // Trezor hard wallets disable fastCatchup as some early wallets had an incorrect earliestKeyCreation date
      final boolean enableFastCatchup = currentWalletSummary.getWalletType() != WalletType.TREZOR_HARD_WALLET;

      // Hide the header view
      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            ViewEvents.fireViewChangedEvent(ViewKey.HEADER, false);
          }
        });

      // Allow time the UI to update
      Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);

      // Clear all the transactions back to the genesis block
      currentWallet.clearTransactions(0);

      // Ensure we have a wallet service in place
      CoreServices.getOrCreateWalletService(currentWalletSummary.getWalletId());

      // Start the Bitcoin network synchronization operation
      ListenableFuture<Boolean> future = walletExecutorService.submit(
        new Callable<Boolean>() {

          @Override
          public Boolean call() throws Exception {
            CoreServices.getOrCreateBitcoinNetworkService().replayWallet(
              InstallationManager.getOrCreateApplicationDataDirectory(),
              Optional.of(replayDate),
              enableFastCatchup,
              true
            );
            return true;

          }

        });
      Futures.addCallback(
        future, new FutureCallback<Boolean>() {
          @Override
          public void onSuccess(@Nullable Boolean result) {

            // Do nothing this just means that the block chain download has begun

          }

          @Override
          public void onFailure(Throwable t) {

            // Have a failure
            walletRepaired = Optional.of(Boolean.FALSE);

            SwingUtilities.invokeLater(
              new Runnable() {
                @Override
                public void run() {
                  ViewEvents.fireComponentChangedEvent(getPanelName(), Optional.of(this));
                }
              });
          }
        });
    }
  }
}
