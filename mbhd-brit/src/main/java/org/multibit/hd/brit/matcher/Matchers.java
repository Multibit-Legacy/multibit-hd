package org.multibit.hd.brit.matcher;

/**
 *  <p>Factory to provide the following to BRIT classes :<br>
 *  <ul>
 *  <li>Create matchers</li>
 *  </ul>
 *  </p>
 *  
 */
public class Matchers {

  /**
   * Private constructor for utility classes
   */
  private Matchers() {
  }

  public static Matcher newBasicMatcher(MatcherConfig matcherConfig) {
    return new BasicMatcher(matcherConfig);
  }
}
