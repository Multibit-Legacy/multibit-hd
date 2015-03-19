package org.multibit.hd.ui.services;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.uri.BitcoinURI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.dto.PaymentSessionStatus;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.InstallationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.Assertions.assertThat;

public class ExternalDataListeningServiceTest {

  private static final Logger log = LoggerFactory.getLogger(ExternalDataListeningServiceTest.class);

  private static final String PAYMENT_REQUEST_BIP21_MINIMUM = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty";

  private static final String PAYMENT_REQUEST_BIP21 = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?" +
    "amount=0.01&" +
    "label=Please%20donate%20to%20multibit.org";

  /**
   * Bitcoin URI containing BIP72 Payment Protocol URI extensions
   */
  private static final String PAYMENT_REQUEST_BIP72_URL_MULTIPLE = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?" +
    "r=https://localhost:8443/abc123&" +
    "r1=https://localhost:8443/def456&" +
    "r2=https://localhost:8443/ghi789&" +
    "amount=1";

  private static final String PAYMENT_REQUEST_BIP72_URL_SINGLE = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?" +
    "r=https://localhost:8443/abc123&" +
    "amount=1";

  /**
   * Windows format relative file path (based on mbhd-swing for Maven - IDE's will need adjustment)
   */
  private static final String PAYMENT_REQUEST_BIP72_FILE_WINDOWS_SINGLE = "src\\test\\resources\\fixtures\\payments\\localhost-signed.bitcoinpaymentrequest";

  /**
   * Java format relative file path (based on mbhd-swing for Maven - IDE's will need adjustment)
   */
  private static final String PAYMENT_REQUEST_BIP72_FILE_JAVA_SINGLE = "src/test/resources/fixtures/payments/localhost-signed.bitcoinpaymentrequest";

  private ServerSocket serverSocket = null;

  private ExternalDataListeningService testObject;

  @Before
  public void setUp() throws Exception {

    // Ensure the shutdown event doesn't overwrite existing configuration
    InstallationManager.unrestricted = true;

  }

  @After
  public void tearDown() throws Exception {

    if (serverSocket != null) {

      serverSocket.close();

    }

  }

