package org.multibit.hd.core.network;

import com.google.bitcoin.core.*;
import org.multibit.hd.core.api.BitcoinNetworkSummary;
import org.multibit.hd.core.events.CoreEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MultiBitPeerEventListener implements PeerEventListener {

  private int startingBlock = -1;
  private int downloadPercent = 0;

  private int numberOfConnectedPeers;

  private static final Logger log = LoggerFactory.getLogger(MultiBitPeerEventListener.class);

  public MultiBitPeerEventListener() {
    numberOfConnectedPeers = 0;
  }

  @Override
  public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {

    log.debug("Number of blocks left = {}", blocksLeft);

    // Keep track of the download progress
    updateDownloadPercent(blocksLeft);

    if (blocksLeft > 0) {
      // Keep the progress updated
      CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newChainDownloadProgress(downloadPercent));
    }

  }

  @Override
  public void onChainDownloadStarted(Peer peer, int blocksLeft) {

    log.debug("Chain download started with number of blocks left = {}", blocksLeft);

    // Reset the starting block
    startingBlock = -1;

    // Keep track of the download progress
    updateDownloadPercent(blocksLeft);

  }

  @Override
  public void onPeerConnected(Peer peer, int peerCount) {

    numberOfConnectedPeers = peerCount;

    // Don't interfere with blockchain download
    if (downloadPercent == 100) {
      CoreEvents.fireBitcoinNetworkChangedEvent(
        BitcoinNetworkSummary.newNetworkReady(numberOfConnectedPeers)
      );
    }
  }

  @Override
  public void onPeerDisconnected(Peer peer, int peerCount) {

    numberOfConnectedPeers = peerCount;

    // Don't interfere with blockchain download
    if (downloadPercent == 100) {
      CoreEvents.fireBitcoinNetworkChangedEvent(
        BitcoinNetworkSummary.newNetworkReady(numberOfConnectedPeers)
      );
    }
  }

  @Override
  public Message onPreMessageReceived(Peer peer, Message message) {
    return message;
  }

  @Override
  public void onTransaction(Peer peer, Transaction transaction) {
  }

  @Override
  public List<Message> getData(Peer peer, GetDataMessage m) {
    return null;
  }

  public int getNumberOfConnectedPeers() {
    return numberOfConnectedPeers;
  }

  private void updateDownloadPercent(int blocksLeft) {

    if (startingBlock == -1) {
      startingBlock = blocksLeft;
    }

    downloadPercent = (int) ((1 - ((double) blocksLeft / startingBlock)) * 100);
  }
}

