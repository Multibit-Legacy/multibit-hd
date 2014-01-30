package org.multibit.hd.core.managers;

/**
 * Copyright 2013 multibit.org
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

import com.google.common.util.concurrent.Uninterruptibles;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.api.WalletData;
import org.multibit.hd.core.api.WalletId;
import org.multibit.hd.core.api.WalletIdTest;
import org.multibit.hd.core.api.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BackupManagerTest extends TestCase {
  @Before
  @Override
  public void setUp() throws Exception {
  }


  @Test
  public void testBackupWallet() throws IOException {
    // Create a random temporary directory in which to store the wallet directory
    File temporaryWalletParentDirectory = WalletManagerTest.makeRandomTemporaryDirectory();

    WalletManager walletManager = WalletManager.INSTANCE;

    // Create a random temporary directory in which to story the cloudBackups
    File temporaryBackupDirectory = WalletManagerTest.makeRandomTemporaryDirectory();

    BackupManager backupManager = BackupManager.INSTANCE;

    // Initialise the backupManager to point at the temporaryBackupDirectory
    backupManager.initialise(temporaryWalletParentDirectory, temporaryBackupDirectory);

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));

    WalletData walletData = walletManager.createWallet(temporaryWalletParentDirectory.getAbsolutePath(), seed, "password");

    // Check there are initially a single wallet backup for the wallet id of the created wallet
    List<File> localBackups = BackupManager.INSTANCE.getLocalZipBackups(walletData.getWalletId());
    assertNotNull("Null localBackups list returned", localBackups);
    assertEquals("Wrong number of localBackups", 1, localBackups.size());

    List<File> cloudBackups = BackupManager.INSTANCE.getCloudBackups(walletData.getWalletId());
    assertNotNull("Null cloudBackups list returned", cloudBackups);
    assertEquals("Wrong number of cloudBackups", 1, cloudBackups.size());

    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

    // Backup the wallet.
    // This zips the wallet root directory and adds a timestamp, then saves the file in both the local and cloud backup directories
    File localBackupFile = BackupManager.INSTANCE.createLocalAndCloudBackup(walletData.getWalletId());

    // Check that a backup copy has been saved in the local backup directory
    localBackups = BackupManager.INSTANCE.getLocalZipBackups(walletData.getWalletId());
    assertNotNull("Null localBackups list returned", localBackups);
    assertEquals("Wrong number of localBackups", 2, localBackups.size());

    // Check that a backup copy has been saved in the cloud backup directory
    cloudBackups = BackupManager.INSTANCE.getCloudBackups(walletData.getWalletId());
    assertNotNull("Null cloudBackups list returned", cloudBackups);
    assertEquals("Wrong number of cloudBackups", 2, cloudBackups.size());

    // Load in the wallet backup and compare the wallets
    WalletId recreatedWalletId= BackupManager.INSTANCE.loadBackup(localBackupFile);

    assertEquals("Recreated local backup wallet id not the same as original", walletData.getWalletId(), recreatedWalletId);

    String walletFilename = WalletManager.getWalletDirectory(temporaryWalletParentDirectory.getAbsolutePath(), WalletManager.createWalletRoot(recreatedWalletId)) + File.separator + WalletManager.MBHD_WALLET_NAME;
    WalletData recreatedWalletData = walletManager.loadFromFile(new File(walletFilename));

    // Check there is the same key in the original wallet as in the recreated one
    assertEquals("Wallet was not roundtripped correctly", walletData.getWallet().getKeys().get(0).toStringWithPrivate(), recreatedWalletData.getWallet().getKeys().get(0).toStringWithPrivate());
  }
}

