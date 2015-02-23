package org.multibit.hd.core.testing.payments;

import com.google.common.io.ByteStreams;
import org.multibit.hd.core.services.PaymentProtocolServiceTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;

/**
 * <p>Runnable to provide the following to Payment Protocol server:</p>
 * <ul>
 * <li>Serves from a fixture</li>
 * </ul>
 *
 * @since 0.0.7
 *  
 */
public class FixtureRunnable implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(FixtureRunnable.class);

  private final ServerSocket serverSocket;
  private String fixture;

  /**
   * @param serverSocket The server socket accept client connections over SSL
   * @param fixture      The classpath reference to the fixture to serve as a byte[]
   */
  public FixtureRunnable(ServerSocket serverSocket, String fixture) {

    this.serverSocket = serverSocket;
    this.fixture = fixture;

  }

  @Override
  public void run() {

    if (serverSocket.isClosed()) {
      log.warn("Server socket is closed. Aborting.");
    } else {

      try {

        // Wait for a client connection
        log.debug("Await client connection to SSLSocket");
        SSLSocket socket = (SSLSocket) serverSocket.accept();

        // Serve the payment request protobuf
        log.debug("Serving fixture: {}", fixture);
        InputStream inputStream = PaymentProtocolServiceTest.class.getResourceAsStream(fixture);
        ByteStreams.copy(inputStream, socket.getOutputStream());

        // Release resources
        log.debug("Flush then close client socket...");
        socket.getOutputStream().flush();
        socket.close();

      } catch (IOException e) {
        log.error("Unexpected IO exception", e);
      }

    }

  }
}
