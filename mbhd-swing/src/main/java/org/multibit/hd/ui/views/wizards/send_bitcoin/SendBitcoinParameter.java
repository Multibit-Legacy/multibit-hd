package org.multibit.hd.ui.views.wizards.send_bitcoin;

import org.bitcoinj.uri.BitcoinURI;
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

  /**
   * @param bitcoinURI  The Bitcoin URI
   *
   */
  public SendBitcoinParameter(Optional<BitcoinURI> bitcoinURI) {
    this.bitcoinURI = bitcoinURI;
  }

  public Optional<BitcoinURI> getBitcoinURI() {
    return bitcoinURI;
  }

}
