package org.multibit.hd.brit.matcher;

/**
 *  <p>Factory to provide the following to BRIT classes :<br>
 *  <ul>
 *  <li>Create matchers</li>
 *  </ul>
 *  </p>
 *  
 */
public class MatcherFactory {

  /**
   * Private constructor for utility classes
   */
  private MatcherFactory() {
  }

  public static Matcher createBasicMatcher(MatcherConfig matcherConfig) {
    return new BasicMatcher(matcherConfig);
  }
}
