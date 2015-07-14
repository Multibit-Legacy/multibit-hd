package org.multibit.hd.integration;

import com.google.common.base.Optional;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.MnemonicCode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.multibit.commons.files.SecureFiles;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.brit.core.dto.FeeState;
import org.multibit.hd.brit.core.dto.MatcherResponse;
import org.multibit.hd.brit.core.dto.SendFeeDto;
import org.multibit.hd.brit.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.core.services.FeeService;
import org.multibit.hd.core.dto.WalletIdTest;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.HttpsManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

@Ignore
public class BRITExchangeIntegrationTest {

  private static final String PASSWORD = "bingAbongADingDong";

  @Before
  public void setUp() throws Exception {

  }

  /**
   * Run an integration test that exercise the BRIT exchange with a Matcher on multibit.org
   *
   * If you are NOT running the BRIT server the exchange fails and the feeService should return the
   * hardwired list of addresses (which will then be used for sending fees to).
   * @throws Exception
   *
   */
  @Test
  public void testCreateFeeServiceAndPerformBRITExchange() throws Exception {
    FeeService feeService = CoreServices.createFeeService();
    assertThat(feeService).isNotNull();

    // Create a random temporary directory where the wallet directory will be written
    File temporaryInstallationDirectory = SecureFiles.createTemporaryDirectory();

    HttpsManager.INSTANCE.installCACertificates(
      temporaryInstallationDirectory,
      InstallationManager.CA_CERTS_NAME,
      null, // Use default host list
      true // Force loading since they won't be present
    );

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] entropy = MnemonicCode.INSTANCE.toEntropy(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    byte[] seed = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));

    BackupManager.INSTANCE.initialise(temporaryInstallationDirectory, Optional.<File>absent());

    long nowInSeconds = Dates.nowInSeconds();
    WalletSummary walletSummary = WalletManager
      .INSTANCE
      .getOrCreateMBHDSoftWalletSummaryFromEntropy(
              temporaryInstallationDirectory,
              entropy,
              seed,
              nowInSeconds,
              PASSWORD,
              "Example",
              "Example",
              false); // No need to sync

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

    // There still should be a SendFeeDto
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
