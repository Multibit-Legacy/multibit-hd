package org.multibit.hd.brit.services;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.multibit.hd.brit.crypto.PGPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <p>Factory to provide the following to application API:
 * <ul>
 * <li>Entry point to configured instances of BRIT services</li>
 * </ul>
 * </p>
 *
 * @since 0.0.1
 */
public class BRITServices {

  private static final Logger log = LoggerFactory.getLogger(BRITServices.class);

  /**
   * The URL of the live matcher daemon
   */
  public static final String LIVE_MATCHER_URL = "https://multibit.org/brit";

  /**
   * The live matcher PGP public key file
   */
  public static final String LIVE_MATCHER_PUBLIC_KEY_FILE = "/multibit-org-matcher-key.asc";

  /**
   * Utilities have a private constructor
   */
  private BRITServices() {
  }

  /**
   * @return A new FeeService pointing to the live server
   */
  public static FeeService newFeeService() throws IOException, PGPException {
    log.debug("Creating new fee service");
    URL matcherURL = new URL(LIVE_MATCHER_URL);
    return new FeeService(getMatcherPublicKey(), matcherURL);

  }

  /**
   * @return The Matcher public key
   *
   * @throws IOException  If the resource could not be read
   * @throws PGPException If something goes wrong with PGP reading it
   */
  public static PGPPublicKey getMatcherPublicKey() throws IOException, PGPException {
    InputStream pgpPublicKeyInputStream = BRITServices.class.getResourceAsStream(LIVE_MATCHER_PUBLIC_KEY_FILE);
    return PGPUtils.readPublicKey(pgpPublicKeyInputStream);
  }

}
