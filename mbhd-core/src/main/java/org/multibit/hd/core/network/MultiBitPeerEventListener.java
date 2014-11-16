package org.multibit.hd.core.network;

import org.bitcoinj.core.*;
import com.google.common.base.Optional;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.managers.WalletManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class MultiBitPeerEventListener implements PeerEventListener {

  private static final Logger log = LoggerFactory.getLogger(MultiBitPeerEventListener.class);

  private int originalBlocksLeft = -1;
  private int lastPercent = 0;
  private Semaphore done = new Semaphore(0);
  private boolean caughtUp = false;

  private int numberOfConnectedPeers = 0;

  // Start with peer count suppression until blocks start to arrive
  private boolean suppressPeerCountMessages = true;

  public MultiBitPeerEventListener() {
  }

  @Override
  public void onPeersDiscovered(Set<PeerAddress> peerAddresses) {
    // Do nothing
  }

  @Override
  public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {
    log.trace("Number of blocks left = {}", blocksLeft);

    if (caughtUp) {
      return;
    }

    if (blocksLeft == 0) {
      caughtUp = true;
      doneDownload();
      done.release();
    }

    if (blocksLeft < 0 || originalBlocksLeft <= 0) {
      return;
    }

    double pct = 100.0 - (100.0 * (blocksLeft / (double) originalBlocksLeft));
    if ((int) pct != lastPercent) {
      if (block != null) {
        progress(pct, blocksLeft, new Date(block.getTimeSeconds() * 1000));
      }
      lastPercent = (int) pct;
    }

    // Determine if peer count message should be suppressed
    // (avoids UI confusion between synchronizing and peer count)
    suppressPeerCountMessages = blocksLeft > 0;

    // Keep track of the download progress
    //updateDownloadPercent(blocksLeft);

    // Fire the download percentage
    CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newChainDownloadProgress(lastPercent, blocksLeft));

    if (!suppressPeerCountMessages) {
      // Fully synchronized so switch to showing the peer count
      CoreEvents.fireBitcoinNetworkChangedEvent(
        BitcoinNetworkSummary.newNetworkReady(numberOfConnectedPeers));
    }
  }

  @Override
  public void onChainDownloadStarted(Peer peer, int blocksLeft) {
    log.debug("Chain download started with number of blocks left = {}", blocksLeft);

    startDownload(blocksLeft);
    // Only mark this the first time, because this method can be called more than once during a chain download
    // if we switch peers during it.
    if (originalBlocksLeft == -1) {
      originalBlocksLeft = blocksLeft;
    } else {
      log.info("Chain download switched to {}", peer);
    }
    if (blocksLeft == 0) {
      doneDownload();
      done.release();
    }

    // Reset the number of blocks at the start of the download
    //numberOfBlocksAtStart = blocksLeft;

    // Determine if peer count message should be suppressed
    // (avoids UI confusion between synchronizing and peer count)
    suppressPeerCountMessages = blocksLeft > 0;

    // Keep track of the download progress
    //updateDownloadPercent(blocksLeft);

    // When the blocks are being downloaded - update the display
    if (suppressPeerCountMessages) {
      CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newChainDownloadProgress(lastPercent, blocksLeft));
    } else {
      // Fully synchronized so switch to showing the peer count
      CoreEvents.fireBitcoinNetworkChangedEvent(
        BitcoinNetworkSummary.newNetworkReady(numberOfConnectedPeers));
    }
  }

  @Override
  public void onPeerConnected(Peer peer, int peerCount) {
    log.trace("(connect) Number of peers = " + peerCount + ", lastPercent = " + lastPercent);

    numberOfConnectedPeers = peerCount;

    // Only show peers after synchronization to avoid confusion
    if (!suppressPeerCountMessages) {
      // Fully synchronized so switch to showing the peer count
      CoreEvents.fireBitcoinNetworkChangedEvent(
        BitcoinNetworkSummary.newNetworkReady(numberOfConnectedPeers));
    }
  }

  @Override
  public void onPeerDisconnected(Peer peer, int peerCount) {
    log.trace("(disconnect) Number of peers = " + peerCount);
    if (peerCount == numberOfConnectedPeers) {
      // Don't fire an event - not useful
      return;
    }
    numberOfConnectedPeers = peerCount;

    // Only show peers after synchronization to avoid confusion
    if (!suppressPeerCountMessages) {
      // Fully synchronized so switch to showing the peer count
      CoreEvents.fireBitcoinNetworkChangedEvent(
        BitcoinNetworkSummary.newNetworkReady(numberOfConnectedPeers));
    }
  }

  @Override
  public Message onPreMessageReceived(Peer peer, Message message) {
    return message;
  }

  @Override
  public void onTransaction(Peer peer, Transaction transaction) {

    // Loop through all the wallets, seeing if the transaction is relevant and adding them as pending if so.
    if (transaction != null) {
      Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
      if (currentWalletSummary.isPresent()) {
        if (currentWalletSummary.get() != null) {
          Wallet currentWallet = currentWalletSummary.get().getWallet();
          if (currentWallet != null) {
            try {
              if (currentWallet.isTransactionRelevant(transaction)) {
                if (!(transaction.isTimeLocked() && transaction.getConfidence().getSource() != TransactionConfidence.Source.SELF)) {
                  if (currentWallet.getTransaction(transaction.getHash()) == null) {

                    log.debug(
                      "MultiBitHD adding a new pending transaction for the wallet '{}'\n{}",
                      currentWalletSummary.get().getWalletId(),
                      transaction.toString()
                    );
                    currentWallet.receivePending(transaction, null);

                    // Emit an event so that GUI elements can update as required
                    Coin value = transaction.getValue(currentWallet);
                    TransactionSeenEvent transactionSeenEvent = new TransactionSeenEvent(transaction, value);
                    transactionSeenEvent.setFirstAppearanceInWallet(true);

                    CoreEvents.fireTransactionSeenEvent(transactionSeenEvent);
                  }
                }
              }
            } catch (ScriptException se) {
              // Cannot understand this transaction - carry on
            }
          }
        }
      }

    }
  }

  @Override
  public List<Message> getData(Peer peer, GetDataMessage m) {
    return null;
  }

  /**
   * Called when download progress is made.
   *
   * @param pct  the percentage of chain downloaded, estimated
   * @param date the date of the last block downloaded
   */
  protected void progress(double pct, int blocksSoFar, Date date) {
    log.trace(
      String.format(
        "Chain download %d%% done with %d blocks to go, block date %s",
        (int) pct,
        blocksSoFar,
        DateFormat.getDateTimeInstance().format(date))
    );
  }

  /**
   * Called when download is initiated.
   *
   * @param blocks the number of blocks to download, estimated
   */
  protected void startDownload(int blocks) {

  }

  /**
   * Called when we are done downloading the block chain.
   */
  protected void doneDownload() {
  }

  /**
   * Wait for the chain to be downloaded.
   */
  public void await() throws InterruptedException {
    done.acquire();
  }
}

