package org.multibit.hd.ui.services;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Queues;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.PaymentSessionSummary;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.AbstractService;
import org.multibit.hd.core.services.PaymentProtocolService;
import org.multibit.hd.core.utils.BitcoinNetwork;
import org.multibit.hd.core.utils.OSUtils;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.models.Models;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Queue;

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

  /**
   * Allow up to 50 entries in the queue to represent a batch of work
   */
  /* package */ static final Queue<AlertModel> alertModelQueue = Queues.newArrayBlockingQueue(50);

  private Optional<ServerSocket> serverSocket = Optional.absent();

  private final Optional<String[]> args;

  /**
   * @param args The command line arguments
   */
  public ExternalDataListeningService(String[] args) {

    super();

    if (args == null || args.length == 0) {
      log.debug("No command line arguments to parse");
      this.args = Optional.absent();
      return;
    }

    // Must have some command line arguments to be here
    for (String arg : args) {
      parseRawData(arg);
    }

    // May need to hand over
    this.args = Optional.of(args);

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

      // Successfully owned the server port

      // Handle ongoing messages as master
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

      // Hand over whatever was given to ensure other instance can report on success/failure
      if (args.isPresent()) {
        for (String arg : args.get()) {
          writeToSocket(arg);
        }
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
   * <p>Write to the MultiBit HD network socket</p>
   *
   * @param message The message to send (appropriate wrapping will be added)
   */
  public static synchronized void writeToSocket(String message) {

    Preconditions.checkNotNull(message, "'message' must be present");

    try(Socket clientSocket = new Socket(
            InetAddress.getLoopbackAddress(),
            MULTIBIT_HD_NETWORK_SOCKET); 
        OutputStream out = clientSocket.getOutputStream()) {
      
        // Write out the raw external data for parsing by the other instance
        out.write(MESSAGE_START.getBytes(Charsets.UTF_8));
        out.write(message.getBytes(Charsets.UTF_8));
        out.write(MESSAGE_END.getBytes(Charsets.UTF_8));
        
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

    log.debug("Successful write to external data listening service.");

  }

  /**
   * <p>The queue of alert models based on data provided externally (command line arguments or via the socket)</p>
   *
   * @return The alert model queue (may be empty)
   */
  public Queue<AlertModel> getAlertModelQueue() {
    return alertModelQueue;
  }

  /**
   * <p>The server socket created as the master if no other was in place</p>
   * <p>Reduced visibility for testing</p>
   *
   * @return The server socket
   */
  Optional<ServerSocket> getServerSocket() {
    return serverSocket;
  }

  /**
   * <p>Parse the raw data into an alert containing one of the following:</p>
   * <ul>
   * <li>Bitcoin URI (BIP21)</li>
   * <li>Payment Request Summary (BIP70)</li>
   * <li>Error message</li>
   * </ul>
   *
   * @param rawData The raw data from the external source
   */
  public static void parseRawData(String rawData) {

    log.debug("Parsing raw data: '{}'", rawData);

    // This is final fallback failure message for unparsable raw data
    final String failureMessage = Languages.safeText(CoreMessageKey.PAYMENT_SESSION_ERROR, rawData);

    // Quick check for BIP21 Bitcoin URI
    if (rawData.startsWith("bitcoin") && !rawData.contains("r=")) {
      // Treat as BIP21 Bitcoin URI
      final Optional<BitcoinURI> bitcoinURI = parseBitcoinURI(rawData);

      // Must be on the EDT and run synchronously to simplify testing
      if (SwingUtilities.isEventDispatchThread()) {
        addBitcoinURIToQueue(bitcoinURI, failureMessage);
      } else {
        try {
          // Wrap in synchronous EDT operation
          SwingUtilities.invokeAndWait(
            new Runnable() {
              @Override
              public void run() {
                addBitcoinURIToQueue(bitcoinURI, failureMessage);
              }
            });
        } catch (InterruptedException | InvocationTargetException e) {
          log.error("Unexpected UI exception", e);
        }
      }

      // Fall through to end processing

    } else {

      // Treat as a Payment Protocol URI referencing PaymentRequest data

      // Supports:
      // URL forms starting with "http", "https", "file", "classpath"
      // Native file path (e.g. "C:\somewhere.bitcoinpaymentrequest")
      // BIP72 variant of "bitcoin"
      //
      final Optional<PaymentSessionSummary> paymentSessionSummary = parsePaymentSessionSummary(rawData);

      // Must be on the EDT and run synchronously to simplify testing
      if (SwingUtilities.isEventDispatchThread()) {
        addPaymentSessionToQueue(paymentSessionSummary, failureMessage);
      } else {
        try {
          // Wrap in synchronous EDT operation
          SwingUtilities.invokeAndWait(
            new Runnable() {
              @Override
              public void run() {
                addPaymentSessionToQueue(paymentSessionSummary, failureMessage);
              }
            });
        } catch (InterruptedException | InvocationTargetException e) {
          log.error("Unexpected UI exception", e);
        }
      }
    }

    // We've done all our processing
    // Note: Don't "auto-purge" the queues since it leads to complex test scenarios
    // and violates the principle of least surprise in use

  }

  /**
   * @param bitcoinURI     The Bitcoin URI to add (if present)
   * @param failureMessage A failure message to use if not present
   */
  private static void addBitcoinURIToQueue(final Optional<BitcoinURI> bitcoinURI, final String failureMessage) {

    log.debug("Building BitcoinURI alert model");
    if (bitcoinURI.isPresent()) {

      // Attempt to create an alert model from the Bitcoin URI
      Optional<AlertModel> alertModel = Models.newBitcoinURIAlertModel(bitcoinURI.get());
      if (alertModel.isPresent()) {
        alertModelQueue.add(alertModel.get());
        return;
      }
    }

    log.debug("Using failure alert model");
    alertModelQueue.add(Models.newAlertModel(failureMessage, RAGStatus.RED));
  }

  /**
   * @param paymentSessionSummary The PaymentSessionSummary to add (if present)
   * @param failureMessage        A failure message to use if not present
   */
  private static void addPaymentSessionToQueue(Optional<PaymentSessionSummary> paymentSessionSummary, String failureMessage) {

    log.debug("Building payment session alert model");
    if (paymentSessionSummary.isPresent()) {

      // Attempt to create an alert model from the Bitcoin URI
      Optional<AlertModel> alertModel = Models.newPaymentRequestAlertModel(paymentSessionSummary.get());
      if (alertModel.isPresent()) {
        alertModelQueue.add(alertModel.get());
        return;
      }
    }

    log.debug("Using failure alert model");
    alertModelQueue.add(Models.newAlertModel(failureMessage, RAGStatus.RED));
  }

  /**
   * Purge the alert model queue if the UI state allows it
   */
  public static synchronized void purgeAlertModelQueue() {

    boolean canPurge = WalletManager.INSTANCE.getCurrentWalletSummary().isPresent();

    if (canPurge) {

      log.debug("Wallet is unlocked so firing alert events (if present)");

      while (!alertModelQueue.isEmpty()) {
        AlertModel alertModel = alertModelQueue.poll();
        ControllerEvents.fireAddAlertEvent(alertModel);
      }

    } else {

      log.debug("Wallet is locked so deferring purge");

    }

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
  private static Optional<BitcoinURI> parseBitcoinURI(String rawData) {

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

      return Optional.fromNullable(new BitcoinURI(rawData));

    } catch (UnsupportedEncodingException e) {
      log.error("UTF-8 is not supported on this platform");
    } catch (BitcoinURIParseException e) {
      log.error("BIP21 Bitcoin URI not valid. Error ", e);
    }

    // Must have failed to be here
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
   *
   * @return The PaymentSessionSummary or absent if there was a problem
   */
  private static Optional<PaymentSessionSummary> parsePaymentSessionSummary(String rawData) {

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
      PaymentProtocolService paymentProtocolService = new PaymentProtocolService(BitcoinNetwork.current().get());
      Optional<URI> uri = parseRawDataAsUri(rawData);
      if (uri.isPresent()) {

        return Optional.fromNullable(paymentProtocolService.probeForPaymentSession(
            uri.get(),
            false, // We do not enforce a signature in order to create a PaymentSession but we do check for trust levels
            null // Use the default trust store (usually "mbhd-cacerts")
          ));
      }

    } catch (UnsupportedEncodingException e) {
      log.error("UTF-8 is not supported on this platform");
    }

    return Optional.absent();

  }

  /**
   * Attempt to convert raw data from the user into a Payment Request URI
   *
   * @param rawData The raw data straight from an assumed untrusted external source
   *
   * @return A Payment Request URI or absent if there was a problem
   */
  static Optional<URI> parseRawDataAsUri(String rawData) {

    // Check for URI form (require ":/" for OS X file URIs)
    if (rawData.startsWith("bitcoin:") || rawData.contains(":/")) {
      // Very likely to be a URI
      try {
        // Attempt to create the URI
        return Optional.of(URI.create(rawData));
      } catch (RuntimeException e) {
        return Optional.absent();
      }
    }

    if (!OSUtils.isWindows()) {
      // Convert from Windows to Java format under principle of least surprise
      rawData = rawData.replace("\\", "/");
    }

    try {
      // Try to build a file path from the raw data
      File file = Paths.get(rawData).toFile();
      if (file.exists() && file.canRead()) {
        // File has valid access permissions so can continue
        return Optional.of(file.toURI());
      } else {
        // Must have failed to be here
        log.warn("No permissions to access the file");
        return Optional.absent();
      }
    } catch (InvalidPathException e) {
      log.warn("Invalid path: '{}'", rawData);
      return Optional.absent();
    }

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

            String rawData;
            try (InputStreamReader reader = new InputStreamReader(client.getInputStream(), Charsets.UTF_8)) {
              rawData = CharStreams.toString(reader);
              if (!rawData.startsWith(MESSAGE_START)) {
                // Message not following the correct format so is likely an error
                continue;
              }
              // Strip off the message start/end tags to leave a raw Bitcoin URI
              rawData = rawData.replace(MESSAGE_START, "").replace(MESSAGE_END, "");
            }
            client.close();

            log.debug("Received external data: '{}'", rawData);

            // Attempt to add to the queues and issue an alert
            ExternalDataListeningService.parseRawData(rawData);

            // Now would be a good time to alert the user
            ExternalDataListeningService.purgeAlertModelQueue();

          } catch (IOException e) {
            socketClosed = true;
          }
        }
      } // End of while


    }
  }
}