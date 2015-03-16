package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import org.bitcoinj.uri.BitcoinURI;
import org.multibit.hd.core.dto.PaymentRequestData;

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
  private final Optional<PaymentRequestData> paymentRequestDataOptional;

  /**
   * @param bitcoinURI The Bitcoin URI
   * @param paymentRequestDataOptional The payment request data containing information about this payment
   */
  public SendBitcoinParameter(BitcoinURI bitcoinURI, Optional<PaymentRequestData> paymentRequestDataOptional) {
    this.bitcoinURI = Optional.fromNullable(bitcoinURI);
    this.paymentRequestDataOptional = paymentRequestDataOptional == null ? Optional.<PaymentRequestData>absent() : paymentRequestDataOptional;
  }

  /**
   * @return A BIP21 Bitcoin URI providing payment details
   */
  public Optional<BitcoinURI> getBitcoinURI() {
    return bitcoinURI;
  }

  public Optional<PaymentRequestData> getPaymentRequestDataOptional() {
    return paymentRequestDataOptional;
  }
}
