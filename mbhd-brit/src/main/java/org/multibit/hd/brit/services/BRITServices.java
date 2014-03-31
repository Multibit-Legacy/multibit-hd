package org.multibit.hd.brit.services;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * <p>Factory to provide the following to application API:
 * <ul>
 * <li>Entry point to configured instances of BRIT services</li>
 * </ul>
 * </p>
 *
 * @since 0.0.1
 *  
 */
public class BRITServices {

  private static final Logger log = LoggerFactory.getLogger(BRITServices.class);

  /**
   * Utilities have a private constructor
   */
  private BRITServices() {
  }

  /**
   * @return A new FeeService
   */
  public static FeeService newFeeService(PGPPublicKey matcherPublicKey, URL matcherURL) {
    log.debug("Creating new fee service");
    return new FeeService(matcherPublicKey, matcherURL);

  }
}
