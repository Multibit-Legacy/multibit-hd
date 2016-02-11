package org.multibit.hd.core.wallet;

/**
 * Copyright 2015 multibit.org
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

import com.google.common.base.Optional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.MnemonicCode;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.commons.files.SecureFiles;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.brit.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.WalletIdTest;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.bitcoinj.core.Coin.COIN;
import static org.bitcoinj.testing.FakeTxBuilder.createFakeTx;
import static org.fest.assertions.Assertions.assertThat;


public class UnconfirmedTransactionDetectorTest {

  private static final Logger log = LoggerFactory.getLogger(UnconfirmedTransactionDetectorTest.class);

  private NetworkParameters mainNet;

  private Wallet wallet;

  private ECKey myKey;
  private Address myAddress;

  @SuppressFBWarnings({"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", "NP_NONNULL_PARAM_VIOLATION"})
  @Before
  public void setUp() throws Exception {
    InstallationManager.unrestricted = true;

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    // Start the core services
    CoreServices.main(null);

    mainNet = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);
    assertThat(mainNet).isNotNull();
  }

  @After
  public void tearDown() throws Exception {
    // Order is important here
    CoreServices.shutdownNow(ShutdownEvent.ShutdownType.SOFT);

    InstallationManager.shutdownNow(ShutdownEvent.ShutdownType.SOFT);
    BackupManager.INSTANCE.shutdownNow();
    WalletManager.INSTANCE.shutdownNow(ShutdownEvent.ShutdownType.HARD);
  }

  @Test
  public void testWindowOfInterest() throws Exception {
    // Get the application directory
    File applicationDirectory = SecureFiles.createTemporaryDirectory();

    WalletManager walletManager = WalletManager.INSTANCE;
    BackupManager.INSTANCE.initialise(applicationDirectory, Optional.<File>absent());

    byte[] entropy = MnemonicCode.INSTANCE.toEntropy(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    long nowInSeconds = Dates.nowInSeconds();

    WalletSummary walletSummary = walletManager
            .getOrCreateMBHDSoftWalletSummaryFromEntropy(
                    applicationDirectory,
                    entropy,
                    seed,
                    nowInSeconds,
                    "credentials",
                    "Example",
                    "Example",
                    false); // No need to sync

    assertThat(walletSummary).isNotNull();
    assertThat(walletSummary.getWallet()).isNotNull();

    wallet = walletSummary.getWallet();

    myKey = wallet.currentReceiveKey();
    myAddress = myKey.toAddress(mainNet);

    DateTime now = Dates.nowUtc();

    // There are no unconfirmed transactions in the wallet so there should be no replay date
    Optional<DateTime> replayDate = UnconfirmedTransactionDetector.calculateReplayDate(wallet, now);
    assertThat(!replayDate.isPresent()).isTrue();

    // Receive some unconfirmed coin
    Transaction unconfirmedTransaction = sendMoneyToWallet();
    assertThat(unconfirmedTransaction).isNotNull();

    // The unconfirmed transaction is dated "now" so WILL NOT in the window of interest (4 hours old to 4 days old)
    replayDate = UnconfirmedTransactionDetector.calculateReplayDate(wallet, now);
    assertThat(!replayDate.isPresent()).isTrue();

    // The unconfirmed transaction WILL be of interest in 4 hours plus a minute
    replayDate = UnconfirmedTransactionDetector.calculateReplayDate(wallet, now.plus(Hours.FOUR).plus(Minutes.ONE));
    assertThat(replayDate.isPresent()).isTrue();
    assertThat(replayDate.get().toDate()).isEqualTo(unconfirmedTransaction.getUpdateTime());

    // The unconfirmed transaction WILL be of interest in 4 days minus a minute
    replayDate = UnconfirmedTransactionDetector.calculateReplayDate(wallet, now.plus(Days.FOUR).minus(Minutes.ONE));
    assertThat(replayDate.isPresent()).isTrue();
    assertThat(replayDate.get().toDate()).isEqualTo(unconfirmedTransaction.getUpdateTime());

    // The unconfirmed transaction WILL NOT be of interest in 4 days plus a minute
    replayDate = UnconfirmedTransactionDetector.calculateReplayDate(wallet, now.plus(Days.FOUR).plus(Minutes.ONE));
    assertThat(!replayDate.isPresent()).isTrue();
  }


  private Transaction sendMoneyToWallet() throws VerificationException {
    Transaction tx = createFakeTx(mainNet, COIN, myAddress);

    // Pending/broadcast tx.
    if (wallet.isPendingTransactionRelevant(tx)) {
      wallet.receivePending(tx, null);
    }

    return wallet.getTransaction(tx.getHash());  // Can be null if tx is a double spend that's otherwise irrelevant.
  }
}

