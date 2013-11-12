package org.multibit.hd.ui.javafx.utils;

import org.multibit.hd.ui.javafx.exceptions.UIException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Utility to provide the following to stream handlers:</p>
 * <ul>
 * <li>Quiet closing</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class Streams {

  /**
   * Utilities have private constructors
   */
  private Streams() {
  }

  /**
   * <p>Close and rethrow any exceptions as runtime</p>
   *
   * @param is The input stream to close
   */
  public static void closeQuietly(InputStream is) {

    if (is == null) {
      return;
    }
    try {
      is.close();
    } catch (IOException e) {
      throw new UIException(e);
    }

  }

  /**
   * <p>Close and rethrow any exceptions as runtime</p>
   *
   * @param is The input stream to close
   */
  public static void closeQuietly(OutputStream is) {

    if (is == null) {
      return;
    }
    try {
      is.close();
    } catch (IOException e) {
      throw new UIException(e.getMessage(), e);
    }

  }

}
