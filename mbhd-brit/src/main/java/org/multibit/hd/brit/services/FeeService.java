package org.multibit.hd.brit.services;

import com.google.bitcoin.core.Wallet;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.multibit.hd.brit.dto.FeeState;

import java.net.URL;

/**
 *  <p>Service to provide the following to Payers:<br>
 *  <ul>
 *  <li>perform a lookup to the BRIT server to get the list of Bitcoin addresses fees need to be paid to</li>
 *  <li>provide the details of the next fee to be paid by the Payer</li>
 *  </p>
 *  
 */
public class FeeService {

  private PGPPublicKey matcherPublicKey;
  private URL matcherURL;

  /**
   * Construct a fee service
   * @param matcherPublicKey The PGP public key of the matcher service to perform exchanges with
   * @param matcherURL the HTTP URL to send PayerRequests to
   */
  public FeeService(PGPPublicKey matcherPublicKey, URL matcherURL) {
    this.matcherPublicKey = matcherPublicKey;
    this.matcherURL = matcherURL;
  }

  /**
   * Perform a BRIT exchange with the Matcher to work out what addresses the Payer should pay to
   * @param seed the seed of the Wallet (from which the britWalletId is worked out)
   * @param wallet the wallet to perform the BRIT exchange against
   */
  public FeeState performExchangeWithMatcher(byte[] seed, Wallet wallet) {
    // TODO
    return null;
  }

  /**
   * Calculate the FeeState for the wallet passed in.
   * This calculates what amount of fee needs paying when.
   */
  public FeeState calculateFeeState(Wallet wallet) {
    // TODO
    return null;
  }
}
