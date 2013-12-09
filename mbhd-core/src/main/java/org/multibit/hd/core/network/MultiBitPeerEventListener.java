package org.multibit.hd.core.network;

import com.google.bitcoin.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MultiBitPeerEventListener implements PeerEventListener {

    private int numberOfConnectedPeers;

    private static final Logger log = LoggerFactory.getLogger(MultiBitPeerEventListener.class);

    public MultiBitPeerEventListener() {
      numberOfConnectedPeers = 0;
    }

    @Override
    public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {
      log.debug("number of blocks left = {}", blocksLeft);
    }

    @Override
    public void onChainDownloadStarted(Peer peer, int blocksLeft) {
      log.debug("chain download started with number of blocks left = {}", blocksLeft);
    }

    @Override
    public void onPeerConnected(Peer peer, int peerCount) {
      numberOfConnectedPeers = peerCount;
    }

    @Override
    public void onPeerDisconnected(Peer peer, int peerCount) {
      numberOfConnectedPeers = peerCount;
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
}

