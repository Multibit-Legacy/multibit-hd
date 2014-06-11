package org.multibit.hd.testing;

import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.Dates;

import java.io.IOException;
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
   * Contains public transactions with timestamp of 1949/09
   */
  private static final String RESTORED_WALLET_SEED_PHRASE = "refuse prison until practice update garlic apology social observe nuclear other daring";

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
    CharSequence password = "abc123";
    long nowInSeconds = Dates.nowInSeconds();

    return walletManager.createWalletSummary(seed, nowInSeconds, password, "Example", "Example empty wallet. Password is abc123.");

  }

  /**
   * <p>Create a restored wallet in the current installation directory containing known transactions</p>
   *
   * @return The wallet summary if successful
   */
  public static WalletSummary createRestoredWalletFixture() throws IOException {

    Bip39SeedPhraseGenerator seedPhraseGenerator = new Bip39SeedPhraseGenerator();

    List<String> seedPhrase = Bip39SeedPhraseGenerator.split(RESTORED_WALLET_SEED_PHRASE);

    WalletManager walletManager = WalletManager.INSTANCE;
    byte[] seed = seedPhraseGenerator.convertToSeed(seedPhrase);
    CharSequence password = "abc123";
    long nowInSeconds = (long) (Dates.parseSeedTimestamp("1949/09").getMillis() * 0.001);

    return walletManager.createWalletSummary(seed, nowInSeconds, password, "Example", "Example recovered wallet. Password is abc123.");

  }

}
