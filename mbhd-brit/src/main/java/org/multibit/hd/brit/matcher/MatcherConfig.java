package org.multibit.hd.brit.matcher;

import java.io.File;

/**
 *  <p>Configuration to provide the following to Matchers:<br>
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
  private char[] password;

  public MatcherConfig(File matcherSecretKeyringFile, char[] password) {
    this.matcherSecretKeyringFile = matcherSecretKeyringFile;
    this.password = password;
  }

  public File getMatcherSecretKeyringFile() {
    return matcherSecretKeyringFile;
  }

  public char[] getPassword() {
    return password;
  }
}
