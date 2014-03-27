package org.multibit.hd.brit.matcher;

import java.io.File;

/**
 *  <p>Configuration to provide the following to MatcherFactory:<br>
 *  <ul>
 *  <li></li>
 *  </ul>
 *  </p>
 *  
 */
public class MatcherConfig {

  /**
   * The location of the secret key ring containing the Matcher secret key
   */
  private File matcherSecretKeyringFile;

  public MatcherConfig(File matcherSecretKeyringFile) {
    this.matcherSecretKeyringFile = matcherSecretKeyringFile;
  }

  public File getMatcherSecretKeyringFile() {
    return matcherSecretKeyringFile;
  }
}
