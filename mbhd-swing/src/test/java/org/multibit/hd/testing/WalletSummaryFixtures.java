package org.multibit.hd.testing;

import com.google.common.io.ByteStreams;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.brit.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.files.ZipFiles;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <p>Test wallet fixtures to provide the following to functional tests:</p>
 * <ul>
 * <li>Repeatable wallet scenarios</li>
 * </ul>
 *
 * @since 0.0.5
 *
 */
public class WalletSummaryFixtures {

  /**
   * Always empty - no public transactions so can be set with a recent timestamp
   */
  public static final String EMPTY_WALLET_SEED_PHRASE = "laundry code later tower memory close truly stomach note kid machine aunt";

  /**
   * Empty but contains public transactions with timestamp of 1984/44
   */
  public static final String STANDARD_WALLET_SEED_PHRASE = "twenty lecture clump slush curious aware wise trend surprise soft level coyote";
  public static final String STANDARD_WALLET_ID = "612538c6-b613cdbb-41b31808-d22f83c6-2562f529";
  public static final String STANDARD_TIMESTAMP = "1984/44";

  public static final String STANDARD_PASSWORD = "abc123";
  public static final String ALTERNATIVE_PASSWORD = "def456";

  public static final String ABANDON_SEED_PHRASE = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
  public static final String ABANDON_TREZOR_PASSWORD = "ec406a3c796099050400f65ab311363e";
  public static final String ABANDON_KEEPKEY_PASSWORD = "ec406a3c796099050400f65ab311363e";

  /**
   * <p>Create an empty Trezor hard wallet in the current installation directory</p>
   *
   * @return The wallet summary if successful
   */
  public static WalletSummary createEmptyTrezorHardWalletFixture() throws IOException {

    String applicationDirectoryName = InstallationManager
      .getOrCreateApplicationDataDirectory()
      .getAbsolutePath();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(ABANDON_SEED_PHRASE));

    DeterministicKey privateMasterKey = HDKeyDerivation.createMasterPrivateKey(seed);

    // Trezor uses BIP-44
    // BIP-44 starts from M/44h/0h/0h
    // Create a root node from which all addresses will be generated
    DeterministicKey trezorRootNode = WalletManager.generateTrezorWalletRootNode(privateMasterKey);

    WalletManager walletManager = WalletManager.INSTANCE;

    long nowInSeconds = Dates.nowInSeconds();

    return walletManager.getOrCreateTrezorHardWalletSummaryFromRootNode(
            new File(applicationDirectoryName),
            trezorRootNode,
            nowInSeconds,
            ABANDON_TREZOR_PASSWORD,
            "Example Trezor hard wallet",
            "Example empty wallet. Password is '" + ABANDON_TREZOR_PASSWORD + "'.",
            false); // No need to sync

  }

  /**
   * <p>Create an empty MBHD soft wallet in the current installation directory</p>
   *
   * @return The wallet summary if successful
   */
  public static WalletSummary createEmptyMBHDSoftWalletFixture() throws Exception {

    String applicationDirectoryName = InstallationManager
      .getOrCreateApplicationDataDirectory()
      .getAbsolutePath();

    Bip39SeedPhraseGenerator seedPhraseGenerator = new Bip39SeedPhraseGenerator();

    List<String> seedPhrase = Bip39SeedPhraseGenerator.split(EMPTY_WALLET_SEED_PHRASE);

    WalletManager walletManager = WalletManager.INSTANCE;
    byte[] entropy = MnemonicCode.INSTANCE.toEntropy(seedPhrase);
    byte[] seed = seedPhraseGenerator.convertToSeed(seedPhrase);

    long nowInSeconds = Dates.nowInSeconds();

    return walletManager.getOrCreateMBHDSoftWalletSummaryFromEntropy(
      new File(applicationDirectoryName),
      entropy,
      seed,
      nowInSeconds,
      STANDARD_PASSWORD,
      "Example MBHD soft wallet",
      "Example empty wallet. Password is '" + STANDARD_PASSWORD + "'.",
      false); // No need to sync
  }

  /**
   * <p>Create an empty KeepKey hard wallet in the current installation directory</p>
   *
   * @return The wallet summary if successful
   */
  public static WalletSummary createEmptyKeepKeyHardWalletFixture() throws IOException {

    String applicationDirectoryName = InstallationManager
      .getOrCreateApplicationDataDirectory()
      .getAbsolutePath();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(ABANDON_SEED_PHRASE));

    DeterministicKey privateMasterKey = HDKeyDerivation.createMasterPrivateKey(seed);

    // Trezor uses BIP-44
    // BIP-44 starts from M/44h/0h/0h
    // Create a root node from which all addresses will be generated
    DeterministicKey trezorRootNode = WalletManager.generateTrezorWalletRootNode(privateMasterKey);

    WalletManager walletManager = WalletManager.INSTANCE;

    long nowInSeconds = Dates.nowInSeconds();

    return walletManager.getOrCreateTrezorHardWalletSummaryFromRootNode(
      new File(applicationDirectoryName),
      trezorRootNode,
      nowInSeconds,
      ABANDON_KEEPKEY_PASSWORD,
      "Example KeepKey hard wallet",
      "Example empty wallet. Password is '" + ABANDON_KEEPKEY_PASSWORD + "'.",
      false); // No need to sync

  }

  /**
   * <p>Create a standard wallet in the current installation directory containing known transactions</p>
   * <p>This is required when we want to examine real transactions in the payments screen</p>
   */
  public static void createStandardMBHDSoftWalletFixture() throws IOException {

    String zipFileName = InstallationManager
      .getOrCreateApplicationDataDirectory()
      .getAbsolutePath() + "/mbhd-" + STANDARD_WALLET_ID + ".zip";

    String applicationDirectoryName = InstallationManager
      .getOrCreateApplicationDataDirectory()
      .getAbsolutePath();

    try (InputStream is = WalletSummaryFixtures.class.getResourceAsStream("/fixtures/mbhd-" + STANDARD_WALLET_ID + ".zip");
         FileOutputStream fos = new FileOutputStream(zipFileName)) {

      // Extract the ZIP of the standard wallet
      ByteStreams.copy(is, fos);
      ZipFiles.unzip(zipFileName, applicationDirectoryName);

    }
  }
}
