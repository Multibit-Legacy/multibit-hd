package org.multibit.hd.brit.extensions;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.core.WalletExtension;
import org.multibit.hd.brit.dto.MatcherResponse;

/**
 * <p>Wallet Extension to provide the following to Wallet:</p>
 * <ul>
 * <li>Persistence of a MatcherResponse</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class MatcherResponseWalletExtension implements WalletExtension {

  public static final String MATCHER_RESPONSE_WALLET_EXTENSION_ID = "org.multibit.hd.brit.dto.MatcherResponse";

  private MatcherResponse matcherResponse;

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
