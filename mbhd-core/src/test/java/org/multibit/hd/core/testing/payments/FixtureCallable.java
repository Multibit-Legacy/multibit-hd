package org.multibit.hd.core.testing.payments;

import com.google.common.io.ByteStreams;
import org.multibit.hd.core.services.PaymentProtocolServiceTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
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
  private String fixture;

  /**
   * @param serverSocket The server socket accept client connections over SSL
   * @param fixture      The classpath reference to the fixture to serve as a byte[]
   */
  public FixtureCallable(ServerSocket serverSocket, String fixture) {

    this.serverSocket = serverSocket;
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

        // Serve the payment request protobuf
        log.debug("Serving fixture: {}", fixture);
        InputStream inputStream = PaymentProtocolServiceTest.class.getResourceAsStream(fixture);
//        log.debug("Initialise the key store containing the private server keys");
//        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
//        BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
//        String m = r.readLine();
//        w.write("HTTP/1.0 200 OK");
//        w.newLine();
//        w.write("Content-Type: bitcoin/html");
//        w.newLine();
//        w.newLine();
//        w.write("<html><body>Hello world!</body></html>");
//        w.newLine();
//        w.flush();
//        w.close();
//        r.close();
//        c.close();
        ByteStreams.copy(inputStream, socket.getOutputStream());

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
