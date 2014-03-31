package org.multibit.hd.brit.matcher;

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
   * @param matcherStoreLocation The directory the matcher store backing files are stored in
   *
   * @return The Matcher store
   */
  public static MatcherStore newBasicMatcherStore(String matcherStoreLocation) {
    return new BasicMatcherStore(matcherStoreLocation);
  }
}
