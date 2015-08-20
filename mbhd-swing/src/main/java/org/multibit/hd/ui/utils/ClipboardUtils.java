package org.multibit.hd.ui.utils;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * <p>Utilities to provide the following to application:</p>
 * <ul>
 * <li>Transferring items out of the application via the Clipboard</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class ClipboardUtils {

  private static final Logger log = LoggerFactory.getLogger(ClipboardUtils.class);

  /**
   * Utilities have private constructors
   */
  private ClipboardUtils() {
  }

  /**
   * @param image The image to copy
   */
  public static void copyImageToClipboard(Image image) {

    try {

      final TransferableImage transferableImage = new TransferableImage(image);

      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

      clipboard.setContents(transferableImage, newClipboardOwner());

      log.debug("Copied image to clipboard");

    } catch (RuntimeException e) {
      // Ignore problems with clipboard
      log.warn("Copy image to clipboard failed: {}", e.getMessage());
    }

  }

  /**
   * @param value The text to copy
   */
  public static void copyStringToClipboard(String value) {

    try {
      // Copy the string to the clipboard
      StringSelection stringSelection = new StringSelection(value);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

      clipboard.setContents(stringSelection, newClipboardOwner());

      log.debug("Copied string to clipboard");

    } catch (RuntimeException e) {
      // Ignore problems with clipboard
      log.warn("Copy string to clipboard failed: {}", e.getMessage());
    }

  }

  /**
   * @return A string from the clipboard if present
   */
  public static Optional<String> pasteStringFromClipboard() {

    try {
      Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

      if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
          return Optional.of((String) transferable.getTransferData(DataFlavor.stringFlavor));
        } catch (UnsupportedFlavorException | IOException e) {
          log.warn("Failed to retrieve clipboard text", e);
        }
      }
    } catch (RuntimeException e) {
      // Ignore problems with clipboard
      log.warn("Paste string from clipboard failed: {}", e.getMessage());
    }

    return Optional.absent();

  }

  private static ClipboardOwner newClipboardOwner() {
    return new ClipboardOwner() {
      @Override
      public void lostOwnership(Clipboard clipboard, Transferable contents) {
        log.warn("Lost ownership of the system clipboard");
      }
    };
  }
}