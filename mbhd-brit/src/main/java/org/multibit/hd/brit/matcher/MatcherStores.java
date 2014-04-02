package org.multibit.hd.brit.matcher;

import java.io.File;
import java.io.IOException;

/**
 * <p>Factory to provide the following to BRIT:</p>
 * <ul>
 * <li>Creation of new MatcherStore instances</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class MatcherStores {
  /**
   * Private constructor for utility classes
   */
  private MatcherStores() {
  }

  /**
   * @param matcherStoreDirectory The directory the matcher store backing files are stored in
   *
   * @return The Matcher store
   * @throws java.io.IOException If the matcher backing store
   */
  public static MatcherStore newBasicMatcherStore(File matcherStoreDirectory) throws IOException {
    return new BasicMatcherStore(matcherStoreDirectory);
  }
}
