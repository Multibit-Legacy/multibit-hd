package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.MnemonicCode;
import org.junit.Before;
import org.junit.Test;
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
import org.multibit.hd.core.managers.HttpsManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.Dates;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class CoreServicesTest {

  private static final String PASSWORD = "bingAbongADingDong";

//  @Test
//     public void testCalculateFeeStateWithRealURL() throws Exception {
//
//       // Get the FeeService
//       FeeService feeService = BRITServices.newFeeService(encryptionKey, new URL(CoreServices.));
//       assertThat(feeService).isNotNull();
//
//       // Perform an exchange with the BRIT Matcher to get the list of fee addresses
//       feeService.performExchangeWithMatcher(seed, wallet1);
//       assertThat(wallet1.getExtensions().get(MatcherResponseWalletExtension.MATCHER_RESPONSE_WALLET_EXTENSION_ID)).isNotNull();
//
//       // Calculate the fee state for an empty wallet
//       FeeState feeState = feeService.calculateFeeState(wallet1, false);
//       assertThat(feeState).isNotNull();
//
//       // We are using a dummy Matcher so will always fall back to the hardwired addresses
//       Set<Address> possibleNextFeeAddresses = feeService.getHardwiredFeeAddresses();
//
//       checkFeeState(feeState, true, 0, Coin.ZERO, FeeService.FEE_PER_SEND, possibleNextFeeAddresses);
//
//       // Receive some bitcoin to the wallet1 address
//       receiveATransaction(wallet1, toAddress1);
//
//       final int NUMBER_OF_NON_FEE_SENDS = 40;
//       for (int i = 0; i < NUMBER_OF_NON_FEE_SENDS; i++) {
//         // Create a send to the non fee destination address
//         // This should increment the send count and the fee owed
//         Coin tenMillis = parseCoin("0.01");
//         sendBitcoin(tenMillis, nonFeeDestinationAddress, null);
//
//         feeState = feeService.calculateFeeState(wallet1, false);
//
//         checkFeeState(feeState, true, 1 + i, FeeService.FEE_PER_SEND.multiply(i + 1), FeeService.FEE_PER_SEND, possibleNextFeeAddresses);
//       }
//
//       // Create another send to the FEE address
//       // Pay the feeOwed and another fee amount (to pay for this send)
//       // This should reset the amount owed and create another feeAddress
//       sendBitcoin(feeState.getFeeOwed().add(FeeService.FEE_PER_SEND), feeState.getNextFeeAddress(), null);
//
//       feeState = feeService.calculateFeeState(wallet1, false);
//       checkFeeState(feeState, true, NUMBER_OF_NON_FEE_SENDS + 1, Coin.ZERO, FeeService.FEE_PER_SEND, possibleNextFeeAddresses);
//     }


  @Before
  public void setUp() throws Exception {

  }

  /**
   * Run a test that exercise the BRIT exchange with a Matcher on multibit.org
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
                  null, false // Do not force loading if they are already present
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
