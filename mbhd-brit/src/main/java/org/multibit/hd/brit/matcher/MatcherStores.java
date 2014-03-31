package org.multibit.hd.brit.matcher;

/**
 *  <p>Factory to provide the following to BRIT:</p>
 *  <ul>
 *  <li>Creation of new MatcherStore instances</li>
 *  </ul>
 *  </p>
 *  
 */
public class MatcherStores {
  /**
   * Private constructor for utility classes
   */
  private MatcherStores() {
  }

  public static MatcherStore newBasicMatcherStore(String matcherStoreLocation) {
    return new BasicMatcherStore(matcherStoreLocation);
  }
}
