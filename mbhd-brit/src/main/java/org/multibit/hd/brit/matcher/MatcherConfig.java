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

  /**
   * The password to use to decrypt the secret key ring
   */
  private char[] password;

  /**
   * The location of the Matcher's store
   */
  private String matcherStoreLocation;

  public MatcherConfig(File matcherSecretKeyringFile, char[] password, String matcherStoreLocation) {
    this.matcherSecretKeyringFile = matcherSecretKeyringFile;
    this.password = password;
    this.matcherStoreLocation = matcherStoreLocation;
  }

  public File getMatcherSecretKeyringFile() {
    return matcherSecretKeyringFile;
  }

  public char[] getPassword() {
    return password;
  }

  public String getMatcherStoreLocation() {
    return matcherStoreLocation;
  }
}
