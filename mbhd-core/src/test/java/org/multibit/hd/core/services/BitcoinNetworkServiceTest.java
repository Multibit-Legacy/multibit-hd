package org.multibit.hd.core.services;

import org.junit.Before;
import org.multibit.hd.core.managers.InstallationManager;

import java.io.File;
import java.io.IOException;

public class BitcoinNetworkServiceTest {

  private BitcoinNetworkService bitcoinNetworkService;

  private File applicationDataDirectory = InstallationManager.createApplicationDataDirectory();

  @Before
  public void setUp() throws IOException {
    CoreServices.main(null);

    bitcoinNetworkService = CoreServices.newBitcoinNetworkService();

    applicationDataDirectory = InstallationManager.createApplicationDataDirectory();
  }


//  @Test
//  public void testSimple() throws Exception {
//    assertThat(bitcoinNetworkService).isNotNull();
//
//    WalletManager walletManager = new WalletManager();
//    walletManager.createWallet("password");
//
//    bitcoinNetworkService.start();
//    bitcoinNetworkService.downloadBlockChain();
//
//    Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
//
//    bitcoinNetworkService.stopAndWait();
//  }
}
