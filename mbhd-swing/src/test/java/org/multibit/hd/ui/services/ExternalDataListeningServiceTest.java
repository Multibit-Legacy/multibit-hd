package org.multibit.hd.ui.services;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.uri.BitcoinURI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.InstallationManager;

import java.io.InputStreamReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.Assertions.assertThat;

public class ExternalDataListeningServiceTest {

  private static final String PAYMENT_REQUEST_BIP21_MINIMUM = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty";

  private static final String PAYMENT_REQUEST_BIP21 = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?" +
    "amount=0.01&" +
    "label=Please%20donate%20to%20multibit.org";

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
    Address address = testObject.getBitcoinURI().get().getAddress();

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
    Address address = testObject.getBitcoinURI().get().getAddress();

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
  public void testParse_BIP72_Single() throws Exception {

    // Act
    BitcoinURI bitcoinURI = new BitcoinURI(
      MainNetParams.get(),
      PAYMENT_REQUEST_BIP72_SINGLE
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
      PAYMENT_REQUEST_BIP72_MULTIPLE
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

    // Arrange
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

    // Arrange
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

    // Arrange
    serverSocket = new ServerSocket(
      ExternalDataListeningService.MULTIBIT_HD_NETWORK_SOCKET,
      10,
      InetAddress.getLoopbackAddress()
    );

    String[] args = new String[]{
      PAYMENT_REQUEST_BIP72_SINGLE
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
    String expectedMessage = ExternalDataListeningService.MESSAGE_START + PAYMENT_REQUEST_BIP72_SINGLE + ExternalDataListeningService.MESSAGE_END;

    // Assert
    assertThat(text).isEqualTo(expectedMessage);

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    assertThat(testObject.getServerSocket().isPresent()).isFalse();
  }

  @Test
  public void testNotify_BIP72_Multiple() throws Exception {

    // Arrange
    serverSocket = new ServerSocket(
      ExternalDataListeningService.MULTIBIT_HD_NETWORK_SOCKET,
      10,
      InetAddress.getLoopbackAddress()
    );

    String[] args = new String[]{
      PAYMENT_REQUEST_BIP72_MULTIPLE
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
    String expectedMessage = ExternalDataListeningService.MESSAGE_START + PAYMENT_REQUEST_BIP72_MULTIPLE + ExternalDataListeningService.MESSAGE_END;

    // Assert
    assertThat(text).isEqualTo(expectedMessage);

    // Don't crash the JVM
    CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);

    assertThat(testObject.getServerSocket().isPresent()).isFalse();
  }

}
