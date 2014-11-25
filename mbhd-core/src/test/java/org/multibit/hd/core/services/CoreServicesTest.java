package org.multibit.hd.core.services;

import org.bitcoinj.core.Wallet;
import org.junit.Before;
import org.junit.Ignore;
import org.multibit.hd.brit.dto.FeeState;
import org.multibit.hd.brit.dto.MatcherResponse;
import org.multibit.hd.brit.dto.SendFeeDto;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.services.FeeService;
import org.multibit.hd.core.dto.WalletIdTest;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.files.SecureFiles;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.Dates;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class CoreServicesTest {

  private static final String PASSWORD = "bingAbongADingDong";

  @Before
  public void setUp() throws Exception {

  }

  /**
   * Run a test that exercise the BRIT exchange with a Matcher
   * If you are running locally on http://localhost:9090 the BRIT server (in the brit-server repo)
   * it will talk to that.
   *
   * If ou are NOT running the BRIT server the exchange fails and the feeService should return the
   * hardwired list of addresses (which will then be used for sending fees to).
   * @throws Exception
   *
   * TODO (JB) Getting failures with 0 addresses returned
   *
   */
  @Ignore
  public void testCreateFeeService() throws Exception {
    FeeService feeService = CoreServices.createFeeService();
    assertThat(feeService).isNotNull();

    // Create a random temporary directory where the wallet directory will be written
    File temporaryDirectory = SecureFiles.createTemporaryDirectory();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));

    BackupManager.INSTANCE.initialise(temporaryDirectory, null);

    long nowInSeconds = Dates.nowInSeconds();
    WalletSummary walletSummary = WalletManager
      .INSTANCE
      .getOrCreateWalletSummaryFromSeed(
              temporaryDirectory,
              seed,
              nowInSeconds,
              PASSWORD,
              "Example",
              "Example"
      );

    Wallet wallet  = walletSummary.getWallet();

    // The wallet should not have any BRIT related data yet.
    MatcherResponse matcherResponse = FeeService.getMatcherResponseFromWallet(wallet);
    assertThat(matcherResponse).isNull();

    SendFeeDto sendFeeDto = FeeService.getSendFeeDtoFromWallet(wallet);
    assertThat(sendFeeDto).isNull();

    // Perform a BRIT exchange
    feeService.performExchangeWithMatcher(seed, wallet);

    // Should now have a MatcherResponse
    matcherResponse = FeeService.getMatcherResponseFromWallet(wallet);
    assertThat(matcherResponse).isNotNull();
    assertThat(matcherResponse.getBitcoinAddresses()).isNotNull();
    assertThat(matcherResponse.getBitcoinAddresses().size()).isGreaterThan(0);

    // There still should be any SendFeeDto
    sendFeeDto = FeeService.getSendFeeDtoFromWallet(wallet);
    assertThat(sendFeeDto).isNull();

    // The wallet is empty but you can still calculate the feeDto. Let's do that now
    FeeState feeState = feeService.calculateFeeState(wallet, false);
    assertThat(feeState).isNotNull();

    // There should now be some fee information in the wallet
    sendFeeDto = FeeService.getSendFeeDtoFromWallet(wallet);
    assertThat(sendFeeDto).isNotNull();
  }
}
