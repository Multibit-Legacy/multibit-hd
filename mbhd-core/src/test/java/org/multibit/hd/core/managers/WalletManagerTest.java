package org.multibit.hd.core.managers;

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

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.crypto.DeterministicKey;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class WalletManagerTest {

  private final static String WALLET_DIRECTORY_1 = "mbhd-11111111-22222222-33333333-44444444-55555555";
  private final static String WALLET_DIRECTORY_2 = "mbhd-66666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String EXPECTED_WALLET_ID_1 = "11111111-22222222-33333333-44444444-55555555";
  private final static String EXPECTED_WALLET_ID_2 = "66666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String INVALID_WALLET_DIRECTORY_1 = "not-mbhd-66666666-77777777-88888888-99999999-aaaaaaaa";
  private final static String INVALID_WALLET_DIRECTORY_2 = "mbhd-66666666-77777777-88888888-99999999-gggggggg";
  private final static String INVALID_WALLET_DIRECTORY_3 = "mbhd-1166666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String SIGNING_PASSWORD = "throgmorton999";
  private final static String MESSAGE_TO_SIGN = "The quick brown fox jumps over the lazy dog.\n1234567890!@#$%^&*()";

  @Before
  public void setUp() throws Exception {
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    // Start the core services
    CoreServices.main(null);
  }

  @Test
  public void testCreateWallet() throws Exception {
    // Create a random temporary directory
    File temporaryDirectory1 = makeRandomTemporaryApplicationDirectory();

    WalletManager walletManager = WalletManager.INSTANCE;
    BackupManager.INSTANCE.initialise(temporaryDirectory1, null);

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    long nowInSeconds = Dates.nowInSeconds();

    WalletSummary walletSummary1 = walletManager
      .getOrCreateWalletSummary(
        temporaryDirectory1,
        seed,
        nowInSeconds,
        "password",
        "Example",
        "Example"
      );

    // Uncomment this next line if you want a wallet created in your MultiBitHD user data directory.
    //walletManager.createWallet( seed, "password");

    assertThat(walletSummary1).isNotNull();

    // Create another wallet - it should have the same wallet id and the private key should be the same
    File temporaryDirectory2 = makeRandomTemporaryApplicationDirectory();
    BackupManager.INSTANCE.initialise(temporaryDirectory2, null);

    WalletSummary walletSummary2 = walletManager
      .getOrCreateWalletSummary(
        temporaryDirectory2,
        seed,
        nowInSeconds,
        "password",
        "Example",
        "Example"
      );

    assertThat(walletSummary2).isNotNull();

    ECKey key1 = walletSummary1.getWallet().freshReceiveKey();
    ECKey key2 = walletSummary2.getWallet().freshReceiveKey();

    assertThat(key1).isEqualTo(key2);

    File expectedFile = new File(
      temporaryDirectory2.getAbsolutePath()
        + File.separator
        + "mbhd-"
        + walletSummary2.getWalletId().toFormattedString()
        + File.separator
        + WalletManager.MBHD_WALLET_NAME
        + WalletManager.MBHD_AES_SUFFIX
    );

    assertThat(expectedFile.exists()).isTrue();
  }

  @Test
  public void testFindWalletDirectories() throws Exception {
    // Create a random temporary directory
    File temporaryDirectory = makeRandomTemporaryApplicationDirectory();

    String walletPath1 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_1);
    String walletPath2 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_2);
    makeDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_1);
    makeDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_2);
    makeDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_3);

    List<File> walletDirectories = WalletManager.findWalletDirectories(temporaryDirectory);
    assertThat(walletDirectories).isNotNull();
    assertThat(walletDirectories.size()).isEqualTo(2);

    // Order of discovery is not guaranteed
    boolean foundWalletPath1First = walletDirectories.get(0).getAbsolutePath().equals(walletPath1);

    if (foundWalletPath1First) {
      assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletPath1);
      assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletPath2);
    } else {
      assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletPath1);
      assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletPath2);
    }
  }

  @Test
  public void testFindWallets() throws Exception {
    // Create a random temporary directory
    File temporaryDirectory = makeRandomTemporaryApplicationDirectory();

    String walletPath1 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_1);
    String walletPath2 = makeDirectory(temporaryDirectory, WALLET_DIRECTORY_2);

    List<File> walletDirectories = WalletManager.findWalletDirectories(temporaryDirectory);
    assertThat(walletDirectories).isNotNull();
    assertThat(walletDirectories.size()).isEqualTo(2);

    // Order of discovery is not guaranteed
    boolean foundWalletPath1First = walletDirectories.get(0).getAbsolutePath().equals(walletPath1);

    if (foundWalletPath1First) {
      assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletPath1);
      assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletPath2);
    } else {
      assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletPath1);
      assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletPath2);
    }

    // Attempt to retrieve the wallet summary
    List<WalletSummary> wallets = WalletManager.findWalletSummaries(walletDirectories, Optional.of(WALLET_DIRECTORY_2));
    assertThat(wallets).isNotNull();
    assertThat(wallets.size()).isEqualTo(2);

    // Expect the current wallet root to be first
    assertThat(wallets.get(0).getWalletId().toFormattedString()).isEqualTo(EXPECTED_WALLET_ID_2);
    assertThat(wallets.get(1).getWalletId().toFormattedString()).isEqualTo(EXPECTED_WALLET_ID_1);
  }

  @Test
  public void testSignAndVerifyMessage() throws Exception {
    // Create a random temporary directory
    File temporaryDirectory1 = makeRandomTemporaryApplicationDirectory();

    WalletManager walletManager = WalletManager.INSTANCE;
    BackupManager.INSTANCE.initialise(temporaryDirectory1, null);

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    long nowInSeconds = Dates.nowInSeconds();

    WalletSummary walletSummary = walletManager
      .getOrCreateWalletSummary(
        temporaryDirectory1,
        seed,
        nowInSeconds,
        SIGNING_PASSWORD,
        "Signing Example",
        "Signing Example"
      );

    // Address not in wallet
    ECKey ecKey = new ECKey();
    String addressNotInWalletString = ecKey.toAddress(NetworkParameters.fromID(NetworkParameters.ID_MAINNET)).toString();

    Wallet wallet = walletSummary.getWallet();

    // Create a signing key
    DeterministicKey key = wallet.freshReceiveKey();
    Address signingAddress = key.toAddress(NetworkParameters.fromID(NetworkParameters.ID_MAINNET));

    // Successfully sign the address
    SignMessageResult signMessageResult = walletManager.signMessage(signingAddress.toString(), MESSAGE_TO_SIGN, SIGNING_PASSWORD);
    assertThat(signMessageResult.isSigningWasSuccessful()).isTrue();
    assertThat(signMessageResult.getSignatureKey()).isEqualTo(CoreMessageKey.SIGN_MESSAGE_SUCCESS);
    assertThat(signMessageResult.getSignatureData()).isNull();
    assertThat(signMessageResult.getSignature().isPresent()).isTrue();
    assertThat(signMessageResult.getSignature().get()).isNotNull();

    // Successfully verify the message
    VerifyMessageResult verifyMessageResult = walletManager.verifyMessage(signingAddress.toString(), MESSAGE_TO_SIGN, signMessageResult.getSignature().get());
    assertThat(verifyMessageResult.isVerifyWasSuccessful()).isTrue();
    assertThat(verifyMessageResult.getVerifyKey()).isEqualTo(CoreMessageKey.VERIFY_MESSAGE_VERIFY_SUCCESS);
    assertThat(verifyMessageResult.getVerifyData()).isNull();

    // Incorrect verify of the message - wrong message
    verifyMessageResult = walletManager.verifyMessage(signingAddress.toString(), MESSAGE_TO_SIGN + "a", signMessageResult.getSignature().get());
    assertThat(verifyMessageResult.isVerifyWasSuccessful()).isFalse();
    assertThat(verifyMessageResult.getVerifyKey()).isEqualTo(CoreMessageKey.VERIFY_MESSAGE_VERIFY_FAILURE);
    assertThat(verifyMessageResult.getVerifyData()).isNull();

    // Incorrect verify of the message - wrong address
    verifyMessageResult = walletManager.verifyMessage(addressNotInWalletString, MESSAGE_TO_SIGN, signMessageResult.getSignature().get());
    assertThat(verifyMessageResult.isVerifyWasSuccessful()).isFalse();
    assertThat(verifyMessageResult.getVerifyKey()).isEqualTo(CoreMessageKey.VERIFY_MESSAGE_VERIFY_FAILURE);
    assertThat(verifyMessageResult.getVerifyData()).isNull();

    // Incorrect verify of the message - wrong signature
    verifyMessageResult = walletManager.verifyMessage(signingAddress.toString(), MESSAGE_TO_SIGN, signMessageResult.getSignature().get() + "b");
    assertThat(verifyMessageResult.isVerifyWasSuccessful()).isFalse();
    assertThat(verifyMessageResult.getVerifyKey()).isEqualTo(CoreMessageKey.VERIFY_MESSAGE_FAILURE);
    assertThat(verifyMessageResult.getVerifyData()).isNull();

    // Bad signing password
    signMessageResult = walletManager.signMessage(signingAddress.toString(), MESSAGE_TO_SIGN, "badPassword");
    assertThat(signMessageResult.isSigningWasSuccessful()).isFalse();
    assertThat(signMessageResult.getSignatureKey()).isEqualTo(CoreMessageKey.SIGN_MESSAGE_NO_PASSWORD);
    assertThat(signMessageResult.getSignatureData()).isNull();
    assertThat(signMessageResult.getSignature().isPresent()).isFalse();

    signMessageResult = walletManager.signMessage(addressNotInWalletString, MESSAGE_TO_SIGN, SIGNING_PASSWORD);
    assertThat(signMessageResult.isSigningWasSuccessful()).isFalse();
    assertThat(signMessageResult.getSignatureKey()).isEqualTo(CoreMessageKey.SIGN_MESSAGE_NO_SIGNING_KEY);
    assertThat(signMessageResult.getSignatureData()).isEqualTo(new Object[] {addressNotInWalletString});
    assertThat(signMessageResult.getSignature().isPresent()).isFalse();
  }

  private String makeDirectory(File parentDirectory, String directoryName) {
    File directory = new File(parentDirectory, directoryName);
    assertThat(directory.mkdir()).isTrue();
    directory.deleteOnExit();

    return directory.getAbsolutePath();
  }

  /**
   * @return A random temporary directory suitable for use as an application directory
   *
   * @throws IOException If something goes wrong
   */
  public static File makeRandomTemporaryApplicationDirectory() throws IOException {

    File temporaryDirectory = Files.createTempDir();
    temporaryDirectory.deleteOnExit();

    return temporaryDirectory;
  }
}

