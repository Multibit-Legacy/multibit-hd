package org.multibit.hd.core.testing.payments;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import org.multibit.hd.core.services.PaymentProtocolServiceTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.Callable;

/**
 * <p>Runnable to provide the following to Payment Protocol server:</p>
 * <ul>
 * <li>Serves from a fixture</li>
 * </ul>
 *
 * @since 0.0.7
 *  
 */
public class FixtureCallable implements Callable<Boolean> {

  private static final Logger log = LoggerFactory.getLogger(FixtureCallable.class);

  private final ServerSocket serverSocket;
  private final String contentType;
  private String fixture;

  /**
   * @param serverSocket The server socket accept client connections over SSL
   * @param contentType  The HTTP Content-Type header value
   * @param fixture      The classpath reference to the fixture to serve as a byte[]
   */
  public FixtureCallable(ServerSocket serverSocket, String contentType, String fixture) {

    this.serverSocket = serverSocket;
    this.contentType = contentType;
    this.fixture = fixture;

  }

  @Override
  public Boolean call() {

    if (serverSocket.isClosed()) {
      log.warn("Server socket is closed. Aborting.");
      return false;
    } else {

      try {

        // Wait for a client connection
        log.debug("Await client connection to SSLSocket");
        SSLSocket socket = (SSLSocket) serverSocket.accept();
        socket.startHandshake();

        log.debug("Serving fixture: {}", fixture);
        InputStream inputStream = PaymentProtocolServiceTest.class.getResourceAsStream(fixture);
        OutputStream outputStream = socket.getOutputStream();

        // Write the HTTP header
        outputStream.write("HTTP/1.0 200 OK\n".getBytes(Charsets.UTF_8));
        outputStream.write("Content-Type: ".getBytes(Charsets.UTF_8));
        outputStream.write(contentType.getBytes(Charsets.UTF_8));
        outputStream.write("\n\n".getBytes(Charsets.UTF_8));

        // Write HTTP entity
        ByteStreams.copy(inputStream, outputStream);

        // Release resources
        log.debug("Flush then close client socket...");
        socket.getOutputStream().flush();
        socket.close();

        return true;

      } catch (IOException e) {
        throw new IllegalStateException("Unexpected IOException", e);
      }
    }
  }
}
