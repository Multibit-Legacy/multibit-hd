package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;

/**
 * <p>Parameter object to provide the following to send bitcoin wizard:</p>
 * <ul>
 * <li>Access to various parameters needed during initialisation</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendBitcoinParameter {

  private final Optional<BitcoinURI> bitcoinURI;
  private final boolean emptyWallet;

  /**
   * @param bitcoinURI  The Bitcoin URI
   * @param emptyWallet True if the wallet should be emptied and all payable fees paid
   */
  public SendBitcoinParameter(Optional<BitcoinURI> bitcoinURI, boolean emptyWallet) {
    this.bitcoinURI = bitcoinURI;
    this.emptyWallet = emptyWallet;
  }

  public Optional<BitcoinURI> getBitcoinURI() {
    return bitcoinURI;
  }

  public boolean isEmptyWallet() {
    return emptyWallet;
  }
}
