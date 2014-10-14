package org.multibit.hd.brit.matcher;

import java.io.File;

/**
 * <p>Value object to provide the following to BRIT API:</p>
 * <ul>
 * <li>Matcher configuration</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class MatcherConfig {

  /**
   * The location of the secret key ring containing the Matcher secret key
   */
  private File matcherSecretKeyringFile;

  /**
   * The credentials to use to decrypt the secret key ring
   */
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
