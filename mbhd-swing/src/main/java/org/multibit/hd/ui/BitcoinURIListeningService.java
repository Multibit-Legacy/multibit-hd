package org.multibit.hd.ui;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.bitcoin.uri.BitcoinURIParseException;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.services.AbstractService;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.platform.listener.GenericOpenURIEvent;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
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
   * Multibit message start - must end with newline
   */
  public static final String MESSAGE_START = "$$MultiBitMessageStart$$\n";

  /**
   * Multibit message end - must end with newline (nonsense text to make it
   * unlikely it appears in a URI)
   */
  public static final String MESSAGE_END = "\n$$SD5DMRMessageEnd$$\n";

  private final Optional<BitcoinURI> bitcoinURI;

  private final Optional<String> rawURI;

  /**
   * @param args The command line arguments
   */
  public BitcoinURIListeningService(String[] args) {

    if (args == null || args.length == 0) {
      this.bitcoinURI = Optional.absent();
      rawURI = Optional.absent();
    } else {
      this.bitcoinURI = parseRawURI(args[0]);
      rawURI = bitcoinURI.isPresent() ? Optional.of(args[0]) : Optional.<String>absent();
    }

  }

  /**
   * <p>The parsed external Bitcoin URI</p>
   *
   * @return The Bitcoin URI
   */
  public Optional<BitcoinURI> getBitcoinURI() {
    return bitcoinURI;
  }

  /**
   * <p>The raw Bitcoin URI (if parsing was successful)</p>
   *
   * @return The raw Bitcoin URI
   */
  public Optional<String> getRawURI() {
    return rawURI;
  }

  /**
   * <p>Registers this instance of the application. Passing in the raw URI that was passed in on the command line</p>
   */
  @Override
  public void start() {

    // This service will run a single background thread
    requireFixedThreadPoolExecutor(1);

    try {

      // Attempt to own the localhost server socket allowing for a backlog of connections
      final ServerSocket socket = new ServerSocket(
        MULTIBIT_HD_NETWORK_SOCKET,
        10,
        InetAddress.getLoopbackAddress()
      );

      // Successfully owned the server port so handle ongoing messages as master
      getExecutorService().submit(getInstanceServerRunnable(socket));

      log.info("Listening for MultiBit HD instances on socket: '{}'", MULTIBIT_HD_NETWORK_SOCKET);

      // Must be OK to be here
    } catch (UnknownHostException e) {

      // Indicates that there is no loop back address on this machine
      log.error(e.getMessage(), e);

    } catch (IOException e) {

      if (bitcoinURI.isPresent()) {
        // Failed to own the server port so notify the other instance
        log.info("Port is already taken. Notifying first instance.");
        notifyOtherInstance();
      }
    }
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
        out.write(MESSAGE_START.getBytes());
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
   * @param socket The server socket on which messages will arrive
   *
   * @return The Runnable handling incoming messages
   */
  private Runnable getInstanceServerRunnable(final ServerSocket socket) {

    return new Runnable() {
      @Override
      public void run() {

        boolean socketClosed = false;

        Socket client = null;

        while (!socketClosed) {

          if (socket.isClosed()) {
            socketClosed = true;
          } else {

            try {
              client = socket.accept();

              BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
              String messageStart = in.readLine();
              if (MESSAGE_START.trim().equals(messageStart.trim())) {
                log.debug("Message prefix matched - new application instance found");

                StringBuilder messageBody = new StringBuilder();
                boolean isEOF = false;
                boolean firstLine = true;

                // Read in the message from the client
                while (!isEOF) {
                  String currentLine = in.readLine();
                  if (currentLine == null) {
                    // EOF
                    isEOF = true;
                  } else {
                    if (MESSAGE_END.trim().equals(currentLine.trim())) {
                      // EOF
                      isEOF = true;
                    } else {
                      if (!firstLine) {
                        messageBody.append("\n");
                      }
                      firstLine = false;
                      // Append the line
                      messageBody.append(currentLine);
                    }
                  }
                }

                String rawURI = messageBody.toString();
                log.debug("rawURI extracted from message as '{}'", rawURI);


                // TODO Hand this over to the alert mechanism
                final BitcoinURI bitcoinURI;
                try {
                  bitcoinURI = new BitcoinURI(rawURI);
                } catch (BitcoinURIParseException e) {
                  ExceptionHandler.handleThrowable(e);
                  return;
                }

                // Action to show the "send Bitcoin" wizard
                AbstractAction action = new AbstractAction() {
                  @Override
                  public void actionPerformed(ActionEvent e) {

                    ControllerEvents.fireRemoveAlertEvent();
                    Panels.showLightBox(Wizards.newSendBitcoinWizard(Optional.of(bitcoinURI)).getWizardScreenHolder());

                  }
                };
                JButton button = Buttons.newAlertPanelButton(action, MessageKey.YES, AwesomeIcon.CHECK);

                // Attempt to decode the Bitcoin URI
                Optional<String> alertMessage = Formats.formatAlertMessage(bitcoinURI);

                // If there is sufficient information in the Bitcoin URI display it to the user as an alert
                if (alertMessage.isPresent()) {

                  AlertModel alertModel = Models.newAlertModel(
                    alertMessage.get(),
                    RAGStatus.AMBER,
                    button
                  );

                  // Add the alert
                  //ControllerEvents.fireOpenURIEvent(alertModel);
                }

                // Wrap this in a generic event
                GenericOpenURIEvent event = new GenericOpenURIEvent() {
                  @Override
                  public URI getURI() {
                    return URI.create(bitcoinURI.toString());
                  }
                };

                // Hand over to the main controller
                //mainController.onOpenURIEvent(event);


              }

              in.close();
              client.close();
            } catch (IOException e) {
              socketClosed = true;
            }
          }
        }

        // exited while due to shutdown request - shutdown socket

        if (client != null) {
          try {
            client.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        if (socket != null) {
          try {
            socket.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        log.debug("Socket is shutdown.");

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
