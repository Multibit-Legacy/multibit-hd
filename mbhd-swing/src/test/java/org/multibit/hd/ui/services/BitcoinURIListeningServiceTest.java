package org.multibit.hd.ui.services;

import com.google.bitcoin.core.Address;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.After;
import org.junit.Test;
import org.multibit.hd.core.events.CoreEvents;

import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;

public class BitcoinURIListeningServiceTest {

  private static final String RAW_URI_FULL = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?amount=0.01&label=Please%20donate%20to%20multibit.org";
  private static final String RAW_URI_ADDRESS = "bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty";

  private ServerSocket serverSocket = null;

  private BitcoinURIListeningService testObject;

  @After
  public void tearDown() throws Exception {

    if (serverSocket != null) {

      serverSocket.close();

    }

  }

  @Test
  public void testParseRawURI_URLEncoded() throws Exception {

    String[] args = new String[]{
      RAW_URI_FULL
    };

    testObject = new BitcoinURIListeningService(args);

    Address address = testObject.getBitcoinURI().get().getAddress();
    if (address == null) {
      fail();
    }
    assertThat(address.toString()).isEqualTo("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    CoreEvents.fireShutdownEvent();

    assertThat(testObject.getServerSocket().isPresent()).isFalse();

  }

  @Test
  public void testParseRawURI_AddressOnly() throws Exception {

    String[] args = new String[]{
      RAW_URI_ADDRESS
    };

    testObject = new BitcoinURIListeningService(args);

    Address address = testObject.getBitcoinURI().get().getAddress();
    if (address == null) {
      fail();
    }
    assertThat(address.toString()).isEqualTo("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty");

    CoreEvents.fireShutdownEvent();

    assertThat(testObject.getServerSocket().isPresent()).isFalse();

  }

  @Test
  public void testNotify_Full() throws Exception {

    serverSocket = new ServerSocket(
      BitcoinURIListeningService.MULTIBIT_HD_NETWORK_SOCKET,
      10,
      InetAddress.getLoopbackAddress()
    );

    String[] args = new String[]{
      RAW_URI_FULL
    };

    testObject = new BitcoinURIListeningService(args);
    testObject.start();

    Socket client = serverSocket.accept();

    String text;
    try (InputStreamReader reader = new InputStreamReader(client.getInputStream(), Charsets.UTF_8)) {
      text = CharStreams.toString(reader);
    }
    client.close();

    String expectedMessage = BitcoinURIListeningService.MESSAGE_START +"bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?amount=0.01&label=Please%20donate%20to%20multibit.org"+BitcoinURIListeningService.MESSAGE_END;
    assertThat(text).isEqualTo(expectedMessage);

    CoreEvents.fireShutdownEvent();

    assertThat(testObject.getServerSocket().isPresent()).isFalse();
  }

  @Test
  public void testNotify_AddressOnly() throws Exception {

    serverSocket = new ServerSocket(
      BitcoinURIListeningService.MULTIBIT_HD_NETWORK_SOCKET,
      10,
      InetAddress.getLoopbackAddress()
    );

    String[] args = new String[]{
      RAW_URI_ADDRESS
    };

    testObject = new BitcoinURIListeningService(args);
    testObject.start();

    Socket client = serverSocket.accept();

    String text;
    try (InputStreamReader reader = new InputStreamReader(client.getInputStream(), Charsets.UTF_8)) {
      text = CharStreams.toString(reader);
    }
    client.close();

    String expectedMessage = BitcoinURIListeningService.MESSAGE_START +"bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty"+BitcoinURIListeningService.MESSAGE_END;
    assertThat(text).isEqualTo(expectedMessage);

    CoreEvents.fireShutdownEvent();

    assertThat(testObject.getServerSocket().isPresent()).isFalse();

  }

}
