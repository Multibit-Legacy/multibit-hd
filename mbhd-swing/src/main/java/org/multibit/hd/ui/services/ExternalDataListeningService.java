package org.multibit.hd.ui.services;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import org.multibit.hd.core.dto.PaymentSessionSummary;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.services.AbstractService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.PaymentProtocolService;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.models.Models;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * <p>Service to maintain a localhost server socket on port 8330.</p>
 *
 * <p>If this socket is taken, then another instance of MultiBit HD is likely to be already
 * running and so this service will hand over any data it was given on startup to whatever
 * is listening on that port.</p>
 */
public class ExternalDataListeningService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(ExternalDataListeningService.class);

  /**
   * MultiBit HD port number as specified in <a href="http://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers">the list of TCP and UDP port numbers</a>.
   */
  public static final int MULTIBIT_HD_NETWORK_SOCKET = 8330;

  /**
   * Message start token (to ensure this isn't an accidental bunch of characters being sent)
   */
  public static final String MESSAGE_START = "$$MBHD-Start$$";

  /**
   * Message end token
   */
  public static final String MESSAGE_END = "$$MBHD-End$$";

  private final Optional<BitcoinURI> bitcoinURI;
  private final Optional<PaymentSessionSummary> paymentSessionSummary;

  private final Optional<String> rawData;

  private Optional<ServerSocket> serverSocket = Optional.absent();

  /**
   * @param args The command line arguments
   */
  public ExternalDataListeningService(String[] args) {

    super();

    if (args == null || args.length == 0) {
      this.bitcoinURI = Optional.absent();
      this.paymentSessionSummary = Optional.absent();
      rawData = Optional.absent();
      return;
    }

    // Must have some command line arguments to be here

    // Quick check for BIP21 Bitcoin URI
    if (args[0].startsWith("bitcoin") && !args[0].contains("r=")) {
      // Treat as BIP21 Bitcoin URI
      this.bitcoinURI = parseBitcoinURI(args[0]);
      this.paymentSessionSummary = Optional.absent();
    } else {
      // Treat as a Payment Protocol URI (could be direct via "http", "https", "file", "classpath" or use BIP72 variant of "bitcoin")
      this.bitcoinURI = Optional.absent();
      this.paymentSessionSummary = parsePaymentSessionSummary(args[0]);
    }

    rawData = Optional.of(args[0]);

  }

  @Override
  public boolean startInternal() {

    // This service will run a single background thread
    requireFixedThreadPoolExecutor(1, "uri-listener");

    try {

      // Attempt to own the localhost server socket allowing for a backlog of connections
      serverSocket = Optional.of(
        new ServerSocket(
          MULTIBIT_HD_NETWORK_SOCKET,
          10,
          InetAddress.getLoopbackAddress()
        ));

      // Successfully owned the server port so handle ongoing messages as master
      ListenableFuture future = getExecutorService().submit(getInstanceServerRunnable(serverSocket.get()));
      Futures.addCallback(
        future, new FutureCallback() {
          @Override
          public void onSuccess(Object result) {
            log.debug("Stopping BitcoinURIListeningService executor (success)");
            getExecutorService().shutdownNow();
          }

          @Override
          public void onFailure(Throwable t) {
            log.debug("Stopping BitcoinURIListeningService executor (failure)", t);
            getExecutorService().shutdownNow();
          }
        });

      log.info("Listening for MultiBit HD instances on socket: '{}'", MULTIBIT_HD_NETWORK_SOCKET);

      // Must be OK to be here
      return true;

    } catch (UnknownHostException e) {

      // Indicates that there is no loop back address on this machine
      log.error(e.getMessage(), e);

      // Indicate that a shutdown should be performed
      return false;

    } catch (IOException e) {

      // Failed to own the server port so notify the other instance
      log.info("Port is already taken. Notifying first instance.");

      if (bitcoinURI.isPresent()) {
        notifyOtherInstance();
      }
      if (paymentSessionSummary.isPresent()) {
        notifyOtherInstance();
      }

      // Indicate that a shutdown should be performed
      return false;
    }
  }

  @Override
  protected boolean shutdownNowInternal(ShutdownEvent.ShutdownType shutdownType) {

    // Service can survive a switch
    return preventCleanupOnSwitch(shutdownType);

  }

  /**
   * <p>A BIP21 Bitcoin URI parsed from the command line arguments</p>
   *
   * @return The Bitcoin URI if present
   */
  public Optional<BitcoinURI> getBitcoinURI() {
    return bitcoinURI;
  }

  /**
   * <p>A Payment Protocol session summary parsed from the command line arguments</p>
   *
   * @return The payment session summary if present
   */
  public Optional<PaymentSessionSummary> getPaymentSessionSummary() {
    return paymentSessionSummary;
  }

  /**
   * <p>The most recent raw data received</p>
   *
   * @return The raw data
   */
  Optional<String> getRawData() {
    return rawData;
  }

  /**
   * <p>The server socket created as the master if no other was in place</p>
   *
   * @return The server socket
   */
  Optional<ServerSocket> getServerSocket() {
    return serverSocket;
  }

  /**
   * <p>Handles the process of notifying another instance of the Bitcoin URI</p>
   */
  private void notifyOtherInstance() {

    if (!bitcoinURI.isPresent() && !paymentSessionSummary.isPresent()) {
      // Nothing to do (the data was meaningless)
      return;
    }

    try {
      Socket clientSocket = new Socket(
        InetAddress.getLoopbackAddress(),
        MULTIBIT_HD_NETWORK_SOCKET
      );

      try (OutputStream out = clientSocket.getOutputStream()) {

        // Write out the raw external data for parsing by the other instance
        out.write(MESSAGE_START.getBytes(Charsets.UTF_8));
        out.write(rawData.get().getBytes(Charsets.UTF_8));
        out.write(MESSAGE_END.getBytes(Charsets.UTF_8));

        out.close();
      }
      clientSocket.close();

    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

    log.info("Successfully notified first instance.");

  }

  /**
   * @param serverSocket The server socket on which messages will arrive
   *
   * @return The Runnable handling incoming messages
   */
  private Runnable getInstanceServerRunnable(final ServerSocket serverSocket) {

    return new MessageServerRunnable(serverSocket);
  }

  /**
   * <p>Attempt to detect if the raw Bitcoin URI is valid.</p>
   * <p>There are many reasons why a raw Bitcoin URI may be invalid:</p>
   * <ul>
   * <li>IE6-8 strips URL encoding when passing in URIs to a protocol handler</li>
   * <li>a user could hand-craft a URI and pass it in with non-ASCII character encoding present in the label</li>
   * </ul>
   *
   * <p>This is a really limited approach (no consideration of "amount=10.0&label=Black & White")
   * but should be OK for <a href="https://github.com/bitcoin/bips/blob/master/bip-0021.mediawiki">BIP21</a> use cases.</p>
   *
   * @param rawData The raw data straight from an assumed untrusted external source
   */
  private Optional<BitcoinURI> parseBitcoinURI(String rawData) {

    log.debug("Decoding BIP21 Bitcoin URI from '{}'", rawData);

    if (Strings.isNullOrEmpty(rawData)) {
      return Optional.absent();
    }

    try {

      // Basic initial checking for URL encoding
      int queryParamIndex = rawData.indexOf('?');
      if (queryParamIndex > 0 && !rawData.contains("%")) {
        // Possibly encoded but more likely not
        String encodedQueryParams = URLEncoder.encode(rawData.substring(queryParamIndex + 1), "UTF-8");
        rawData = rawData.substring(0, queryParamIndex) + "?" + encodedQueryParams;
        rawData = rawData.replaceAll("%3D", "=");
        rawData = rawData.replaceAll("%26", "&");
      }

      log.debug("Using '{}' to create BIP21 Bitcoin URI", rawData);
      return Optional.of(new BitcoinURI(rawData));

    } catch (UnsupportedEncodingException e) {
      log.error("UTF-8 is not supported on this platform");
    } catch (BitcoinURIParseException e) {
      log.error("BIP21 Bitcoin URI not valid. Error ", e);
    }

    return Optional.absent();

  }

  /**
   * <p>Attempt to detect if the raw data is a valid URI for Payment Protocol.</p>
   * <p>There are many reasons why a raw URI may be invalid:</p>
   * <ul>
   * <li>IE6-8 strips URL encoding when passing in URIs to a protocol handler</li>
   * <li>a user could hand-craft a URI and pass it in with non-ASCII character encoding present in the label</li>
   * </ul>
   *
   * <p>This is a substantial improvement over BIP21 URI handling
   * and should be OK for <a href="https://github.com/bitcoin/bips/blob/master/bip-0072.mediawiki">BIP72</a> use cases.</p>
   *
   * @param rawData The raw data straight from an assumed untrusted external source
   */
  private Optional<PaymentSessionSummary> parsePaymentSessionSummary(String rawData) {

    log.debug("Decoding URI from '{}'", rawData);

    if (Strings.isNullOrEmpty(rawData)) {
      return Optional.absent();
    }

    try {

      // Basic initial checking for URL encoding
      int queryParamIndex = rawData.indexOf('?');
      if (queryParamIndex > 0 && !rawData.contains("%")) {
        // Possibly encoded but more likely not
        String encodedQueryParams = URLEncoder.encode(rawData.substring(queryParamIndex + 1), "UTF-8");
        rawData = rawData.substring(0, queryParamIndex) + "?" + encodedQueryParams;
        rawData = rawData.replaceAll("%3D", "=");
        rawData = rawData.replaceAll("%26", "&");
      }

      log.debug("Using '{}' to create payment protocol session summary", rawData);
      PaymentProtocolService paymentProtocolService = CoreServices.getPaymentProtocolService();
      return Optional.fromNullable(paymentProtocolService.probeForPaymentSession(URI.create(rawData), true, null));

    } catch (UnsupportedEncodingException e) {
      log.error("UTF-8 is not supported on this platform");
    }

    return Optional.absent();

  }

  /**
   * The listening server
   */
  private static class MessageServerRunnable implements Runnable {

    private final ServerSocket serverSocket;

    public MessageServerRunnable(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
    }

    // Guava will call this due to the annotation
    @SuppressFBWarnings({"UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS"})
    // Requires its own shutdown subscriber to ensure correct shutdown
    @Subscribe
    public void onShutdownEvent(ShutdownEvent event) {

      if (ShutdownEvent.ShutdownType.SWITCH.equals(event.getShutdownType())) {
        // Can ignore the shutdown
        log.debug("Instance server runnable ignoring wallet switch shutdown event");
        return;
      }

      try {
        serverSocket.close();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }

    }

    @Override
    public void run() {

      CoreEvents.subscribe(this);

      boolean socketClosed = false;


      while (!socketClosed) {

        if (serverSocket.isClosed()) {
          socketClosed = true;
        } else {

          try {

            Socket client = serverSocket.accept();

            String message;
            try (InputStreamReader reader = new InputStreamReader(client.getInputStream(), Charsets.UTF_8)) {
              message = CharStreams.toString(reader);
              if (!message.startsWith(MESSAGE_START)) {
                // Message not following the correct format so is likely an error
                continue;
              }
              // Strip off the message start/end tags to leave a raw Bitcoin URI
              message = message.replace(MESSAGE_START, "").replace(MESSAGE_END, "");
            }
            client.close();

            log.debug("Received external data: '{}'", message);

            // Validate the data
            final BitcoinURI bitcoinURI;
            try {
              bitcoinURI = new BitcoinURI(message);
            } catch (BitcoinURIParseException e) {
              // Quietly ignore (don't log to avoid flooding logs)
              continue;
            }

            // Attempt to create an alert model from the Bitcoin URI
            Optional<AlertModel> alertModel = Models.newBitcoinURIAlertModel(bitcoinURI);

            // If successful the fire the event
            if (alertModel.isPresent()) {
              ControllerEvents.fireAddAlertEvent(alertModel.get());
            }

          } catch (IOException e) {
            socketClosed = true;
          }
        }
      } // End of while


    }
  }
}