package org.multibit.hd.testing;

import com.google.common.io.ByteStreams;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.files.ZipFiles;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.Dates;

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
 * @since 0.0.1
 *
 */
public class WalletFixtures {

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

  /**
   * <p>Create an empty wallet in the current installation directory</p>
   *
   * @return The wallet summary if successful
   */
  public static WalletSummary createEmptyWalletFixture() throws IOException {

    Bip39SeedPhraseGenerator seedPhraseGenerator = new Bip39SeedPhraseGenerator();

    List<String> seedPhrase = Bip39SeedPhraseGenerator.split(EMPTY_WALLET_SEED_PHRASE);

    WalletManager walletManager = WalletManager.INSTANCE;
    byte[] seed = seedPhraseGenerator.convertToSeed(seedPhrase);

    long nowInSeconds = Dates.nowInSeconds();

    return walletManager.createSoftWalletSummary(seed, nowInSeconds, STANDARD_PASSWORD, "Example", "Example empty wallet. Password is '" + STANDARD_PASSWORD + "'.", false);

  }

  /**
   * <p>Create a standard wallet in the current installation directory containing known transactions</p>
   * <p>This is required when we want to examine real transactions in the payments screen</p>
   */
  public static void createStandardWalletFixture() throws IOException {

    String zipFileName = InstallationManager
      .getOrCreateApplicationDataDirectory()
      .getAbsolutePath() + "/mbhd-" + STANDARD_WALLET_ID + ".zip";

    String applicationDirectoryName = InstallationManager
      .getOrCreateApplicationDataDirectory()
      .getAbsolutePath();

    try (InputStream is = WalletFixtures.class.getResourceAsStream("/fixtures/mbhd-" + STANDARD_WALLET_ID + ".zip");
         FileOutputStream fos = new FileOutputStream(zipFileName)) {

      // Extract the ZIP of the standard wallet
      ByteStreams.copy(is, fos);
      ZipFiles.unzip(zipFileName, applicationDirectoryName);

    }

  }

}
