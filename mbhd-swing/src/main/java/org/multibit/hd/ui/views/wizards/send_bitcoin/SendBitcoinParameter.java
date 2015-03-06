package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import org.bitcoinj.uri.BitcoinURI;
import org.multibit.hd.core.dto.PaymentSessionSummary;

/**
 * <p>Parameter object to provide the following to "send bitcoin" wizard:</p>
 * <ul>
 * <li>Access to various parameters needed during initialisation</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendBitcoinParameter {

  private final Optional<BitcoinURI> bitcoinURI;
  private final Optional<PaymentSessionSummary> paymentSessionSummary;

  /**
   * @param bitcoinURI The Bitcoin URI
   * @param paymentSessionSummary The Payment Protocol session summary providing meta data about the payment
   */
  public SendBitcoinParameter(BitcoinURI bitcoinURI, PaymentSessionSummary paymentSessionSummary) {
    this.bitcoinURI = Optional.fromNullable(bitcoinURI);
    this.paymentSessionSummary = Optional.fromNullable(paymentSessionSummary);
  }

  /**
   * @return A BIP21 Bitcoin URI providing payment details
   */
  public Optional<BitcoinURI> getBitcoinURI() {
    return bitcoinURI;
  }

  /**
   * @return A Payment Protocol session summary providing meta data about the payment
   */
  public Optional<PaymentSessionSummary> getPaymentSessionSummary() {
    return paymentSessionSummary;
  }
}
