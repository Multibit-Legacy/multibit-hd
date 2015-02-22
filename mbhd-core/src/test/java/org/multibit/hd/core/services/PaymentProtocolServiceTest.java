package org.multibit.hd.core.services;

import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.junit.*;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.PaymentSessionStatus;
import org.multibit.hd.core.dto.PaymentSessionSummary;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.SSLManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.hardware.core.concurrent.SafeExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URI;

import static org.fest.assertions.Assertions.assertThat;

public class PaymentProtocolServiceTest {

  private static final NetworkParameters networkParameters = MainNetParams.get();

  private static final ListeningExecutorService executorService = SafeExecutors.newSingleThreadExecutor("bip70-server");

  private static SSLServerSocket serverSocket;

  private PaymentProtocolService testObject;

  /**
   * Bitcoin URI containing BIP72 Payment Protocol URI extensions
   */
  private static final String PAYMENT_REQUEST_BIP72_MULTIPLE = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?" +
    "r=https://localhost:8443/abc123&" +
    "r1=https://localhost:8443/def456&" +
    "r2=https://localhost:8443/ghi789&" +
    "amount=1";

  private static final String PAYMENT_REQUEST_BIP72_SINGLE = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?" +
    "r=https://localhost:8443/abc123&" +
    "amount=1";

  @BeforeClass
  public static void beforeClass() throws IOException {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    // Configure the server side keystore for "localhost"
    String keyStoreFile = PaymentProtocolServiceTest.class.getResource("/localhost-ssl.keystore").getFile();
    System.setProperty("javax.net.ssl.keyStore", keyStoreFile);
    System.setProperty("javax.net.ssl.keyStorePassword", String.valueOf(SSLManager.PASSPHRASE));

    // Start the payment protocol request server to resolve https://localhost:8443
    final SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

    // Create a re-usable server socket
    serverSocket = (SSLServerSocket) factory.createServerSocket(8443);
    String[] suites = serverSocket.getSupportedCipherSuites();
    serverSocket.setEnabledCipherSuites(suites);
  }

  @AfterClass
  public static void afterClass() throws IOException {

    executorService.shutdownNow();
    serverSocket.close();

  }

  @Before
  public void setUp() throws Exception {

    // Start the SSL server to ensure we have its certificate
    executorService.submit(new PaymentProtocolServerRunnable(serverSocket, "/fixtures/payments/test-net-faucet.bitcoinpaymentrequest"));

    SSLManager.INSTANCE.installCACertificates(
      InstallationManager.getOrCreateApplicationDataDirectory(),
      InstallationManager.CA_CERTS_NAME,
      new String[]{"localhost:8443"},
      true
    );

    testObject = new PaymentProtocolService(networkParameters);
    testObject.start();

  }

  @After
  public void tearDown() {

    // Order is important here
    CoreServices.shutdownNow(ShutdownEvent.ShutdownType.SOFT);

    InstallationManager.shutdownNow(ShutdownEvent.ShutdownType.SOFT);
    BackupManager.INSTANCE.shutdownNow();
    WalletManager.INSTANCE.shutdownNow(ShutdownEvent.ShutdownType.HARD);

  }

  @Test
  public void testProbeForPaymentSession_ProtobufError() throws Exception {

    // Act
    final URI uri = URI.create("/fixtures/payments/test-net-faucet-broken.bitcoinpaymentrequest");
    final PaymentSessionSummary paymentSessionSummary = testObject.probeForPaymentSession(uri, true, null);

    // Assert
    assertThat(paymentSessionSummary.getStatus()).isEqualTo(PaymentSessionStatus.ERROR);
    assertThat(paymentSessionSummary.getPaymentSession().isPresent()).isFalse();
    assertThat(paymentSessionSummary.getMessageKey().get()).isEqualTo(CoreMessageKey.PAYMENT_SESSION_ERROR);

  }

