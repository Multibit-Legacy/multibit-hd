package org.multibit.hd.testing;

import org.joda.time.DateTime;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.WalletManager;

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

  private static final String EMPTY_WALLET_SEED_PHRASE = "laundry code later tower memory close truly stomach note kid machine aunt";

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
    long nowInSeconds = (long)(DateTime.now().getMillis() * 0.001);

    return walletManager.createWalletSummary(seed, nowInSeconds, password);


  }


}
