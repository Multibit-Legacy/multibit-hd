package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.subgraph.orchid.TorClient;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.wallet.KeyChain;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.events.BitcoinSentEvent;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.TransactionCreationEvent;
import org.multibit.hd.core.managers.BlockStoreManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.network.MultiBitPeerEventListener;
import org.multibit.hd.core.utils.Coins;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>Service to provide access to the Bitcoin network, including:</p>
 * <ul>
 * <li>Initialisation of bitcoin network connection</li>
 * <li>Ability to send bitcoin</li>
 * </ul>
 * <p/>
 * <p>Emits the following events:</p>
 * <ul>
 * <li><code>BitcoinNetworkChangeEvent</code></li>
 * </ul>
 *
 * @since 0.0.1
 */
public class BitcoinNetworkService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkService.class);

  public static final Coin DEFAULT_FEE_PER_KB = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE; // Currently 10,000 satoshi
  public static final int MAXIMUM_NUMBER_OF_PEERS = 6;

  /**
   * The boundary for when more mining fee is due
   */
  private static final int MINING_FEE_BOUNDARY = 1000;

  private BlockStore blockStore;
  private PeerGroup peerGroup;  // May need to add listener as in MultiBitPeerGroup
  private BlockChain blockChain;
  private MultiBitPeerEventListener peerEventListener;

  private final NetworkParameters networkParameters;

  private boolean startedOk = false;

  private Optional<SendRequestSummary> lastSendRequestSummaryOptional = Optional.absent();
  private Optional<Wallet> lastWalletOptional = Optional.absent();

  /**
   * @param networkParameters The Bitcoin network parameters
   */
  public BitcoinNetworkService(NetworkParameters networkParameters) {

    Preconditions.checkNotNull(networkParameters, "'networkParameters' must be present");

    log.debug("Bitcoin network service using '{}'", networkParameters.getId());

    this.networkParameters = networkParameters;

    requireFixedThreadPoolExecutor(5, "bitcoin-network");

  }

  @Override
  public boolean start() {
    try {
      // Check if there is a wallet - if there is no wallet the network will not start (there's nowhere to put the blockchain)
      if (!WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
        log.warn("Not starting bitcoin network service as there is currently no wallet.");
        return true;
      }
      File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
      String walletRoot = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get().getParentFile().getAbsolutePath();
      File blockStoreFile = new File(walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX);
      File checkpointsFile = new File(walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX);

      // Load or create the blockStore..
      log.debug("Create new block store");
      blockStore = new BlockStoreManager(networkParameters).createBlockStore(blockStoreFile, checkpointsFile, null, false);
      log.debug("Success. Blockstore is '{}'", blockStore);

      log.debug("Starting Bitcoin network...");

      restartNetwork();

    } catch (IOException | BlockStoreException | TimeoutException e) {

      log.error(e.getMessage(), e);

      CoreEvents.fireBitcoinNetworkChangedEvent(
        BitcoinNetworkSummary.newNetworkStartupFailed(
          CoreMessageKey.START_NETWORK_CONNECTION_ERROR,
          Optional.of(new Object[]{})
        )
      );

    }

    return true;
  }

  @Override
  public void stopAndWait() {

    startedOk = false;

    // Stop the peer group if it is running
    stopPeerGroup();

    // Close the block store
    closeBlockstore();

    // Save the current wallet
    saveWallet();

    // Close the wallet
    closeWallet();

    // Hand over to the superclass to finalise service executors
    super.stopAndWait();

    log.debug("Bitcoin network service shut down");

  }

  /**
   * @return True if the network has started and a peer group is available
   */
  public boolean isStartedOk() {
    return startedOk;
  }

  public void recalculateFastCatchupAndFilter() {

    if (peerGroup != null) {
      peerGroup.recalculateFastCatchupAndFilter(PeerGroup.FilterRecalculateMode.FORCE_SEND_FOR_REFRESH);
    }

  }

  /**
   * <p>Download the block chain in a new thread</p>
   */
  public void downloadBlockChainInBackground() {

    getExecutorService().submit(new Runnable() {
      @Override
      public void run() {
        Preconditions.checkNotNull(peerGroup, "'peerGroup' must be present");

        log.debug("Downloading block chain...");

        // This method blocks until completed but fires events along the way
        try {
          log.debug("Starting blockchain download . . .");
          peerGroup.downloadBlockChain();
          log.debug("Block chain downloaded.");
          CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newNetworkReady(peerGroup.numConnectedPeers()));
        } catch (RuntimeException re) {
          log.debug("Blockchain download was interrupted. Error was : '" + re.getMessage() + "'");
        }
      }
    });

  }

  /**
   * Sync the current wallet from the date specified.
   * The blockstore is deleted and created anew, checkpointed and then the blockchain is downloaded.
   */
  public void replayWallet(DateTime dateToReplayFrom) throws IOException, BlockStoreException, TimeoutException {

    Preconditions.checkNotNull(dateToReplayFrom);
    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().isPresent());
    Preconditions.checkState(!SwingUtilities.isEventDispatchThread(), "Replay should not take place on the EDT");

    log.debug("Stopping any existing downloads");

    // Stop the peer group if it is running
    stopPeerGroup();

    // Close the block store
    closeBlockstore();

    log.info("Starting replay of wallet with id '" + WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletId()
      + "' from date " + dateToReplayFrom);

    // TODO (JB) The current best height should be remembered and used to generate percentage completeWithoutSigning as
    // TODO (JB) then if the peer is replaced the percentage increases monotonically

    File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
    String walletRoot = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get().getParentFile().getAbsolutePath();

    File blockchainFile = new File(walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.SPV_BLOCKCHAIN_SUFFIX);
    File checkpointsFile = new File(walletRoot + File.separator + InstallationManager.MBHD_PREFIX + InstallationManager.CHECKPOINTS_SUFFIX);

    log.debug("Recreating blockstore with checkpoint date of " + dateToReplayFrom + " ...");
    blockStore = new BlockStoreManager(networkParameters).createBlockStore(blockchainFile, checkpointsFile, dateToReplayFrom.toDate(), true);
    log.debug("Blockstore is '{}'", blockStore);

    restartNetwork();

    downloadBlockChainInBackground();

    log.debug("Blockchain download started.");
  }

  /**
   * <p>Send bitcoin</p>
   * <p/>
   * <p>In the future will also need:</p>
   * <ul>
   * <li>a CoinSelector - when HD subnodes are supported</li>
   * </ul>
   * <p>The result of the operation is sent to the CoreEventBus as a TransactionCreationEvent and, if the tx is sent ok, a BitcoinSentEvent</p>
   *
   * @param sendRequestSummary The information required to send bitcoin
   */
  public void send(final SendRequestSummary sendRequestSummary) {
    lastSendRequestSummaryOptional = Optional.absent();
    lastWalletOptional = Optional.absent();

    getExecutorService().submit(new Runnable() {
      @Override
      public void run() {

        performSend(sendRequestSummary);
      }

    });
  }

  /**
   * @param sendRequestSummary The information required to send bitcoin
   *
   * @return The send request
   */
  private boolean performSend(SendRequestSummary sendRequestSummary) {
    log.debug("Starting the send process");

    // Verify the wallet summary
    if (!checkWalletSummary(sendRequestSummary)) {
      log.debug("Wallet summary check fail");
      return false;
    }

    // Get the current wallet
    WalletSummary currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();
    Wallet wallet = currentWalletSummary.getWallet();

    if (WalletType.TREZOR_HARD_WALLET.equals(currentWalletSummary.getWalletType())) {
      // Attempt to sign the transaction using the Trezor wallet
      // This will fire HardwareWalletEvents as the signing progresses which are dealt by the onHardwareWalletEvent method in the SendBitcoinConfirmTrezorPanelView

      // Remember the last sendRequestSummary and Wallet for callback
      lastSendRequestSummaryOptional = Optional.of(sendRequestSummary);
      lastWalletOptional = Optional.of(wallet);

      // Clear the transaction output count in the HardwareWalletService context
      CoreServices.getOrCreateHardwareWalletService().get().getContext().setTransactionOutputCount(Optional.<Integer>absent());

      if (!signUsingTrezor(sendRequestSummary, wallet)) {
        return false;
      }
    } else {
      // Regular non Trezor signing
      // Derive and append the key parameter to unlock the wallet
      if (!appendKeyParameter(sendRequestSummary, wallet)) {
        return false;
      }

      // Attempt to sign the transaction directly
      if (!signDirectly(sendRequestSummary, wallet)) {
        return false;
      }

      performCommitAndBroadcast(sendRequestSummary, wallet);
    }

    return true;

  }

  public void commitAndBroadcast(final SendRequestSummary sendRequestSummary, final Wallet wallet) {
    getExecutorService().submit(new Runnable() {
      @Override
      public void run() {

        performCommitAndBroadcast(sendRequestSummary, wallet);
      }

    });
  }

  private boolean performCommitAndBroadcast(SendRequestSummary sendRequestSummary, Wallet wallet) {
    // Attempt to commit the signed transaction to the wallet
    if (!commit(sendRequestSummary, wallet)) {
      return false;
    }

    // Attempt to broadcast it
    if (!broadcast(sendRequestSummary, wallet)) {
      return false;
    }

    // Must be OK to be here
    log.debug("Commit and broadcast of coins has completed");
    lastSendRequestSummaryOptional = Optional.absent();
    lastWalletOptional = Optional.absent();

    return true;
  }

  /**
   * @param sendRequestSummary The information required to send bitcoin
   * @param wallet             The wallet
   *
   * @return True if the derivation process was successful
   */
  private boolean appendKeyParameter(SendRequestSummary sendRequestSummary, Wallet wallet) {

    // Wrap it all up in a try-catch to ensure we fire a failure event
    try {

      log.debug("Deriving key parameter");

      Preconditions.checkNotNull(wallet, "'wallet' must be present");

      if (wallet.getKeyCrypter() == null) {
        throw new IllegalStateException("No key crypter in wallet when one is expected.");
      }
      sendRequestSummary.setKeyParameter(wallet.getKeyCrypter().deriveKey(sendRequestSummary.getPassword()));

      return true;

    } catch (IllegalStateException | KeyCrypterException e) {

      log.error(e.getMessage(), e);

      // Declare the transaction creation a failure
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        null,
        sendRequestSummary.getTotalAmount(),
        Optional.<FiatPayment>absent(),
        Optional.<Coin>absent(),
        Optional.<Coin>absent(),
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        false,
        CoreMessageKey.THE_ERROR_WAS.getKey(),
        new String[]{e.getClass().getCanonicalName() + " " + e.getMessage()},
        sendRequestSummary.getNotes(),
        false));
    }

    // Must have failed to be here
    return false;
  }

  /**
   * @param sendRequestSummary The information required to send bitcoin
   *
   * @return True if no error was encountered
   */
  private boolean appendClientFee(SendRequestSummary sendRequestSummary, boolean forceNow) {

    log.debug("Appending client fee (if required)");

    final boolean isClientFeeRequired;
    if (sendRequestSummary.getFeeState().isPresent()) {
      int currentNumberOfSends = sendRequestSummary.getFeeState().get().getCurrentNumberOfSends();
      int nextFeeSendCount = sendRequestSummary.getFeeState().get().getNextFeeSendCount();

      isClientFeeRequired = (currentNumberOfSends == nextFeeSendCount) || forceNow;

    } else {
      // Nothing more to be done
      return true;
    }

    // May need to add the client fee
    if (isClientFeeRequired) {
      Address feeAddress = sendRequestSummary.getFeeState().get().getNextFeeAddress();
      sendRequestSummary.setFeeAddress(Optional.of(feeAddress));

      log.debug("Added client fee to address: '{}'", feeAddress);
    } else {
      log.debug("No client fee address added for this tx");
    }

    // Must be OK to be here
    return true;
  }

  /**
   * Handle missing wallet summary
   *
   * @param sendRequestSummary The information required to send bitcoin
   *
   * @return True if the wallet summary is present
   */
  private boolean checkWalletSummary(SendRequestSummary sendRequestSummary) {

    if (!WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {

      // Declare the transaction creation a failure - no wallet
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        null,
        sendRequestSummary.getTotalAmount(),
        Optional.<FiatPayment>absent(),
        Optional.<Coin>absent(),
        Optional.<Coin>absent(),
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        false,
        CoreMessageKey.NO_ACTIVE_WALLET.getKey(),
        new String[]{""},
        sendRequestSummary.getNotes(),
        false
      ));

      // Prevent fall-through to success
      return false;
    }

    // Must be OK to be here
    return true;
  }

  /**
   * <p>Build and append a Bitcoinj send request using the summary. This also calculates the final transaction fees and fiat equivalents</p>
   *
   * @param sendRequestSummary The information required to send bitcoin
   */
  private boolean appendSendRequest(SendRequestSummary sendRequestSummary) {

    log.debug("Appending send request based on: {}", sendRequestSummary);

    try {
      final Wallet.SendRequest sendRequest = Wallet.SendRequest.to(
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getAmount()
      );
      if (sendRequestSummary.getKeyParameter().isPresent()) {
        sendRequest.aesKey = sendRequestSummary.getKeyParameter().get();
      }
      sendRequest.fee = Coin.ZERO;
      sendRequest.feePerKb = sendRequestSummary.getFeePerKB();
      sendRequest.changeAddress = sendRequestSummary.getChangeAddress();

      // Require empty wallet to ensure that all funds are included
      sendRequest.emptyWallet = sendRequestSummary.isEmptyWallet();

      // Only include the fee output if not emptying since it interferes
      // with the coin selector
      if (!sendRequest.emptyWallet && sendRequestSummary.getFeeAddress().isPresent()) {
        // Work out the size of the transaction in bytes (the fee solver will have calculated the fee for this size)
        int initialSize;
        try {
          initialSize = calculateSize(sendRequest.tx);
          log.debug("Size of transaction before adding the client fee was {}", initialSize);
        } catch (IOException ioe) {
          log.error("Could not calculate initial transaction size. " + ioe.getMessage());
          return false;
        }

        // Add a tx output to pay the client fee if it is greater than the dust level
        if (sendRequestSummary.getFeeState().get().getFeeOwed().compareTo(Transaction.MIN_NONDUST_OUTPUT) <= 0) {
          log.debug("Not adding client fee as it is smaller than dust : {}", sendRequestSummary.getFeeState().get().getFeeOwed());
          sendRequestSummary.setClientFeeAdded(Optional.<Coin>absent());
        } else {
          sendRequest.tx.addOutput(
            sendRequestSummary.getFeeState().get().getFeeOwed(),
            sendRequestSummary.getFeeAddress().get()
          );
          sendRequestSummary.setClientFeeAdded(Optional.of(sendRequestSummary.getFeeState().get().getFeeOwed()));

          // The transaction now has an extra client fee output added
          // This increases the size of the transaction, and may require more fee if it pushes it over a 1000 byte size boundary
          int updatedSize;
          try {
            updatedSize = calculateSize(sendRequest.tx);
            log.debug("Size of transaction after adding the client fee was {}", updatedSize);
          } catch (IOException ioe) {
            log.error("Could not calculate updated transaction size. " + ioe.getMessage());
            return false;
          }

          if (Math.floor(updatedSize / MINING_FEE_BOUNDARY) > Math.floor(initialSize / MINING_FEE_BOUNDARY)) {
            // Adding a client fee output has stepped over a mining fee boundary.
            // There is extra mining fee due - this can either be paid by reducing the amount redeemed (tx output 0)
            // or reducing the client fee (tx output 1)
            // If neither of these is possible (due to dust limits) then give up trying to claim the client fee.

            if (sendRequest.tx.getOutput(0).getValue().compareTo(Transaction.MIN_NONDUST_OUTPUT.add(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE)) > 0) {
              // There is enough bitcoin on the redemption output, decrease that
              sendRequest.tx.getOutput(0).setValue(sendRequest.tx.getOutput(0).getValue().subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE));
              log.debug("Adjusting transaction output 0 to {}", sendRequest.tx.getOutput(0).getValue());
            } else {
              // Try decreasing the client fee
              if (sendRequest.tx.getOutput(1).getValue().compareTo(Transaction.MIN_NONDUST_OUTPUT.add(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE)) > 0) {
                // There is enough bitcoin on the client fee output, decrease that
                Coin adjustedClientFee = sendRequest.tx.getOutput(1).getValue().subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
                sendRequest.tx.getOutput(1).setValue(adjustedClientFee);
                sendRequestSummary.setClientFeeAdded(Optional.of(adjustedClientFee));
                log.debug("Adjusting transaction output 1 to {}", adjustedClientFee);
              } else {
                // We cannot pay the mining fee for the extra client fee output so remove it.
                // Put back the original amount on the redemption output
                sendRequest.tx.clearOutputs();
                sendRequest.tx.addOutput(sendRequestSummary.getAmount(), sendRequestSummary.getDestinationAddress());
                sendRequestSummary.setClientFeeAdded(Optional.<Coin>absent());
                log.debug("Removing client fee as cannot be paid due to dust levels");
              }
            }
          }
        }
      } else {
        log.debug("Not adding client fee output due to !sendRequest.emptyWallet = {} or sendRequestSummary.getFeeAddress().isPresent() = {}", sendRequest.emptyWallet, sendRequestSummary.getFeeAddress().isPresent());
      }

      // Append the Bitcoinj send request to the summary
      sendRequestSummary.setSendRequest(sendRequest);

    } catch (IllegalStateException e) {

      log.error(e.getMessage(), e);

      // Declare the transaction creation a failure
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        null,
        sendRequestSummary.getTotalAmount(),
        Optional.<FiatPayment>absent(),
        Optional.<Coin>absent(),
        Optional.<Coin>absent(),
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        false,
        CoreMessageKey.THE_ERROR_WAS.getKey(),
        new String[]{e.getClass().getCanonicalName() + " " + e.getMessage()},
        sendRequestSummary.getNotes(),
        false
      ));

      // We cannot proceed to broadcast
      return false;

    }

    // Must be OK to be here
    return true;

  }

  /**
   * @param sendRequestSummary The information required to prepare a transaction for sending (this is everything except the credentials)
   *                           This prepares the transaction but does not sign it.
   *
   * @return whether the prepareTransaction was successful or not
   */
  public boolean prepareTransaction(SendRequestSummary sendRequestSummary) {
    log.debug("Starting the prepare transaction process");

    // Verify the wallet summary
    if (!checkWalletSummary(sendRequestSummary)) {
      log.debug("Wallet summary check fail");
      return false;
    }

    // Get the current wallet
    Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();

    // Build and append the client fee (if required)
    if (!appendClientFee(sendRequestSummary, sendRequestSummary.isEmptyWallet())) {
      return false;
    }

    if (!sendRequestSummary.isEmptyWallet()) {

      // This is a standard send so proceed as normal
      log.debug("Treating as standard send");

      // Attempt to build and append the send request as if it were standard
      if (!appendSendRequest(sendRequestSummary)) {
        return false;
      }

      // Attempt to completeWithoutSigning it
      if (!completeWithoutSigning(sendRequestSummary, wallet)) {
        return false;
      }

      // Set the fiat equivalent amount into the sendRequestSummary - this will take into account the transaction fee and client fee
      setFiatEquivalent(sendRequestSummary);
    } else {
      // This is an empty wallet so perform a dry run to get fees
      log.debug("Treating as an 'empty wallet' send");

      // Attempt to build and append the send request as if it were standard
      if (!appendSendRequest(sendRequestSummary)) {
        return false;
      }

      // Attempt to completeWithoutSigning it
      if (!completeWithoutSigning(sendRequestSummary, wallet)) {
        return false;
      }

      log.debug("Adjusting outputs using 'dry run' values");

      // Examine the result to determine miner fees - the calculated maximum amount is put on the (single) tx output
      Wallet.SendRequest sendRequest = sendRequestSummary.getSendRequest().get();

      // Determine the maximum amount allowing for client fees
      Coin recipientAmount = sendRequest.tx.getOutput(0).getValue();
      Coin clientFeeAmount = sendRequestSummary.getFeeState().get().getFeeOwed();

      // Adjust the send request summary accordingly
      recipientAmount = recipientAmount.subtract(clientFeeAmount);
      log.debug("Adjusted recipientAmount = " + recipientAmount.toString() + ", clientFeeAmount = " + clientFeeAmount.toString());

      // Update the SendRequestSummary with the new values and ensure it is not an "empty wallet"
      SendRequestSummary emptyWalletSendRequestSummary = new SendRequestSummary(
        sendRequestSummary.getDestinationAddress(),
        recipientAmount,
        sendRequestSummary.getFiatPayment(),
        sendRequestSummary.getChangeAddress(),
        sendRequestSummary.getFeePerKB(),
        sendRequestSummary.getPassword(),
        sendRequestSummary.getFeeState(),
        false
      );
      emptyWalletSendRequestSummary.setNotes(sendRequestSummary.getNotes());
      if (sendRequestSummary.getKeyParameter().isPresent()) {
        emptyWalletSendRequestSummary.setKeyParameter(sendRequestSummary.getKeyParameter().get());
      }

      // Attempt to build and append the send request as if it were standard
      // (This may now add on a client fee output and also adjust the size of the output amounts)
      if (!appendSendRequest(emptyWalletSendRequestSummary)) {
        return false;
      }

      // Attempt to completeWithoutSigning it
      if (!completeWithoutSigning(emptyWalletSendRequestSummary, wallet)) {
        return false;
      }

      // Set the fiat equivalent amount into the emptyWalletSendRequestSummary - this will take into account the transaction fee and client fee
      setFiatEquivalent(emptyWalletSendRequestSummary);
    }

    // Must be OK to be here
    log.debug("Prepare transaction has completed");

    return true;

  }

  /**
   * Work out the fiat equivalent of the total bitcoin amount being spent.
   * This includes the transaction fee and the client fee.
   * The exchange rate used is already set into the SendRequestSummary.fiatPayment by the UI models
   *
   * @param sendRequestSummary Send information, including transaction fee, client fee and exchange rate information
   *
   * @return boolean true if operation was successful, false otherwise
   */
  private boolean setFiatEquivalent(SendRequestSummary sendRequestSummary) {
    Preconditions.checkState(sendRequestSummary.getSendRequest().isPresent(), "No send request is present");
    Preconditions.checkNotNull(sendRequestSummary.getSendRequest().get().tx);

    log.debug("sendRequestSummary = " + sendRequestSummary.toString());

    try {
      Coin totalAmountIncludingTransactionAndClientFee = Coin.ZERO;

      // Loop over all the tx outputs, adding up everything but the change address
      log.debug("Calculating the fiat equivalent for transaction being sent");
      for (TransactionOutput transactionOutput : sendRequestSummary.getSendRequest().get().tx.getOutputs()) {
        log.debug("Examining tx output = " + transactionOutput.toString());
        Address toAddress = transactionOutput.getScriptPubKey().getToAddress(networkParameters);
        if (!toAddress.equals(sendRequestSummary.getChangeAddress())) {
          // Add to the running total
          totalAmountIncludingTransactionAndClientFee = totalAmountIncludingTransactionAndClientFee.add(transactionOutput.getValue());
          log.debug("Adding a transaction output amount, total bitcoin is now " + totalAmountIncludingTransactionAndClientFee.toString());
        } else {
          log.debug("Skipping a transaction output as it is the change address");
        }
      }

      // Now add in the transaction fee
      totalAmountIncludingTransactionAndClientFee = totalAmountIncludingTransactionAndClientFee.add(sendRequestSummary.getSendRequest().get().fee);

      // Sends are negative
      totalAmountIncludingTransactionAndClientFee = totalAmountIncludingTransactionAndClientFee.negate();

      log.debug("Added the transaction fee, bitcoin total is now " + totalAmountIncludingTransactionAndClientFee.toString());

      // Apply the exchange rate
      BigDecimal localAmount;
      if (sendRequestSummary.getFiatPayment().isPresent() && sendRequestSummary.getFiatPayment().get().getRate().isPresent()) {
        localAmount = Coins.toLocalAmount(totalAmountIncludingTransactionAndClientFee, new BigDecimal(sendRequestSummary.getFiatPayment().get().getRate().get()));
        sendRequestSummary.getFiatPayment().get().setAmount(Optional.of(localAmount));
      } else {
        localAmount = BigDecimal.ZERO;
      }
      log.debug("Total transaction bitcoin amount = " + totalAmountIncludingTransactionAndClientFee.toString() + ", calculated fiat amount = " + localAmount.toString());
      return true;
    } catch (ScriptException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  /**
   * Complete the transaction to work out the fees but DO NOT sign it yet
   *
   * @param sendRequestSummary The information required to send bitcoin
   * @param wallet             The wallet
   *
   * @return True if the completeWithoutSigning and signDirectly operations were successful
   */
  private boolean completeWithoutSigning(SendRequestSummary sendRequestSummary, Wallet wallet) {

    log.debug("Completing send request...");

    Wallet.SendRequest sendRequest = sendRequestSummary.getSendRequest().get();

    try {

      // Complete it (works out fee) but DO NOT sign it
      sendRequest.signInputs = false;
      wallet.completeTx(sendRequest);

    } catch (Exception e) {

      log.error(e.getMessage(), e);

      String transactionId = sendRequest.tx != null ? sendRequest.tx.getHashAsString() : "?";

      // Fire a failed transaction creation event
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        transactionId,
        sendRequestSummary.getTotalAmount(),
        Optional.<FiatPayment>absent(),
        Optional.<Coin>absent(),
        Optional.<Coin>absent(),
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        false,
        CoreMessageKey.THE_ERROR_WAS.getKey(),
        new String[]{e.getMessage()},
        sendRequestSummary.getNotes(),
        false));

      // We cannot proceed to signing
      return false;
    }

    // Must be OK to be here
    return true;
  }

  /**
   * Sign the transaction using a Trezor
   *
   * TODO Refactor out of the BitcoinNetworkService
   *
   * @param sendRequestSummary The information required to send bitcoin
   * @param wallet             The wallet
   *
   * @return True if the signDirectly operation was successful
   */
  private boolean signUsingTrezor(SendRequestSummary sendRequestSummary, Wallet wallet) {
    log.debug("Signing the send request using a Trezor ...");


    Wallet.SendRequest sendRequest = sendRequestSummary.getSendRequest().get();

    Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();

    // Check if there is a wallet present
    if (hardwareWalletService.isPresent()) {
      try {

        // Provide a map between the input indices and the HD path of receiving addresses used
        Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap = buildReceivingAddressPathMap(
          sendRequest.tx,
          wallet
        );

        // Sign the transaction using the Trezor device
        hardwareWalletService.get().signTx(sendRequest.tx, receivingAddressPathMap);

        // Must be ok to reach here
        return true;
      } catch (Exception e) {
        log.error("Failed to sign using Trezor", e);
        return false;
      }
    } else {
      log.warn("HardwareWalletService not present so cannot sign transaction");
      return false;
    }
  }

  /**
   * Sign the transaction directly
   *
   * @param sendRequestSummary The information required to send bitcoin
   * @param wallet             The wallet
   *
   * @return True if the signDirectly operation was successful
   */
  private boolean signDirectly(SendRequestSummary sendRequestSummary, Wallet wallet) {

    log.debug("Signing the send request directly...");

    Wallet.SendRequest sendRequest = sendRequestSummary.getSendRequest().get();

    try {
      // Ensure the aeskey for decrypting the keys is present in the sendRequest
      sendRequest.aesKey = wallet.getKeyCrypter().deriveKey(sendRequestSummary.getPassword());

      // Sign the transaction
      sendRequest.signInputs = true;
      log.debug("sendRequest just before signing " + sendRequest);
      wallet.signTransaction(sendRequest);

      // Check the signatures are canonical - non-canonical signatures are not relayed
      for (TransactionInput txInput : sendRequest.tx.getInputs()) {
        byte[] signature = txInput.getScriptSig().getChunks().get(0).data;
        log.debug("Is signature canonical test result '{}' for txInput '{}', signature '{}'", TransactionSignature.isEncodingCanonical(signature), txInput.toString(), Utils.HEX.encode(signature));
      }
    } catch (Exception e) {

      log.error(e.getMessage(), e);

      String transactionId = sendRequest.tx != null ? sendRequest.tx.getHashAsString() : "?";

      // Fire a failed transaction creation event
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        transactionId,
        sendRequestSummary.getTotalAmount(),
        Optional.<FiatPayment>absent(),
        Optional.<Coin>absent(),
        Optional.<Coin>absent(),
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        false,
        CoreMessageKey.THE_ERROR_WAS.getKey(),
        new String[]{e.getMessage()},
        sendRequestSummary.getNotes(),
        false));

      // We cannot proceed to commit
      return false;
    }

    // Must be OK to be here
    return true;
  }

  /**
   * Commit the (signed) transaction to the wallet
   *
   * @param sendRequestSummary The information required to send bitcoin
   * @param wallet             The wallet
   *
   * @return True if the commit operation was successful
   */
  private boolean commit(SendRequestSummary sendRequestSummary, Wallet wallet) {

    log.debug("Committing send request...");

    Wallet.SendRequest sendRequest = sendRequestSummary.getSendRequest().get();

    try {
      // Commit to the wallet (informs the wallet of the transaction)
      if (wallet.getTransaction(sendRequest.tx.getHash()) != null) {
        wallet.commitTx(sendRequest.tx);

        // Fire a successful transaction creation event (not yet broadcast)
        CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
          sendRequest.tx.getHashAsString(),
          sendRequestSummary.getTotalAmount(),
          sendRequestSummary.getFiatPayment(),
          Optional.of(sendRequest.fee) /* the actual mining fee paid */,
          sendRequestSummary.getClientFeeAdded(),
          sendRequestSummary.getDestinationAddress(),
          sendRequestSummary.getChangeAddress(),
          true,
          null,
          null,
          sendRequestSummary.getNotes(),
          true
        ));
      } else {
        log.debug("Not committing tx with hash '{}' because tx is already present in wallet", sendRequest.tx.getHashAsString());
      }

    } catch (Exception e) {

      log.error(e.getMessage(), e);

      String transactionId = sendRequest.tx != null ? sendRequest.tx.getHashAsString() : "?";

      // Fire a failed transaction creation event
      CoreEvents.fireTransactionCreationEvent(new TransactionCreationEvent(
        transactionId,
        sendRequestSummary.getTotalAmount(),
        Optional.<FiatPayment>absent(),
        Optional.<Coin>absent(),
        Optional.<Coin>absent(),
        sendRequestSummary.getDestinationAddress(),
        sendRequestSummary.getChangeAddress(),
        false,
        CoreMessageKey.THE_ERROR_WAS.getKey(),
        new String[]{e.getMessage()},
        sendRequestSummary.getNotes(),
        false));

      // We cannot proceed to broadcast
      return false;
    }

    // Must be OK to be here
    return true;
  }

  /**
   * <p>Attempt to broadcast the transaction in the Bitcoinj send request</p>
   *
   * @param sendRequestSummary The information required to send bitcoin
   *
   * @return True if the broadcast operation was successful
   */
  private boolean broadcast(SendRequestSummary sendRequestSummary, Wallet wallet) {

    log.debug("Attempting to broadcast transaction");

    Wallet.SendRequest sendRequest = sendRequestSummary.getSendRequest().get();

    try {

      // Ping the peers to check the Bitcoin network connection
      if (!pingPeers()) {

        // Declare the send a failure
        CoreEvents.fireBitcoinSentEvent(new BitcoinSentEvent(
          sendRequestSummary.getDestinationAddress(),
          sendRequestSummary.getTotalAmount(),
          sendRequestSummary.getChangeAddress(),
          Optional.<Coin>absent(),
          Optional.<Coin>absent(),
          false,
          CoreMessageKey.COULD_NOT_CONNECT_TO_BITCOIN_NETWORK.getKey(),
          new String[]{"Could not reach any Bitcoin nodes"}
        ));

        // Prevent a fall-through to success
        return false;
      }

      // Broadcast
      peerGroup.broadcastTransaction(sendRequest.tx);

      log.debug("Broadcast transaction: '{}'", Utils.HEX.encode(sendRequest.tx.bitcoinSerialize()));

      // Declare the send a success
      CoreEvents.fireBitcoinSentEvent(new BitcoinSentEvent(
        sendRequestSummary.getDestinationAddress(), sendRequestSummary.getTotalAmount(),
        sendRequestSummary.getChangeAddress(),
        Optional.of(sendRequest.fee),
        sendRequestSummary.getClientFeeAdded(),
        true,
        CoreMessageKey.BITCOIN_SENT_OK.getKey(),
        null
      ));

    } catch (VerificationException e) {

      log.error(e.getMessage(), e);

      // Declare the send a failure
      CoreEvents.fireBitcoinSentEvent(new BitcoinSentEvent(
        sendRequestSummary.getDestinationAddress(), sendRequestSummary.getTotalAmount(),
        sendRequestSummary.getChangeAddress(),
        Optional.<Coin>absent(),
        Optional.<Coin>absent(),
        false,
        CoreMessageKey.THE_ERROR_WAS.getKey(),
        new String[]{e.getMessage()}
      ));

      // Prevent a fall-through to success
      return false;

    }

    // Must be OK to be here
    return true;

  }

  /**
   * @param unsignedTx The unsigned transaction (expect OP_0 in place of signatures)
   * @param wallet     The wallet
   *
   * @return The receiving address path map linking the tx input index to a deterministic path
   */
  private Map<Integer, ImmutableList<ChildNumber>> buildReceivingAddressPathMap(Transaction unsignedTx, Wallet wallet) {

    Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap = Maps.newHashMap();

    // Examine the Tx inputs to determine receiving addresses in use
    for (int i = 0; i < unsignedTx.getInputs().size(); i++) {
      TransactionInput input = unsignedTx.getInput(i);

      // Unsigned input script arranged as OP_0, PUSHDATA(33)[public key]
      Script script = input.getScriptSig();
      byte[] data = script.getChunks().get(1).data;

      DeterministicKey keyFromPubKey = wallet.getActiveKeychain().findKeyFromPubKey(data);
      Preconditions.checkNotNull(keyFromPubKey, "Could not find deterministic key from given pubkey. Input script index: " + i);

      receivingAddressPathMap.put(i, keyFromPubKey.getPath());

    }

    return receivingAddressPathMap;
  }


  /**
   * <p>Create a new peer group</p>
   */
  private void createNewPeerGroup() throws TimeoutException {

    if (Configurations.currentConfiguration.isTor()) {

      log.info("Creating new TOR peer group for '{}'", networkParameters);
      InstallationManager.removeCryptographyRestrictions();
      peerGroup = PeerGroup.newWithTor(networkParameters, blockChain, new TorClient());

    } else {

      log.info("Creating new DNS peer group for '{}'", networkParameters);
      peerGroup = new PeerGroup(networkParameters, blockChain);
      peerGroup.addPeerDiscovery(new DnsDiscovery(networkParameters));

    }

    peerGroup.setUserAgent(InstallationManager.MBHD_APP_NAME,
      Configurations.currentConfiguration.getAppearance().getVersion());
    peerGroup.setFastCatchupTimeSecs(0); // genesis block
    peerGroup.setMaxConnections(MAXIMUM_NUMBER_OF_PEERS);
    peerGroup.setUseLocalhostPeerWhenPossible(false);

    peerEventListener = new MultiBitPeerEventListener();
    peerGroup.addEventListener(peerEventListener);

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();
      peerGroup.addWallet(wallet);
      peerGroup.setFastCatchupTimeSecs(wallet.getEarliestKeyCreationTime());
    }
  }

  /**
   * Get the next available change address
   *
   * @return changeAddress The next change address as an Address
   */
  public Address getNextChangeAddress() {

    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().isPresent());
    Preconditions.checkNotNull(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet());
    Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet().getKeychainSize() > 0);

    Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();

    return wallet.freshKey(KeyChain.KeyPurpose.CHANGE).toAddress(networkParameters);
  }

  /**
   * Ping all connected peers to see if there is an active network connection
   *
   * @return true is one or more peers respond to the ping
   */
  public boolean pingPeers() {

    List<Peer> connectedPeers = peerGroup.getConnectedPeers();
    boolean atLeastOnePingWorked = false;
    if (connectedPeers != null) {
      for (Peer peer : connectedPeers) {

        log.debug("Ping: {}", peer.getAddress().toString());

        try {
          ListenableFuture<Long> result = peer.ping();
          result.get(4, TimeUnit.SECONDS);
          atLeastOnePingWorked = true;
          break;
        } catch (ProtocolException | InterruptedException | ExecutionException | TimeoutException e) {
          log.warn("Peer '" + peer.getAddress().toString() + "' failed ping test. Message was " + e.getMessage());
        }
      }
    }

    return atLeastOnePingWorked;
  }

  /**
   * @return True if at least one of the MainNet DNS seeds can be reached without error
   */
  private boolean isNetworkPresent() {

    final String[] dnsSeeds = networkParameters.getDnsSeeds();

    // Attempt to lookup each address - first success indicates working network
    for (String dnsSeed : dnsSeeds) {
      try {
        if (InetAddress.getAllByName(dnsSeed) != null) {
          return true;
        }
      } catch (UnknownHostException e) {
        log.warn("Could not resolve '{}'", dnsSeed);
      }
    }

    // All DNS seeds failed
    return false;
  }

  /**
   * Removes the current wallet from the block chain and closes the block store
   */
  private void closeBlockstore() {

    // Remove the wallet from the block chain
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent() && blockChain != null) {
      blockChain.removeWallet(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet());
    }

    // Close the blockstore
    if (blockStore != null) {
      try {
        // The blockstore can throw an NPE internally
        blockStore.close();
      } catch (BlockStoreException e) {
        log.error("Blockstore not closed cleanly", e);
      } catch (NullPointerException e) {
        // Internal bug in Bitcoinj
      }
    }
  }

  /**
   * Closes the wallet
   */
  private void closeWallet() {
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent() && blockChain != null) {
      try {
        WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet().shutdownAutosaveAndWait();
      } catch (IllegalStateException ise) {
        // If there is no autosaving set up yet then that is ok
        if (!ise.getMessage().contains("Auto saving not enabled.")) {
          throw ise;
        }
      }
    }
  }

  /**
   * <p>Stops the current peer group blocking until completeWithoutSigning</p>
   */
  private void stopPeerGroup() {

    if (peerGroup != null) {
      log.debug("Stopping peerGroup service...");
      peerGroup.removeEventListener(peerEventListener);

      // Remove the wallet from the peer group
      if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
        peerGroup.removeWallet(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet());
      }

      peerGroup.stopAsync();
      log.debug("Service peerGroup stopped");
    }
  }

  /**
   * Restart the network, using the current wallet (specifically the blockstore)
   *
   * @throws BlockStoreException                   If the block store fails
   * @throws IOException                           If the network fails
   * @throws java.util.concurrent.TimeoutException If the TOR connection fails
   */
  private void restartNetwork() throws BlockStoreException, IOException, TimeoutException {

    // Check if there is a network connection
    if (!isNetworkPresent()) {
      return;
    }

    CoreEvents.fireBitcoinNetworkChangedEvent(BitcoinNetworkSummary.newNetworkNotInitialised());

    log.debug("Creating block chain ...");
    blockChain = new BlockChain(networkParameters, blockStore);

    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
      Wallet wallet = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWallet();
      blockChain.addWallet(wallet);
    }
    log.debug("Created block chain '{}' with height '{}'", blockChain, blockChain.getBestChainHeight());

    log.debug("Creating peer group ...");
    createNewPeerGroup();
    log.debug("Created peer group '{}'", peerGroup);

    log.debug("Starting peer group ...");
    peerGroup.startAsync();
    log.debug("Started peer group.");

    startedOk = true;
  }

  /**
   * <p>Save the current wallet to application directory, create a rolling backup and a cloud backup</p>
   */
  private void saveWallet() {

    // Save the current wallet immediately
    if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {

      WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();
      WalletId walletId = walletSummary.getWalletId();
      log.debug("Saving wallet with id '" + walletId + "'.");

      try {
        File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
        File currentWalletFile = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get();
        walletSummary.getWallet().saveToFile(currentWalletFile);
        File encryptedAESCopy = EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(currentWalletFile, walletSummary.getPassword());
        log.debug("Created AES encrypted wallet as file '{}', size {}", encryptedAESCopy == null ? "null" : encryptedAESCopy.getAbsolutePath(),
          encryptedAESCopy == null ? "null" : encryptedAESCopy.length());

        BackupService backupService = CoreServices.getOrCreateBackupService();
        backupService.rememberWalletSummaryAndPasswordForRollingBackup(walletSummary, walletSummary.getPassword());
        backupService.rememberWalletIdAndPasswordForLocalZipBackup(walletSummary.getWalletId(), walletSummary.getPassword());
        backupService.rememberWalletIdAndPasswordForCloudZipBackup(walletSummary.getWalletId(), walletSummary.getPassword());

      } catch (IOException ioe) {
        log.error("Could not write wallet and backups for wallet with id '" + walletId + "' successfully. The error was '" + ioe.getMessage() + "'");
      }
    }
  }

  /**
   * Calculate the size of the transaction
   *
   * @param transaction The transaction to calculate the size of
   *
   * @return size of the transaction
   */
  private int calculateSize(Transaction transaction) throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    transaction.bitcoinSerialize(byteOutputStream);
    return byteOutputStream.size();
  }

  public Optional<SendRequestSummary> getLastSendRequestSummaryOptional() {
    return lastSendRequestSummaryOptional;
  }

  public Optional<Wallet> getLastWalletOptional() {
    return lastWalletOptional;
  }

  public void setLastSendRequestSummaryOptional(Optional<SendRequestSummary> lastSendRequestSummaryOptional) {
    this.lastSendRequestSummaryOptional = lastSendRequestSummaryOptional;
  }

  public void setLastWalletOptional(Optional<Wallet> lastWalletOptional) {
    this.lastWalletOptional = lastWalletOptional;
  }
}