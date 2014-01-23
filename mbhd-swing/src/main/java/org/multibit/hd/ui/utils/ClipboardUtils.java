package org.multibit.hd.ui.utils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * <p>Utilities to provide the following to application:</p>
 * <ul>
 * <li>Transferring items out of the application via the Clipboard</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ClipboardUtils {

  /**
   * Utilities have private constructors
   */
  private ClipboardUtils() {
  }

  /**
   * @param image The image to copy
   */
  public static void copyImageToClipboard(BufferedImage image) {

    TransferableImage trans = new TransferableImage(image);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(trans, new ClipboardOwner() {
      @Override
      public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // Do nothing
      }
    });

  }

  /**
   * @param value The text to copy
   */
  public static void copyStringToClipboard(String value) {

    // Copy the string to the clipboard
    StringSelection stringSelection = new StringSelection(value);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);

  }


  /**
   * Utility class to handle the transfer of an image via the Clipboard
   */
  private static class TransferableImage implements Transferable {

    Image image;

    /**
     * @param image The image
     */
    public TransferableImage(Image image) {
      this.image = image;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

      if (flavor.equals(DataFlavor.imageFlavor) && image != null) {
        return image;
      } else {
        throw new UnsupportedFlavorException(flavor);
      }

    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {

      DataFlavor[] flavors = new DataFlavor[1];
      flavors[0] = DataFlavor.imageFlavor;
      return flavors;

    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {

      DataFlavor[] dataFlavors = getTransferDataFlavors();

      for (DataFlavor flavor : dataFlavors) {
        if (dataFlavor.equals(flavor)) {
          return true;
        }
      }

      return false;
    }
  }
}