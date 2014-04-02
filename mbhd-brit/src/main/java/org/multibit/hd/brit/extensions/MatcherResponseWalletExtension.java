package org.multibit.hd.brit.extensions;

import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletExtension;
import org.multibit.hd.brit.dto.MatcherResponse;

/**
 *  <p>Walet Extension to provide the following to Wallet:<br>
 *  <ul>
 *  <li>Persistence of a MatcherResponse</li>
 *  </ul>
 *  Example:FeeService<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class MatcherResponseWalletExtension implements WalletExtension {
  public static final String MATCHER_RESPONSE_WALLET_EXTENSION_ID = "org.multibit.hd.brit.dto.MatcherResponse";

  private MatcherResponse matcherResponse;;

  public MatcherResponseWalletExtension() {
    this.matcherResponse = null;
  }

  public MatcherResponseWalletExtension(MatcherResponse matcherResponse) {
    this.matcherResponse = matcherResponse;
  }

  @Override
  public String getWalletExtensionID() {
    return MATCHER_RESPONSE_WALLET_EXTENSION_ID;
  }

  @Override
  public boolean isWalletExtensionMandatory() {
    return false;
  }

  @Override
  public byte[] serializeWalletExtension() {
    if (matcherResponse != null) {
      return matcherResponse.serialise();
  } else {
    return new byte[0];
  }
}

  @Override
  public void deserializeWalletExtension(Wallet containingWallet, byte[] data) throws Exception {
    matcherResponse = MatcherResponse.parse(data);
  }

  public MatcherResponse getMatcherResponse() {
    return matcherResponse;
  }
}
