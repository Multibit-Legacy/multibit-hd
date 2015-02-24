package org.multibit.hd.core.services;

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
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.testing.payments.PaymentProtocolHttpsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

import static org.fest.assertions.Assertions.assertThat;

@Ignore
public class PaymentProtocolServiceTest {

  private static final Logger log = LoggerFactory.getLogger(PaymentProtocolServiceTest.class);

  private static final NetworkParameters networkParameters = MainNetParams.get();

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

  private static PaymentProtocolHttpsServer server;

  static {

    InstallationManager.unrestricted = true;
    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    try {
      server = new PaymentProtocolHttpsServer();
    } catch (IOException e) {
      log.error("Unable to start the Payment Protocol HTTPS server");
    }

  }

  @BeforeClass
  public static void beforeClass() throws IOException {

    assertThat(server).isNotNull();

  }

  @AfterClass
  public static void afterClass() throws IOException {

    assertThat(server).isNotNull();
    server.stop();

  }

  @Before
  public void setUp() throws Exception {

    server.reset();

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

    server.reset();

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
    server.addFixture("/fixtures/payments/test-net-faucet.bitcoinpaymentrequest");

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
    server.addFixture("/fixtures/payments/test-net-faucet.bitcoinpaymentrequest");

    final URI uri = URI.create(PAYMENT_REQUEST_BIP72_SINGLE);

    // Act
    final PaymentSessionSummary paymentSessionSummary = testObject.probeForPaymentSession(uri, false, null);

    // Assert
    assertThat(paymentSessionSummary.getStatus()).isEqualTo(PaymentSessionStatus.OK);
    assertThat(paymentSessionSummary.getPaymentSession().isPresent()).isTrue();
    assertThat(paymentSessionSummary.getMessageKey().get()).isEqualTo(CoreMessageKey.PAYMENT_SESSION_PKI_UNTRUSTED_CA);

  }

}
