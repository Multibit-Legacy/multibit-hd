package org.multibit.hd.core.services;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentSessionStatus;
import org.multibit.hd.core.dto.PaymentSessionSummary;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import static org.fest.assertions.Assertions.assertThat;

public class PaymentProtocolServiceTest {

  private static final Logger log = LoggerFactory.getLogger(PaymentProtocolServiceTest.class);

  private static NetworkParameters networkParameters = TestNet3Params.get();

  private PaymentProtocolService testObject;

  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

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

  }

  @Test
  public void testProbeForPaymentSession_NoPKI_Error() throws Exception {

    // Act
    final URI uri = URI.create("/fixtures/payments/test-net-faucet.bitcoinpaymentrequest");
    final PaymentSessionSummary paymentSessionSummary = testObject.probeForPaymentSession(uri, true, null);

    // Assert
    assertThat(paymentSessionSummary.getStatus()).isEqualTo(PaymentSessionStatus.PKI_MISSING);

  }

  @Test
  public void testGetPaymentSession_NoPKI_Expected() throws Exception {

    // Act
    final URI uri = URI.create("/fixtures/payments/test-net-faucet.bitcoinpaymentrequest");
    final PaymentSessionSummary paymentSessionSummary = testObject.probeForPaymentSession(uri, false, null);

    // Assert
    assertThat(paymentSessionSummary.getStatus()).isEqualTo(PaymentSessionStatus.OK);

  }

  @Test
  public void testNotify_BIP72_SingePaymentRequestUrl() throws Exception {

//    // Start the payment protocol request server to resolve https://localhost:8443
//    final SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
//    final SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(8443);
//
//    String[] suites = serverSocket.getSupportedCipherSuites();
//    serverSocket.setEnabledCipherSuites(suites);
//
//    executorService.submit(
//      new Runnable() {
//        @Override
//        public void run() {
//          try {
//
//            // Wait for a client connection
//            SSLSocket socket = (SSLSocket) serverSocket.accept();
//
//            // Serve the payment request protobuf
//            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//            dos.write("Hi".getBytes());
//
//            dos.close();
//            socket.close();
//            serverSocket.close();
//          } catch (BindException e) {
//            fail("Address already in use - is another test already running?");
//          } catch (IOException e) {
//            fail("IOException:" + e.getMessage());
//          }
//
//        }
//      });

//    String[] args = new String[]{
//      PAYMENT_REQUEST_BIP72_SINGLE
//    };
//
//    testObject = new BitcoinURIListeningService(args);
//    testObject.start();
//
//    Socket client = serverSocket.accept();
//
//    String text;
//    try (InputStreamReader reader = new InputStreamReader(client.getInputStream(), Charsets.UTF_8)) {
//      text = CharStreams.toString(reader);
//    }
//    client.close();
//
//    String expectedMessage = BitcoinURIListeningService.MESSAGE_START + PAYMENT_REQUEST_BIP72_SINGLE + BitcoinURIListeningService.MESSAGE_END;
//    assertThat(text).isEqualTo(expectedMessage);
//
//    // Don't crash the JVM
//    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);
//
//    assertThat(testObject.getServerSocket().isPresent()).isFalse();

  }


}