  @Test
  public void testParse_BIP21() throws Exception {

    // Arrange
    String[] args = new String[]{
      PAYMENT_REQUEST_BIP21
    };

    testObject = new ExternalDataListeningService(args);

    // Act
    Address address = testObject.getBitcoinURIQueue().poll().getAddress();

    // Assert
    if (address == null) {
      fail();
    }
    assertThat(address.toString()).isEqualTo("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    assertThat(testObject.getServerSocket().isPresent()).isFalse();

  }

  @Test
  public void testParse_BIP21_Minimum() throws Exception {

    // Arrange
    String[] args = new String[]{
      PAYMENT_REQUEST_BIP21_MINIMUM
    };

    testObject = new ExternalDataListeningService(args);

    // Act
    Address address = testObject.getBitcoinURIQueue().poll().getAddress();

    // Assert
    if (address == null) {
      fail();
    }
    assertThat(address.toString()).isEqualTo("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    assertThat(testObject.getServerSocket().isPresent()).isFalse();

  }

  @Test
  public void testParse_BIP72_File_Windows_Single() throws Exception {

    // Arrange
    // Check for Maven or IDE execution environment
    File single = Paths.get(PAYMENT_REQUEST_BIP72_FILE_JAVA_SINGLE).toFile();
    final String[] args;
    if (single.exists()) {
      log.info("Resolved test fixture as: '{}'. Verified Maven build.", single.getAbsolutePath());
      args = new String[]{
        // Provide the Windows fixture to test handling
        PAYMENT_REQUEST_BIP72_FILE_WINDOWS_SINGLE
      };
    } else {
      log.info("Resolved Windows fixture as: '{}' but does not exist. Assuming an IDE build.", single.getAbsolutePath());
      single = Paths.get("mbhd-swing/" + PAYMENT_REQUEST_BIP72_FILE_JAVA_SINGLE).toFile();
      if (single.exists()) {
        log.info("Resolved Windows fixture as: '{}'. Verified IDE build.", single.getAbsolutePath());
        args = new String[]{
          // Provide adjusted Windows fixture to test handling
          "mbhd-swing\\" + PAYMENT_REQUEST_BIP72_FILE_WINDOWS_SINGLE
        };
      } else {
        fail();
        return;
      }
    }

    // Act
    testObject = new ExternalDataListeningService(args);

    // Assert
    assertThat(testObject.getPaymentSessionSummaryQueue().isEmpty()).isFalse();
    PaymentSessionStatus status = testObject.getPaymentSessionSummaryQueue().poll().getStatus();
    if (status == null) {
      fail();
    }
    assertThat(status).isEqualTo(PaymentSessionStatus.UNTRUSTED);

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    assertThat(testObject.getServerSocket().isPresent()).isFalse();

  }

  @Test
  public void testParse_BIP72_Single() throws Exception {

    // Act
    BitcoinURI bitcoinURI = new BitcoinURI(
      MainNetParams.get(),
      PAYMENT_REQUEST_BIP72_URL_SINGLE
    );

    // Assert
    final List<String> paymentRequestUrls = bitcoinURI.getPaymentRequestUrls();
    assertThat(paymentRequestUrls.size()).isEqualTo(1);

    // The primary payment request URL is in its own field
    assertThat(bitcoinURI.getPaymentRequestUrl()).isEqualTo("https://localhost:8443/abc123");
    assertThat(paymentRequestUrls.get(0)).isEqualTo("https://localhost:8443/abc123");

  }

  @Test
  public void testParse_BIP72_Multiple() throws Exception {

    // Act
    BitcoinURI bitcoinURI = new BitcoinURI(
      MainNetParams.get(),
      PAYMENT_REQUEST_BIP72_URL_MULTIPLE
    );

    // Assert
    final List<String> paymentRequestUrls = bitcoinURI.getPaymentRequestUrls();
    assertThat(paymentRequestUrls.size()).isEqualTo(3);

    // The primary payment request URL is in its own field
    assertThat(bitcoinURI.getPaymentRequestUrl()).isEqualTo("https://localhost:8443/abc123");

    // Backup payment request URLs are in reverse order
    assertThat(paymentRequestUrls.get(0)).isEqualTo("https://localhost:8443/ghi789");
    assertThat(paymentRequestUrls.get(1)).isEqualTo("https://localhost:8443/def456");
    assertThat(paymentRequestUrls.get(2)).isEqualTo("https://localhost:8443/abc123");

  }

  @Test
  public void testNotify_BIP21() throws Exception {

    // Arrange to grab the server socket first
    serverSocket = new ServerSocket(
      ExternalDataListeningService.MULTIBIT_HD_NETWORK_SOCKET,
      10,
      InetAddress.getLoopbackAddress()
    );

    String[] args = new String[]{
      PAYMENT_REQUEST_BIP21
    };

    testObject = new ExternalDataListeningService(args);
    testObject.start();

    Socket client = serverSocket.accept();

    String text;
    try (InputStreamReader reader = new InputStreamReader(client.getInputStream(), Charsets.UTF_8)) {
      text = CharStreams.toString(reader);
    }
    client.close();

    // Act
    String expectedMessage = ExternalDataListeningService.MESSAGE_START + PAYMENT_REQUEST_BIP21 + ExternalDataListeningService.MESSAGE_END;

    // Assert
    assertThat(text).isEqualTo(expectedMessage);

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    assertThat(testObject.getServerSocket().isPresent()).isFalse();
  }

  @Test
  public void testNotify_BIP21_Minimum() throws Exception {

    // Arrange to grab the server socket first
    try {
      serverSocket = new ServerSocket(
        ExternalDataListeningService.MULTIBIT_HD_NETWORK_SOCKET,
        10,
        InetAddress.getLoopbackAddress()
      );
    } catch (BindException e) {
      fail("Address already in use - is another version of MultiBit HD already running?");
    }

    String[] args = new String[]{
      PAYMENT_REQUEST_BIP21_MINIMUM
    };

    testObject = new ExternalDataListeningService(args);
    testObject.start();

    Socket client = serverSocket.accept();

    String text;
    try (InputStreamReader reader = new InputStreamReader(client.getInputStream(), Charsets.UTF_8)) {
      text = CharStreams.toString(reader);
    }
    client.close();

    // Act
    String expectedMessage = ExternalDataListeningService.MESSAGE_START + PAYMENT_REQUEST_BIP21_MINIMUM + ExternalDataListeningService.MESSAGE_END;

    // Assert
    assertThat(text).isEqualTo(expectedMessage);

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    assertThat(testObject.getServerSocket().isPresent()).isFalse();

  }

  @Test
  public void testNotify_BIP72_Single() throws Exception {

    // Arrange to grab the server socket first
    serverSocket = new ServerSocket(
      ExternalDataListeningService.MULTIBIT_HD_NETWORK_SOCKET,
      10,
      InetAddress.getLoopbackAddress()
    );

    String[] args = new String[]{
      PAYMENT_REQUEST_BIP72_URL_SINGLE
    };

    testObject = new ExternalDataListeningService(args);
    testObject.start();

    Socket client = serverSocket.accept();

    String text;
    try (InputStreamReader reader = new InputStreamReader(client.getInputStream(), Charsets.UTF_8)) {
      text = CharStreams.toString(reader);
    }
    client.close();

    // Act
    String expectedMessage = ExternalDataListeningService.MESSAGE_START + PAYMENT_REQUEST_BIP72_URL_SINGLE + ExternalDataListeningService.MESSAGE_END;

    // Assert
    assertThat(text).isEqualTo(expectedMessage);

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    assertThat(testObject.getServerSocket().isPresent()).isFalse();
  }

  @Test
  public void testNotify_BIP72_Multiple() throws Exception {

    // Arrange to grab the server socket first
    serverSocket = new ServerSocket(
      ExternalDataListeningService.MULTIBIT_HD_NETWORK_SOCKET,
      10,
      InetAddress.getLoopbackAddress()
    );

    String[] args = new String[]{
      PAYMENT_REQUEST_BIP72_URL_MULTIPLE
    };

    testObject = new ExternalDataListeningService(args);
    testObject.start();

    Socket client = serverSocket.accept();

    String text;
    try (InputStreamReader reader = new InputStreamReader(client.getInputStream(), Charsets.UTF_8)) {
      text = CharStreams.toString(reader);
    }
    client.close();

    // Act
    String expectedMessage = ExternalDataListeningService.MESSAGE_START + PAYMENT_REQUEST_BIP72_URL_MULTIPLE + ExternalDataListeningService.MESSAGE_END;

    // Assert
    assertThat(text).isEqualTo(expectedMessage);

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    assertThat(testObject.getServerSocket().isPresent()).isFalse();
  }

}
