package org.multibit.hd.brit.matcher;

import java.io.IOException;

/**
 * <p>Factory to provide the following to BRIT API:</p>
 * <ul>
 * <li>Create Matchers</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class Matchers {

  /**
   * Private constructor for utility classes
   */
  private Matchers() {
  }

  /**
   * @param matcherConfig The Matcher configuration
   *
   * @return A new basic Matcher
   *
   * @throws java.io.IOException If the Matcher backing store could not access the files
   */
  public static Matcher newBasicMatcher(MatcherConfig matcherConfig) throws IOException {
    return new BasicMatcher(matcherConfig);
  }
}
