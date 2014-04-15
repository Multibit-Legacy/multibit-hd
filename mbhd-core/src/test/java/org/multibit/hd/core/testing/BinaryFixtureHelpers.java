package org.multibit.hd.core.testing;

import com.google.common.io.Resources;

import java.io.IOException;

/**
 * <p>Helpers to provide the following to tests:</p>
 * <ul>
 * <li>Easy access to binary test fixtures</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BinaryFixtureHelpers {

  private BinaryFixtureHelpers() { /* singleton */ }

  /**
   * Reads the given fixture file from {@code src/test/resources} and returns its contents as a
   * byte array.
   *
   * @param filename the filename of the fixture file
   *
   * @return the contents of {@code src/test/resources/{filename}}
   *
   * @throws java.io.IOException if {@code filename} doesn't exist or can't be opened
   */
  public static byte[] fixture(String filename) throws IOException {
    return Resources.toByteArray(BinaryFixtureHelpers.class.getResource(filename));
  }
}
