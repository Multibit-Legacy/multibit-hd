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

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.KeyChain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class WalletManagerTest {

  private static final Logger log = LoggerFactory.getLogger(WalletManagerTest.class);

  private final static String WALLET_DIRECTORY_1 = "mbhd-11111111-22222222-33333333-44444444-55555555";
  private final static String WALLET_DIRECTORY_2 = "mbhd-66666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String EXPECTED_WALLET_ID_1 = "11111111-22222222-33333333-44444444-55555555";
  private final static String EXPECTED_WALLET_ID_2 = "66666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String INVALID_WALLET_DIRECTORY_1 = "not-mbhd-66666666-77777777-88888888-99999999-aaaaaaaa";
  private final static String INVALID_WALLET_DIRECTORY_2 = "mbhd-66666666-77777777-88888888-99999999-gggggggg";
  private final static String INVALID_WALLET_DIRECTORY_3 = "mbhd-1166666666-77777777-88888888-99999999-aaaaaaaa";

  private final static String SIGNING_PASSWORD = "throgmorton999";
  private final static String MESSAGE_TO_SIGN = "The quick brown fox jumps over the lazy dog.\n1234567890!@#$%^&*()";

  private final static String SHORT_PASSWORD = "a"; // 1
  private final static String MEDIUM_PASSWORD = "abcefghijklm"; // 12
  private final static String LONG_PASSWORD = "abcefghijklmnopqrstuvwxyz"; // 26
  private final static String LONGER_PASSWORD = "abcefghijklmnopqrstuvwxyz1234567890"; // 36
  private final static String LONGEST_PASSWORD = "abcefghijklmnopqrstuvwxyzabcefghijklmnopqrstuvwxyz"; // 52

  /**
   * The seed phrase for the Trezor 'Insecure' wallet
   */
  private final static String TREZOR_SEED_PHRASE = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

  // The generated Trezor addresses for the 'Abandon' wallet
  private final static String TREZOR_ADDRESS_M_44H_0H_0H_0_0 = "1LqBGSKuX5yYUonjxT5qGfpUsXKYYWeabA";  // Receiving funds
  private final static String TREZOR_ADDRESS_M_44H_0H_0H_1_0 = "1J3J6EvPrv8q6AC3VCjWV45Uf3nssNMRtH";  // Change
  private final static String TREZOR_ADDRESS_M_44H_0H_0H_0_1 = "1Ak8PffB2meyfYnbXZR9EGfLfFZVpzJvQP";
  private final static String TREZOR_ADDRESS_M_44H_0H_0H_1_1 = "13vKxXzHXXd8HquAYdpkJoi9ULVXUgfpS5";
  private final static String TREZOR_ADDRESS_M_44H_0H_0H_0_2 = "1MNF5RSaabFwcbtJirJwKnDytsXXEsVsNb";
  private final static String TREZOR_ADDRESS_M_44H_0H_0H_1_2 = "1M21Wx1nGrHMPaz52N2En7c624nzL4MYTk";
  private final static String TREZOR_ADDRESS_M_44H_0H_0H_0_3 = "1MVGa13XFvvpKGZdX389iU8b3qwtmAyrsJ";
  private final static String TREZOR_ADDRESS_M_44H_0H_0H_1_3 = "1DzVLMA4HzjXPAr6aZoaacDPHXXntsZ2zL";

  @Before
  public void setUp() throws Exception {

    InstallationManager.reset();
    InstallationManager.unrestricted = true;

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    // Start the core services
    CoreServices.main(null);
  }

  @After
  public void tearDown() throws Exception {

    InstallationManager.reset();

  }

  @Test
  public void testCreateWallet() throws Exception {

    // Get the application directory (will be temporary for unit tests)
    File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    WalletManager walletManager = WalletManager.INSTANCE;
    BackupManager.INSTANCE.initialise(applicationDirectory, null);

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    long nowInSeconds = Dates.nowInSeconds();

    WalletSummary walletSummary1 = walletManager
            .getOrCreateMBHDSoftWalletSummaryFromSeed(
                    applicationDirectory,
                    seed,
                    nowInSeconds,
                    "credentials",
                    "Example",
                    "Example"
            );

    // Uncomment this next line if you want a wallet created in your MultiBitHD user data directory.
    //walletManager.createWallet( seed, "credentials");

    assertThat(walletSummary1).isNotNull();

    // Create another wallet - it should have the same wallet id and the private key should be the same
    File applicationDirectory2 = SecureFiles.createTemporaryDirectory();
    BackupManager.INSTANCE.initialise(applicationDirectory2, null);

    WalletSummary walletSummary2 = walletManager
            .getOrCreateMBHDSoftWalletSummaryFromSeed(
                    applicationDirectory2,
                    seed,
                    nowInSeconds,
                    "credentials",
                    "Example",
                    "Example"
            );

    assertThat(walletSummary2).isNotNull();

    ECKey key1 = walletSummary1.getWallet().freshReceiveKey();
    ECKey key2 = walletSummary2.getWallet().freshReceiveKey();

    assertThat(key1).isEqualTo(key2);

    File expectedFile = new File(
            applicationDirectory2.getAbsolutePath()
                    + File.separator
                    + "mbhd-"
                    + walletSummary2.getWalletId().toFormattedString()
                    + File.separator
                    + WalletManager.MBHD_WALLET_NAME
                    + WalletManager.MBHD_AES_SUFFIX
    );

    assertThat(expectedFile.exists()).isTrue();
    assertThat(WalletType.MBHD_SOFT_WALLET.equals(walletSummary1.getWalletType()));
    assertThat(WalletType.MBHD_SOFT_WALLET.equals(walletSummary2.getWalletType()));
  }

  @Test
  /**
   * Test creation of a Trezor (soft) wallet.
   * This matches the 'Insecure' wallet used in the multibit-hardware project TrezorV1WipeAndLoadWalletExample test
   */
  public void testCreateTrezorWallet() throws Exception {

    // Get the application directory (will be temporary for unit tests)
    File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    WalletManager walletManager = WalletManager.INSTANCE;
    BackupManager.INSTANCE.initialise(applicationDirectory, null);

    long nowInSeconds = Dates.nowInSeconds();

    WalletSummary walletSummary = walletManager.getOrCreateTrezorSoftWalletSummaryFromSeedPhrase(
            applicationDirectory,
            TREZOR_SEED_PHRASE,
            nowInSeconds,
            "aPassword",
            "Abandon",
            "Abandon");

    assertThat(walletSummary).isNotNull();
    assertThat(WalletType.TREZOR_SOFT_WALLET.equals(walletSummary.getWalletType()));

    // Check that the generated addresses match the list of addresses you get directly from the Trezor
    // (Either from myTrezor.com or from the multibit-hardware test TrezorV1GetAddressExample)

    Wallet trezorWallet = walletSummary.getWallet();

    log.debug("TrezorWallet : {}", trezorWallet.toString());

    DeterministicKey trezorKeyM44H_0H_0H_0_0 = trezorWallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    String addressM44H_0H_0H_0_0 = trezorKeyM44H_0H_0H_0_0.toAddress(NetworkParameters.fromID(NetworkParameters.ID_MAINNET)).toString();

    System.out.println("WalletManagerTest - trezorKeyM44H_0H_0H_0_0 = " + trezorKeyM44H_0H_0H_0_0.toString());
    System.out.println("WalletManagerTest - addressM44H_0H_0H_0_0 = " + addressM44H_0H_0H_0_0);

    DeterministicKey trezorKeyM44H_0H_0H_1_0 = trezorWallet.freshKey(KeyChain.KeyPurpose.CHANGE);
    String addressM44H_0H_0H_1_0 = trezorKeyM44H_0H_0H_1_0.toAddress(NetworkParameters.fromID(NetworkParameters.ID_MAINNET)).toString();

    System.out.println("WalletManagerTest - trezorKeyM44H_0H_0H_1_0 = " + trezorKeyM44H_0H_0H_1_0.toString());
    System.out.println("WalletManagerTest - addressM44H_0H_0H_1_0 = " + addressM44H_0H_0H_1_0);

    assertThat(TREZOR_ADDRESS_M_44H_0H_0H_0_0.equals(addressM44H_0H_0H_0_0)).isTrue();
    assertThat(TREZOR_ADDRESS_M_44H_0H_0H_1_0.equals(addressM44H_0H_0H_1_0)).isTrue();

    Address trezorAddressM44H_0H_0H_0_1 = trezorWallet.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    Address trezorAddressM44H_0H_0H_1_1 = trezorWallet.freshAddress(KeyChain.KeyPurpose.CHANGE);
    assertThat(TREZOR_ADDRESS_M_44H_0H_0H_0_1.equals(trezorAddressM44H_0H_0H_0_1.toString())).isTrue();
    assertThat(TREZOR_ADDRESS_M_44H_0H_0H_1_1.equals(trezorAddressM44H_0H_0H_1_1.toString())).isTrue();

    Address trezorAddressM44H_0H_0H_0_2 = trezorWallet.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    Address trezorAddressM44H_0H_0H_1_2 = trezorWallet.freshAddress(KeyChain.KeyPurpose.CHANGE);
    assertThat(TREZOR_ADDRESS_M_44H_0H_0H_0_2.equals(trezorAddressM44H_0H_0H_0_2.toString())).isTrue();
    assertThat(TREZOR_ADDRESS_M_44H_0H_0H_1_2.equals(trezorAddressM44H_0H_0H_1_2.toString())).isTrue();

    Address trezorAddressM44H_0H_0H_0_3 = trezorWallet.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
    Address trezorAddressM44H_0H_0H_1_3 = trezorWallet.freshAddress(KeyChain.KeyPurpose.CHANGE);
    assertThat(TREZOR_ADDRESS_M_44H_0H_0H_0_3.equals(trezorAddressM44H_0H_0H_0_3.toString())).isTrue();
    assertThat(TREZOR_ADDRESS_M_44H_0H_0H_1_3.equals(trezorAddressM44H_0H_0H_1_3.toString())).isTrue();


    File temporaryFile = File.createTempFile("WalletManagerTest", ".wallet");

    // Check the wallet can be reloaded ok i.e. the protobuf round trips
    trezorWallet.saveToFile(temporaryFile);
    File encryptedWalletFile = EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(temporaryFile, "aPassword");

    Wallet rebornWallet = WalletManager.INSTANCE.loadWalletFromFile(encryptedWalletFile, "aPassword");

    // Check the first keys above are in the wallet
    assertThat(rebornWallet.hasKey(trezorKeyM44H_0H_0H_0_0)).isTrue();
    assertThat(rebornWallet.hasKey(trezorKeyM44H_0H_0H_1_0)).isTrue();
   }

  @Test
  public void testFindWalletDirectories() throws Exception {

    // Create a random temporary directory
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    File walletDirectory1 = SecureFiles.verifyOrCreateDirectory(temporaryDirectory, WALLET_DIRECTORY_1);
    File walletDirectory2 = SecureFiles.verifyOrCreateDirectory(temporaryDirectory, WALLET_DIRECTORY_2);
    SecureFiles.verifyOrCreateDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_1);
    SecureFiles.verifyOrCreateDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_2);
    SecureFiles.verifyOrCreateDirectory(temporaryDirectory, INVALID_WALLET_DIRECTORY_3);

    List<File> walletDirectories = WalletManager.findWalletDirectories(temporaryDirectory);
    assertThat(walletDirectories).isNotNull();
    assertThat(walletDirectories.size()).isEqualTo(2);

    // Order of discovery is not guaranteed
    boolean foundWalletPath1First = walletDirectories.get(0).getAbsolutePath().equals(walletDirectory1.getAbsolutePath());

    if (foundWalletPath1First) {
      assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletDirectory1.getAbsolutePath());
      assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletDirectory2.getAbsolutePath());
    } else {
      assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletDirectory1.getAbsolutePath());
      assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletDirectory2.getAbsolutePath());
    }
  }

  @Test
  public void testFindWallets() throws Exception {

    // Create a random temporary directory
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    File walletDirectory1 = SecureFiles.verifyOrCreateDirectory(temporaryDirectory, WALLET_DIRECTORY_1);
    File walletDirectory2 = SecureFiles.verifyOrCreateDirectory(temporaryDirectory, WALLET_DIRECTORY_2);

    List<File> walletDirectories = WalletManager.findWalletDirectories(temporaryDirectory);
    assertThat(walletDirectories).isNotNull();
    assertThat(walletDirectories.size()).isEqualTo(2);

    // Order of discovery is not guaranteed
    boolean foundWalletPath1First = walletDirectories.get(0).getAbsolutePath().equals(walletDirectory1.getAbsolutePath());

    if (foundWalletPath1First) {
      assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletDirectory1.getAbsolutePath());
      assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletDirectory2.getAbsolutePath());
    } else {
      assertThat(walletDirectories.get(1).getAbsolutePath()).isEqualTo(walletDirectory1.getAbsolutePath());
      assertThat(walletDirectories.get(0).getAbsolutePath()).isEqualTo(walletDirectory2.getAbsolutePath());
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

    // Get the application directory (will be temporary for unit tests)
    File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    WalletManager walletManager = WalletManager.INSTANCE;
    BackupManager.INSTANCE.initialise(applicationDirectory, null);

    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    long nowInSeconds = Dates.nowInSeconds();

    log.debug("");
    WalletSummary walletSummary = walletManager.getOrCreateMBHDSoftWalletSummaryFromSeed(
            applicationDirectory,
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
    log.debug("Expect successful signature");
    SignMessageResult signMessageResult = walletManager.signMessage(signingAddress.toString(), MESSAGE_TO_SIGN, SIGNING_PASSWORD);
    assertThat(signMessageResult.isSigningWasSuccessful()).isTrue();
    assertThat(signMessageResult.getSignatureKey()).isEqualTo(CoreMessageKey.SIGN_MESSAGE_SUCCESS);
    assertThat(signMessageResult.getSignatureData()).isNull();
    assertThat(signMessageResult.getSignature().isPresent()).isTrue();
    assertThat(signMessageResult.getSignature().get()).isNotNull();

    // Successfully verify the message
    log.debug("Expect successful verification");
    VerifyMessageResult verifyMessageResult = walletManager.verifyMessage(signingAddress.toString(), MESSAGE_TO_SIGN, signMessageResult.getSignature().get());
    assertThat(verifyMessageResult.isVerifyWasSuccessful()).isTrue();
    assertThat(verifyMessageResult.getVerifyKey()).isEqualTo(CoreMessageKey.VERIFY_MESSAGE_VERIFY_SUCCESS);
    assertThat(verifyMessageResult.getVerifyData()).isNull();

    // Incorrect verify of the message - wrong message
    log.debug("Expect wrong message");
    verifyMessageResult = walletManager.verifyMessage(signingAddress.toString(), MESSAGE_TO_SIGN + "a", signMessageResult.getSignature().get());
    assertThat(verifyMessageResult.isVerifyWasSuccessful()).isFalse();
    assertThat(verifyMessageResult.getVerifyKey()).isEqualTo(CoreMessageKey.VERIFY_MESSAGE_VERIFY_FAILURE);
    assertThat(verifyMessageResult.getVerifyData()).isNull();

    // Incorrect verify of the message - wrong address
    log.debug("Expect wrong address");
    verifyMessageResult = walletManager.verifyMessage(addressNotInWalletString, MESSAGE_TO_SIGN, signMessageResult.getSignature().get());
    assertThat(verifyMessageResult.isVerifyWasSuccessful()).isFalse();
    assertThat(verifyMessageResult.getVerifyKey()).isEqualTo(CoreMessageKey.VERIFY_MESSAGE_VERIFY_FAILURE);
    assertThat(verifyMessageResult.getVerifyData()).isNull();

    // Incorrect verify of the message - wrong signature
    log.debug("Expect bad signature");
    verifyMessageResult = walletManager.verifyMessage(signingAddress.toString(), MESSAGE_TO_SIGN, signMessageResult.getSignature().get() + "b");
    assertThat(verifyMessageResult.isVerifyWasSuccessful()).isFalse();
    assertThat(verifyMessageResult.getVerifyKey()).isEqualTo(CoreMessageKey.VERIFY_MESSAGE_FAILURE);
    assertThat(verifyMessageResult.getVerifyData()).isNull();

    // Bad signing credentials
    log.debug("Expect bad credentials");
    signMessageResult = walletManager.signMessage(signingAddress.toString(), MESSAGE_TO_SIGN, "badPassword");
    assertThat(signMessageResult.isSigningWasSuccessful()).isFalse();
    assertThat(signMessageResult.getSignatureKey()).isEqualTo(CoreMessageKey.SIGN_MESSAGE_NO_PASSWORD);
    assertThat(signMessageResult.getSignatureData()).isNull();
    assertThat(signMessageResult.getSignature().isPresent()).isFalse();

    // Signed with address not in wallet
    log.debug("Expect bad address (not in wallet)");
    signMessageResult = walletManager.signMessage(addressNotInWalletString, MESSAGE_TO_SIGN, SIGNING_PASSWORD);
    assertThat(signMessageResult.isSigningWasSuccessful()).isFalse();
    assertThat(signMessageResult.getSignatureKey()).isEqualTo(CoreMessageKey.SIGN_MESSAGE_NO_SIGNING_KEY);
    assertThat(signMessageResult.getSignatureData()).isEqualTo(new Object[]{addressNotInWalletString});
    assertThat(signMessageResult.getSignature().isPresent()).isFalse();
  }

  @Test
  public void testWriteOfEncryptedPasswordAndSeed() throws Exception {

    List<String> passwordList = Lists.newArrayList();
    passwordList.add(SHORT_PASSWORD);
    passwordList.add(MEDIUM_PASSWORD);
    passwordList.add(LONG_PASSWORD);
    passwordList.add(LONGER_PASSWORD);
    passwordList.add(LONGEST_PASSWORD);

    for (String passwordToCheck : passwordList) {
      // Get the application directory (will be temporary for unit tests)
      File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

      WalletManager walletManager = WalletManager.INSTANCE;
      BackupManager.INSTANCE.initialise(applicationDirectory, null);

      SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
      byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
      long nowInSeconds = Dates.nowInSeconds();


      WalletSummary walletSummary = walletManager
              .getOrCreateMBHDSoftWalletSummaryFromSeed(
                      applicationDirectory,
                      seed,
                      nowInSeconds,
                      passwordToCheck,
                      "Password/seed encryption Example",
                      "Password/seed encryption Example"
              );

      // Check the encrypted wallet credentials and seed are correct
      byte[] foundEncryptedBackupKey = walletSummary.getEncryptedBackupKey();
      byte[] foundEncryptedPaddedPassword = walletSummary.getEncryptedPassword();

      log.debug("Length of padded encrypted credentials = " + foundEncryptedPaddedPassword.length);

      // Check that the encrypted credentials length is always equal to at least 3 x the AES block size of 16 bytes i.e 48 bytes.
      // This ensures that the existence of short passwords is not leaked from the length of the encrypted credentials
      assertThat(foundEncryptedPaddedPassword.length).isGreaterThanOrEqualTo(48);

      KeyParameter seedDerivedAESKey = org.multibit.hd.core.crypto.AESUtils.createAESKey(seed, WalletManager.SCRYPT_SALT);
      byte[] passwordBytes = passwordToCheck.getBytes(Charsets.UTF_8);
      byte[] decryptedFoundPaddedPasswordBytes = org.multibit.hd.brit.crypto.AESUtils.decrypt(foundEncryptedPaddedPassword, seedDerivedAESKey, WalletManager.AES_INITIALISATION_VECTOR);
      byte[] decryptedFoundPasswordBytes = WalletManager.unpadPasswordBytes(decryptedFoundPaddedPasswordBytes);
      assertThat(Arrays.equals(passwordBytes, decryptedFoundPasswordBytes)).isTrue();

      KeyParameter walletPasswordDerivedAESKey = org.multibit.hd.core.crypto.AESUtils.createAESKey(passwordBytes, WalletManager.SCRYPT_SALT);
      byte[] decryptedFoundBackupAESKey = org.multibit.hd.brit.crypto.AESUtils.decrypt(foundEncryptedBackupKey, walletPasswordDerivedAESKey, WalletManager.AES_INITIALISATION_VECTOR);
      assertThat(Arrays.equals(seedDerivedAESKey.getKey(), decryptedFoundBackupAESKey)).isTrue();
    }
  }

}

