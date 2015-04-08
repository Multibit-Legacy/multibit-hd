package org.multibit.hd.core.testing.payments;

import com.google.common.base.Charsets;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.multibit.hd.hardware.core.utils.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.Callable;

/**
 * <p>Runnable to provide the following to Payment Protocol server:</p>
 * <ul>
 * <li>Responds to a Payment with a PaymentACK</li>
 * </ul>
 *
 * @since 0.0.7
 *  
 */
public class PaymentACKCallable implements Callable<Boolean> {

  private static final Logger log = LoggerFactory.getLogger(PaymentACKCallable.class);

  private final ServerSocket serverSocket;
  private final String contentType;
  private String fixture;

  /**
   * @param serverSocket The server socket accept client connections over SSL
   * @param contentType  The HTTP Content-Type header value
   */
  public PaymentACKCallable(ServerSocket serverSocket, String contentType) {

    this.serverSocket = serverSocket;
    this.contentType = contentType;
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

        log.debug("Sending PaymentACK");
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        // Read the inputStream - this is expected to be a header followed by a serialised Payment
        Reader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        int contentLength = -1;
        StringBuilder builder = new StringBuilder();
        while(!"".equals((line = bufferedReader.readLine()))) {
          builder.append(line).append("\n");
          log.debug("Read line: {}", line);
          if (line.startsWith("Content-Length")) {
            String[] tokens = line.replaceAll(" ", "").split(":");
            if (tokens.length >= 2) {
              contentLength = Integer.parseInt(tokens[1]);
            }
          }
        }
        log.debug("Calculated contentLength: {}", contentLength);
        log.debug("Read the header:\n{}\n", builder.toString());

        // Get the Content-Length and read those - this is expected to be the serialised Payment
        if (contentLength > -1) {
          byte buffer[] = new byte[contentLength];
          for (int i = 0; i < contentLength; i++) {
            buffer[i] = (byte)inputStream.read();
          }

          log.debug("Read:\n", HexUtils.toHexBytes(buffer));

          try {
            Protos.Payment payment = Protos.Payment.parseFrom(buffer);

            log.debug("Successfully parsed a payment {}", payment);

            // Create a PaymentACK for the payment
            Protos.PaymentACK paymentAck = PaymentProtocol.createPaymentAck(payment, "You sent:'" + payment.getMemo() + "'");

            log.debug("Sending paymentACK as a response: {}", paymentAck);
            // Write the HTTP header
            outputStream.write("HTTP/1.0 200 OK\n".getBytes(Charsets.UTF_8));
            outputStream.write("Content-Type: ".getBytes(Charsets.UTF_8));
            outputStream.write(contentType.getBytes(Charsets.UTF_8));
            outputStream.write("\n\n".getBytes(Charsets.UTF_8));

            // Write the protobuf response
            paymentAck.writeTo(outputStream);

            //ByteArrayInputStream responseInputStream = new ByteArrayInputStream(paymentAckBytes);
            //ByteStreams.copy(responseInputStream, outputStream);

          } catch (com.google.protobuf.InvalidProtocolBufferException ipbe) {
            log.error("Was expecting a Payment on the socket but saw something else");
            ipbe.printStackTrace();
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            // Release resources
            log.debug("Flush then close client socket...");
            socket.getOutputStream().flush();
            socket.close();
          }
        } else {
          log.debug("Could not find a Content-Length in the header so not reading payment");
        }
        return true;

      } catch (IOException e) {
        throw new IllegalStateException("Unexpected IOException", e);
      }
    }
  }
}
