package org.multibit.hd.ui.services;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.services.AbstractService;
import org.multibit.hd.core.services.CoreServices;
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
 * <p>Service to maintain a localhost server socket on port 8330. If this socket is taken, then another instance of MultiBit HD is likely
 * running and so this service will hand over any Bitcoin URI it was given on startup to whatever is listening on that port.</p>
 */
public class BitcoinURIListeningService extends AbstractService {

  private static final Logger log = LoggerFactory.getLogger(BitcoinURIListeningService.class);

  /**
   * MultiBit port number as specified in <a href="http://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers">the list of TCP and UDP port numbers</a>.
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

  private final Optional<String> rawURI;

  private Optional<ServerSocket> serverSocket = Optional.absent();

  /**
   * @param args The command line arguments
   */
  public BitcoinURIListeningService(String[] args) {

    super();

    if (args == null || args.length == 0) {
      this.bitcoinURI = Optional.absent();
      rawURI = Optional.absent();
    } else {
      this.bitcoinURI = parseRawURI(args[0]);
      rawURI = bitcoinURI.isPresent() ? Optional.of(args[0]) : Optional.<String>absent();
    }

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

      if (bitcoinURI.isPresent()) {
        // Failed to own the server port so notify the other instance
        log.info("Port is already taken. Notifying first instance.");
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
   * <p>The parsed Bitcoin URI (if parsing was successful) provided during startup</p>
   *
   * @return The Bitcoin URI
   */
  public Optional<BitcoinURI> getBitcoinURI() {
    return bitcoinURI;
  }

  /**
   * <p>The raw Bitcoin URI (if parsing was successful) provided during startup</p>
   *
   * @return The raw Bitcoin URI
   */
  Optional<String> getRawURI() {
    return rawURI;
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

    Preconditions.checkState(bitcoinURI.isPresent(), "'bitcoinURI' must be present");

    try {
      Socket clientSocket = new Socket(
        InetAddress.getLoopbackAddress(),
        MULTIBIT_HD_NETWORK_SOCKET
      );

      try (OutputStream out = clientSocket.getOutputStream()) {

        // Write out the raw Bitcoin URI
        out.write(MESSAGE_START.getBytes(Charsets.UTF_8));
        out.write(rawURI.get().getBytes());
        out.write(MESSAGE_END.getBytes());

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

    return new Runnable() {

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

        CoreServices.uiEventBus.register(this);

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

              log.debug("Received Bitcoin URI message: '{}'", message);

              // Validate the data
              final BitcoinURI bitcoinURI;
              try {
                bitcoinURI = new BitcoinURI(message);
              } catch (BitcoinURIParseException e) {
                // Quietly ignore
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
    };
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
   * @param rawURI The raw URI straight from an assumed untrusted external source
   */
  private Optional<BitcoinURI> parseRawURI(String rawURI) {

    log.debug("Decoding Bitcoin URI from '{}'", rawURI);

    if (Strings.isNullOrEmpty(rawURI)) {
      return Optional.absent();
    }

    try {

      // Basic initial checking for URL encoding
      int queryParamIndex = rawURI.indexOf('?');
      if (queryParamIndex > 0 && !rawURI.contains("%")) {
        // Possibly encoded but more likely not
        String encodedQueryParams = URLEncoder.encode(rawURI.substring(queryParamIndex + 1), "UTF-8");
        rawURI = rawURI.substring(0, queryParamIndex) + "?" + encodedQueryParams;
        rawURI = rawURI.replaceAll("%3D", "=");
        rawURI = rawURI.replaceAll("%26", "&");
      }

      log.debug("Using '{}' to create Bitcoin URI", rawURI);
      return Optional.of(new BitcoinURI(rawURI));

    } catch (UnsupportedEncodingException e) {
      log.error("UTF-8 is not supported on this platform");
    } catch (BitcoinURIParseException e) {
      log.error("Bitcoin URI not valid. Error ", e);
    }

    return Optional.absent();

  }

}
