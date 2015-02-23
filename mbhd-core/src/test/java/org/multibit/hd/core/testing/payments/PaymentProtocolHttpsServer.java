package org.multibit.hd.core.testing.payments;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.SSLManager;
import org.multibit.hd.core.services.PaymentProtocolServiceTest;
import org.multibit.hd.hardware.core.concurrent.SafeExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * A HTTPS server listening on localhost:8443 and responding with Payment Protocol
 * requests created from fixtures
 */
public class PaymentProtocolHttpsServer {

  private static final Logger log = LoggerFactory.getLogger(PaymentProtocolHttpsServer.class);

  private final SSLServerSocket serverSocket;
  private final Queue<String> fixtures = Queues.newArrayBlockingQueue(10);

  private static final ListeningExecutorService executorService = SafeExecutors.newSingleThreadExecutor("bip70-server");

  public PaymentProtocolHttpsServer() throws IOException {

    log.debug("Registering the key store");

    // Configure the server side keystore for "localhost"
    String keyStoreFile = PaymentProtocolServiceTest.class.getResource("/localhost.jks").getFile();
    System.setProperty("javax.net.ssl.keyStore", keyStoreFile);
    System.setProperty("javax.net.ssl.keyStorePassword", String.valueOf(SSLManager.PASSPHRASE));

    // Start the payment protocol request server to resolve https://localhost:8443
    final SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

    // Create a re-usable server socket
    serverSocket = (SSLServerSocket) factory.createServerSocket(8443);
    String[] suites = serverSocket.getSupportedCipherSuites();
    serverSocket.setEnabledCipherSuites(suites);

    // Provide an initial fixture to ensure SSL certificates are placed in the trust store
    addFixture("/fixtures/payments/test-net-faucet.bitcoinpaymentrequest");

    // Start the SSL server to ensure we have its certificate
    executorService.submit(new FixtureRunnable(serverSocket, fixtures.poll()));

    log.debug("Attempting to install the CA cert in the trust store");

    SSLManager.INSTANCE.installCACertificates(
      InstallationManager.getOrCreateApplicationDataDirectory(),
      InstallationManager.CA_CERTS_NAME,
      new String[]{"localhost:8443"},
      true
    );

  }

  /**
   * @param fixture The classpath reference of the fixture to add (served as byte[])
   */
  public void addFixture(String fixture) {

    log.debug("Adding fixture:{}", fixture);
    fixtures.add(fixture);

  }

  /**
   * Remove all entries from the fixture queue and reset the executor service
   */
  public void reset() {

    fixtures.clear();

    executorService.shutdownNow();
    try {
      executorService.awaitTermination(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.warn("Failed to terminate executor service cleanly");
    }

  }

  public void stop() {

    reset();
    try {
      serverSocket.close();
    } catch (IOException e) {
      log.warn("Failed to close server socket", e);
    }

  }
}
