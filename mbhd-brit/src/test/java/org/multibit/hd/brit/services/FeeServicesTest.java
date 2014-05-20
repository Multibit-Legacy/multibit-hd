package org.multibit.hd.brit.services;

/**
 * Copyright 2014 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.bitcoin.core.*;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.MemoryBlockStore;
import com.google.bitcoin.utils.Threading;
import com.google.bitcoin.wallet.DeterministicSeed;
import com.google.bitcoin.wallet.KeyChainGroup;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.multibit.hd.brit.crypto.PGPUtilsTest;
import org.multibit.hd.brit.dto.BRITWalletIdTest;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.extensions.MatcherResponseWalletExtension;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.util.LinkedList;
import java.util.Set;

import static com.google.bitcoin.core.Utils.toNanoCoins;
import static org.fest.assertions.Assertions.assertThat;

public class FeeServicesTest {

  private static final Logger log = LoggerFactory.getLogger(FeeServicesTest.class);

  /**
   * Always use MainNet in BRIT
   */
  private static final NetworkParameters NETWORK_PARAMETERS = MainNetParams.get();

  private final CharSequence WALLET_PASSWORD = "horatio nelson 123";
  private Wallet wallet1;

  /**
   * An address in the test wallet1
   */
  private Address toAddress1;

  /**
   * An address that is not in the wallet1 and not a BRIT fee address
   */
  private Address nonFeeDestinationAddress;

  protected BlockChain chain;
  protected BlockStore blockStore;

  private PGPPublicKey encryptionKey;

  /**
   * A dummy URL - forces the hardwired list of addresses to be used
   */
  private static final String DUMMY_MATCHER_URL = "http://nowhere.com";

  private byte[] seed;

  ECKey toKey1;

  @Before
  public void setUp() throws Exception {

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(BRITWalletIdTest.SEED_PHRASE_1));

    // Create the wallet 'wallet1'
    createWallet(seed, WALLET_PASSWORD);

    toKey1 = wallet1.freshReceiveKey();
    toAddress1 = toKey1.toAddress(NETWORK_PARAMETERS);

    // Read the manually created public keyring in the test directory to find a public key suitable for encryption
    File publicKeyRingFile = PGPUtilsTest.makeFile(PGPUtilsTest.TEST_MATCHER_PUBLIC_KEYRING_FILE);
    log.debug("Loading public keyring from '" + publicKeyRingFile.getAbsolutePath() + "'");
    FileInputStream publicKeyRingInputStream = new FileInputStream(publicKeyRingFile);
    encryptionKey = PGPUtils.readPublicKey(publicKeyRingInputStream);
    assertThat(encryptionKey).isNotNull();

    blockStore = new MemoryBlockStore(NETWORK_PARAMETERS);
    chain = new BlockChain(NETWORK_PARAMETERS, wallet1, blockStore);

    nonFeeDestinationAddress = new Address(NETWORK_PARAMETERS, "1CQH7Hp9nNQVDcKtFVwbA8tqPMNWDBvqE3"); // Any old address that is not a fee address
  }

  @Test
  public void testCalculateFeeState() throws Exception {
    // Get the FeeService
    FeeService feeService = BRITServices.newFeeService(encryptionKey, new URL(DUMMY_MATCHER_URL));
    assertThat(feeService).isNotNull();

    // Perform an exchange with the BRIT Matcher to get the list of fee addresses
    feeService.performExchangeWithMatcher(seed, wallet1);
    assertThat(wallet1.getExtensions().get(MatcherResponseWalletExtension.MATCHER_RESPONSE_WALLET_EXTENSION_ID)).isNotNull();

    // Calculate the fee state for an empty wallet
    FeeState feeState = feeService.calculateFeeState(wallet1, false);
    assertThat(feeState).isNotNull();

    // We are using a dummy Matcher so will always fall back to the hardwired addresses
    Set<Address> possibleNextFeeAddresses = feeService.getHardwiredFeeAddresses();

    checkFeeState(feeState, true, 0, BigInteger.ZERO, FeeService.FEE_PER_SEND, possibleNextFeeAddresses);

    // Receive some bitcoin to the wallet1 address
    receiveATransaction(wallet1, toAddress1);

    final int NUMBER_OF_NON_FEE_SENDS = 40;
    for (int i = 0; i < NUMBER_OF_NON_FEE_SENDS; i++) {
      // Create a send to the non fee destination address
      // This should increment the send count and the fee owed
      BigInteger tenMillis = toNanoCoins(0, 1);
      sendBitcoin(tenMillis, nonFeeDestinationAddress, null);

      feeState = feeService.calculateFeeState(wallet1, false);

      checkFeeState(feeState, true, 1 + i, FeeService.FEE_PER_SEND.multiply(BigInteger.valueOf(i + 1)), FeeService.FEE_PER_SEND, possibleNextFeeAddresses);
    }

    // Create another send to the FEE address
    // Pay the feeOwed and another fee amount (to pay for this send)
    // This should reset the amount owed and create another feeAddress
    sendBitcoin(feeState.getFeeOwed().add(FeeService.FEE_PER_SEND), feeState.getNextFeeAddress(), null);

    feeState = feeService.calculateFeeState(wallet1, false);
    checkFeeState(feeState, true, NUMBER_OF_NON_FEE_SENDS + 1, BigInteger.ZERO, FeeService.FEE_PER_SEND, possibleNextFeeAddresses);
  }

  private void checkFeeState(FeeState feeState,
                             boolean expectedIsUsingHardwiredBRITAddress,
                             int expectedCurrentNumberOfSends,
                             BigInteger expectedFeeOwed,
                             BigInteger expectedFeePerSendSatoshi,
                             Set<Address> possibleNextFeeAddresses) {

    assertThat(feeState.isUsingHardwiredBRITAddresses() == expectedIsUsingHardwiredBRITAddress).isTrue();
    assertThat(feeState.getCurrentNumberOfSends()).isEqualTo(expectedCurrentNumberOfSends);
    assertThat(feeState.getFeeOwed()).isEqualTo(expectedFeeOwed);
    assertThat(feeState.getFeePerSendSatoshi()).isEqualTo(expectedFeePerSendSatoshi);
    assertThat(possibleNextFeeAddresses.contains(feeState.getNextFeeAddress())).isTrue();

    int upperLimitOfNextFeeSendCount = feeState.getCurrentNumberOfSends() + FeeService.NEXT_SEND_DELTA_UPPER_LIMIT - 1;

    // Verify limits
    assertThat(upperLimitOfNextFeeSendCount).isGreaterThanOrEqualTo(feeState.getNextFeeSendCount());
  }

  public void createWallet(byte[] seed, CharSequence password) throws Exception {
    DeterministicSeed deterministicSeed = new DeterministicSeed(seed, DateTime.now().getMillis());
    KeyChainGroup keyChainGroup = new KeyChainGroup(deterministicSeed);

    wallet1 = new Wallet(MainNetParams.get(), keyChainGroup);
  }

  private void receiveATransaction(Wallet wallet, Address toAddress) throws Exception {
    BigInteger v1 = Utils.toNanoCoins(1, 0);
    final ListenableFuture<BigInteger> availFuture = wallet.getBalanceFuture(v1, Wallet.BalanceType.AVAILABLE);
    final ListenableFuture<BigInteger> estimatedFuture = wallet.getBalanceFuture(v1, Wallet.BalanceType.ESTIMATED);
    assertThat(availFuture.isDone()).isFalse();
    assertThat(estimatedFuture.isDone()).isFalse();

    // Send some pending coins to the wallet.
    Transaction t1 = sendMoneyToWallet(wallet, v1, toAddress);
    Threading.waitForUserCode();
    final ListenableFuture<Transaction> depthFuture = t1.getConfidence().getDepthFuture(1);
    assertThat(depthFuture.isDone()).isFalse();
    assertThat(v1).isEqualTo(wallet.getBalance(Wallet.BalanceType.ESTIMATED));

    // Our estimated balance has reached the requested level.
    assertThat(estimatedFuture.isDone()).isTrue();
  }

  private Transaction sendMoneyToWallet(Wallet wallet, BigInteger value, Address toAddress) throws IOException, VerificationException {
    Preconditions.checkNotNull(toAddress);

    // If the next line isn't compiling you probably need to update your bitcoinj !
    // createFakeTx has been moved !
    Transaction tx = com.google.bitcoin.testing.FakeTxBuilder.createFakeTx(NETWORK_PARAMETERS, value, toAddress);
    // Mark it as coming from self as then it can be spent when pending
    tx.getConfidence().setSource(TransactionConfidence.Source.SELF);

    // Mark it as being seen by a couple of peers
    tx.getConfidence().markBroadcastBy(new PeerAddress(InetAddress.getByAddress(new byte[]{1, 2, 3, 4})));
    tx.getConfidence().markBroadcastBy(new PeerAddress(InetAddress.getByAddress(new byte[]{10, 2, 3, 4})));

    // Pending/broadcast tx.
    if (wallet.isPendingTransactionRelevant(tx)) {
      wallet.receivePending(tx, null);
    }

    return wallet.getTransaction(tx.getHash());  // Can be null if tx is a double spend that's otherwise irrelevant.
  }

  private static void broadcastAndCommit(Wallet wallet, Transaction t) throws Exception {
    final LinkedList<Transaction> txns = Lists.newLinkedList();
    wallet.addEventListener(new AbstractWalletEventListener() {
      @Override
      public void onCoinsSent(Wallet wallet, Transaction tx, BigInteger prevBalance, BigInteger newBalance) {
        txns.add(tx);
      }
    });

    t.getConfidence().markBroadcastBy(new PeerAddress(InetAddress.getByAddress(new byte[]{1, 2, 3, 4})));
    t.getConfidence().markBroadcastBy(new PeerAddress(InetAddress.getByAddress(new byte[]{10, 2, 3, 4})));
    wallet.commitTx(t);
    Threading.waitForUserCode();
  }

  private void sendBitcoin(BigInteger amount, Address destinationAddress, KeyParameter aesKey) throws Exception {
    Wallet.SendRequest req = Wallet.SendRequest.to(destinationAddress, amount);
    req.aesKey = aesKey;
    req.fee = toNanoCoins(0, 1);
    req.ensureMinRequiredFee = false;

    // Complete the transaction successfully.
    wallet1.completeTx(req);

    // Broadcast the transaction and commit.
    broadcastAndCommit(wallet1, req.tx);
  }
}

