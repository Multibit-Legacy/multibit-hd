package org.multibit.hd.ui.utils;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * <p>Utility class to handle the transfer of an image via the Clipboard</p>
 * <p>Reduced visibility since it should not be created outside of this package</p>
 */
class TransferableImage implements Transferable {

  private static final Logger log = LoggerFactory.getLogger(TransferableImage.class);

  private final Image image;

  /**
   * @param image The image
   */
  public TransferableImage(Image image) {

    Preconditions.checkNotNull(image, "'image' must be present");

    this.image = image;
  }

  @Override
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

    if (!DataFlavor.imageFlavor.equals(flavor)) {
      throw new UnsupportedFlavorException(flavor);
    }

    log.debug("Clipboard image requested");

    return image;
  }

  @Override
  public DataFlavor[] getTransferDataFlavors() {

    return new DataFlavor[]{
      DataFlavor.imageFlavor
    };
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
