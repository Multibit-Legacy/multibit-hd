package org.multibit.hd.core.services;

import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class BitcoinNetworkServiceTest {

  private BitcoinNetworkService bitcoinNetworkService;

  private String applicationDataDirectoryName = InstallationManager.createApplicationDataDirectory();

  @Before
  public void setUp() throws IOException {
    CoreServices.main(null);

    bitcoinNetworkService = CoreServices.newBitcoinNetworkService();

    applicationDataDirectoryName = InstallationManager.createApplicationDataDirectory();
  }


  @Test
  public void testSimple() throws Exception {
    assertThat(bitcoinNetworkService).isNotNull();

    WalletManager walletManager = new WalletManager();
    walletManager.createSimpleWallet("password");

    bitcoinNetworkService.start();
    bitcoinNetworkService.downloadBlockChain();

    Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);

    bitcoinNetworkService.stopAndWait();
  }
}
