package org.multibit.hd.core.managers;

import com.google.common.base.Optional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.services.CoreServices;

import java.io.File;
import java.security.KeyStore;

import static org.fest.assertions.Assertions.assertThat;

public class HttpsManagerTest {

  @SuppressFBWarnings({"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", "NP_NONNULL_PARAM_VIOLATION"})
  @Before
  public void setUp() throws Exception {
    InstallationManager.unrestricted = true;

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    // Start the core services
    CoreServices.main(null);

  }

  @After
  public void tearDown() throws Exception {
    // Order is important here
    CoreServices.shutdownNow(ShutdownEvent.ShutdownType.SOFT);

    InstallationManager.shutdownNow(ShutdownEvent.ShutdownType.SOFT);
    BackupManager.INSTANCE.shutdownNow();
    WalletManager.INSTANCE.shutdownNow(ShutdownEvent.ShutdownType.HARD);
  }


  @Test
  public void testGetOrCreateTrustStore() throws Exception {

    // Act
    Optional<File> cacertsFile = HttpsManager.INSTANCE.getOrCreateTrustStore(
      InstallationManager.getOrCreateApplicationDataDirectory(),
      InstallationManager.CA_CERTS_NAME,
      true
    );

    // Assert
    assertThat(cacertsFile.isPresent()).isTrue();

    // Verify key store can be loaded in standard manner and contains a suitable number of certificates
    final KeyStore ks = HttpsManager.INSTANCE.getKeyStore(cacertsFile.get());
    assertThat(ks.size()).isGreaterThan(70);

  }
}