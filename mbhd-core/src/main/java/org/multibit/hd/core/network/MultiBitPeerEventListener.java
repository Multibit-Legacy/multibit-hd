package org.multibit.hd.core.network;

import com.google.bitcoin.core.*;
import com.google.common.base.Optional;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.managers.WalletManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

public class MultiBitPeerEventListener implements PeerEventListener {

  private static final Logger log = LoggerFactory.getLogger(MultiBitPeerEventListener.class);
  private int numberOfBlocksAtStart = -1;
  private int downloadPercent = 0;
  private int numberOfConnectedPeers = 0;

  private boolean suppressPeerCountMessages = true;

  public MultiBitPeerEventListener() {
  }

  @Override
  public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {

    log.trace("Number of blocks left = {}", blocksLeft);

    // Determine if peer count message should be suppressed
    // (avoids UI confusion between synchronizing and peer count)
    suppressPeerCountMessages = blocksLeft > 0;

    // Keep track of the download progress
    updateDownloadPercent(blocksLeft);

    // Fire the download percentage
    CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newChainDownloadProgress(downloadPercent));

    if (!suppressPeerCountMessages) {
      // Switch to showing the peer count
      CoreEvents.fireBitcoinNetworkChangedEvent(
              BitcoinNetworkSummary.newNetworkReady(numberOfConnectedPeers));
    }
  }

  @Override
  public void onChainDownloadStarted(Peer peer, int blocksLeft) {

    log.debug("Chain download started with number of blocks left = {}", blocksLeft);

    // Reset the number of blocks at the start of the download
    numberOfBlocksAtStart = blocksLeft;

    // Determine if peer count message should be suppressed
    // (avoids UI confusion between synchronizing and peer count)
    suppressPeerCountMessages = blocksLeft > 0;

    // Keep track of the download progress
    updateDownloadPercent(blocksLeft);

    // When the blocks are being downloaded - update the display
    if (suppressPeerCountMessages) {
      CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newChainDownloadProgress(downloadPercent));
    } else {
      // Switch to showing the peer count
      CoreEvents.fireBitcoinNetworkChangedEvent(
              BitcoinNetworkSummary.newNetworkReady(numberOfConnectedPeers));
    }
  }

  @Override
  public void onPeerConnected(Peer peer, int peerCount) {

    log.trace("(connect) Number of peers = " + peerCount + ", downloadPercent = " + downloadPercent);

    numberOfConnectedPeers = peerCount;

    // Only show peers after synchronization to avoid confusion
    if (!suppressPeerCountMessages) {
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
      // TODO - want to iterate over all open wallets
      Optional<WalletData> currentWalletData = WalletManager.INSTANCE.getCurrentWalletData();
      if (currentWalletData.isPresent()) {
        if (currentWalletData.get() != null) {
          Wallet currentWallet = currentWalletData.get().getWallet();
          if (currentWallet != null) {
            try {
              if (currentWallet.isTransactionRelevant(transaction)) {
                if (!(transaction.isTimeLocked() && transaction.getConfidence().getSource() != TransactionConfidence.Source.SELF)) {
                  if (currentWallet.getTransaction(transaction.getHash()) == null) {
                    log.debug("MultiBitHD adding a new pending transaction for the wallet '"
                            + currentWalletData.get().getWalletId() + "'\n" + transaction.toString());
                    // The perWalletModelData is marked as dirty.
                    // TODO - mark wallet as dirty ?
                    currentWallet.receivePending(transaction, null);

                    // Emit an event so that GUI elements can update as required
                    TransactionSeenEvent transactionSeenEvent = new TransactionSeenEvent((transaction));
                    BigInteger value = transaction.getValue(currentWallet);
                    transactionSeenEvent.setValue(value);
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
   * <p>Calculate an appropriate download percent</p>
   *
   * @param blocksLeft The number of blocks left to download
   */
  private void updateDownloadPercent(int blocksLeft) {

    if (numberOfBlocksAtStart == -1) {
      // We don't have a number of blocks at the start so count down from the blocksLeft figure
      numberOfBlocksAtStart = blocksLeft;
    }

    if (blocksLeft == 0 && numberOfBlocksAtStart == 0) {
      // Nothing to download so we are finished
      downloadPercent = 100;
    } else {
      downloadPercent = (int) ((1 - ((double) blocksLeft / numberOfBlocksAtStart)) * 100);
    }
  }
}

