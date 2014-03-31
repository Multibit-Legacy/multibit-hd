package org.multibit.hd.brit.matcher;

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
   */
  public static Matcher newBasicMatcher(MatcherConfig matcherConfig) {
    return new BasicMatcher(matcherConfig);
  }
}