  @Test
  public void testProbeForPaymentSession_NoPKI_PKIMissing() throws Exception {

    // Act
    final URI uri = URI.create("/fixtures/payments/test-net-faucet.bitcoinpaymentrequest");
    final PaymentSessionSummary paymentSessionSummary = testObject.probeForPaymentSession(uri, true, null);

    // Assert
    assertThat(paymentSessionSummary.getStatus()).isEqualTo(PaymentSessionStatus.PKI_INVALID);
    assertThat(paymentSessionSummary.getPaymentSession().isPresent()).isFalse();
    assertThat(paymentSessionSummary.getMessageKey().get()).isEqualTo(CoreMessageKey.PAYMENT_SESSION_PKI_INVALID);

  }

  @Test
  public void testProbeForPaymentSession_NoPKI_OK() throws Exception {

    // Act
    final URI uri = URI.create("/fixtures/payments/test-net-faucet.bitcoinpaymentrequest");
    final PaymentSessionSummary paymentSessionSummary = testObject.probeForPaymentSession(uri, false, null);

    // Assert
    assertThat(paymentSessionSummary.getStatus()).isEqualTo(PaymentSessionStatus.OK);
    assertThat(paymentSessionSummary.getPaymentSession().isPresent()).isTrue();
    assertThat(paymentSessionSummary.getMessageKey().get()).isEqualTo(CoreMessageKey.PAYMENT_SESSION_OK);

  }

  @Test
  public void testProbeForPaymentSession_LocalPKI_PKIMissing() throws Exception {

    // Arrange
    executorService.submit(new PaymentProtocolServerRunnable(serverSocket, "/fixtures/payments/test-net-faucet.bitcoinpaymentrequest"));

    final URI uri = URI.create(PAYMENT_REQUEST_BIP72_SINGLE);

    // Act
    final PaymentSessionSummary paymentSessionSummary = testObject.probeForPaymentSession(uri, true, null);

    // Assert
    assertThat(paymentSessionSummary.getStatus()).isEqualTo(PaymentSessionStatus.PKI_INVALID);
    assertThat(paymentSessionSummary.getPaymentSession().isPresent()).isFalse();
    assertThat(paymentSessionSummary.getMessageKey().get()).isEqualTo(CoreMessageKey.PAYMENT_SESSION_PKI_INVALID);

  }

  @Test
  public void testProbeForPaymentSession_LocalPKI_OK() throws Exception {

    // Arrange
    executorService.submit(new PaymentProtocolServerRunnable(serverSocket, "/fixtures/payments/test-net-faucet.bitcoinpaymentrequest"));

    final URI uri = URI.create(PAYMENT_REQUEST_BIP72_SINGLE);

    // Act
    final PaymentSessionSummary paymentSessionSummary = testObject.probeForPaymentSession(uri, false, null);

    // Assert
    assertThat(paymentSessionSummary.getStatus()).isEqualTo(PaymentSessionStatus.OK);
    assertThat(paymentSessionSummary.getPaymentSession().isPresent()).isTrue();
    assertThat(paymentSessionSummary.getMessageKey().get()).isEqualTo(CoreMessageKey.PAYMENT_SESSION_PKI_UNTRUSTED_CA);

  }

  ////////////////////////////////////////////

  /**
   * A local server listening on 8443 and responding with Payment Protocol requests created from fixtures
   */
  private static class PaymentProtocolServerRunnable implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PaymentProtocolServerRunnable.class);

    private final ServerSocket serverSocket;
    private final String fixture;

    /**
     * @param serverSocket The server socket
     * @param fixture      The classpath reference to the Payment Protocol request protobuf fixture
     */
    public PaymentProtocolServerRunnable(ServerSocket serverSocket, String fixture) {
      this.serverSocket = serverSocket;
      this.fixture = fixture;

    }

    @Override
    public void run() {

      boolean socketClosed = false;

      while (!socketClosed) {

        if (serverSocket.isClosed()) {
          socketClosed = true;
        } else {

          try {

            // Wait for a client connection
            log.debug("Await client connection to SSLSocket");
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            socket.startHandshake();

            // Serve the payment request protobuf
            InputStream inputStream = PaymentProtocolServiceTest.class.getResourceAsStream(fixture);
            ByteStreams.copy(inputStream, socket.getOutputStream());

            // Release resources
            log.debug("Closing SSLSocket...");
            socket.close();

          } catch (IOException e) {
            socketClosed = true;
          }

        }
      } // End of while

    }
  }

}
