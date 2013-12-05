package org.multibit.hd.core.services;

import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.utils.MultiBitFiles;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class BitcoinNetworkServiceTest {

  private BitcoinNetworkService bitcoinNetworkService;

  private String applicationDataDirectoryName = MultiBitFiles.createApplicationDataDirectory();

  @Before
  public void setUp() throws IOException {
    CoreServices.main(null);

    bitcoinNetworkService = CoreServices.newBitcoinNetworkService();

    applicationDataDirectoryName = MultiBitFiles.createApplicationDataDirectory();
  }


  @Test
  public void testBasic() throws Exception {
    assertThat(bitcoinNetworkService).isNotNull();

    bitcoinNetworkService.initialise();

    String nameOfTestWallet = "test";


    // Create wallet directory (into which config files will be written)
    String testWalletDirectory = applicationDataDirectoryName + File.separator + nameOfTestWallet;
    (new File(testWalletDirectory)).mkdir();

    Configurations.currentConfiguration.getApplicationConfiguration().setCurrentWalletFilename(nameOfTestWallet);

    bitcoinNetworkService.start();
    bitcoinNetworkService.downloadBlockChain();

    Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);

    bitcoinNetworkService.stopAndWait();
  }
}
