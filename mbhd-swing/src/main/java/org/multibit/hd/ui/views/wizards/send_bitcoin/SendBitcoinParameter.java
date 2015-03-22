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
  // TODO Reinstate this as a PaymentSessionSummary and rebuild the PaymentRequest etc from PaymentRequestData
  // using a dedicated process in PaymentProtocolService during deserialization
  // As it stands this is using the wrong object in the wizard model and subverting/replicating the purpose of the
  // original PaymentSessionSummary
  private final Optional<PaymentRequestData> paymentRequestData;

  /**
   * @param bitcoinURI         The Bitcoin URI
   * @param paymentRequestData The payment request data containing information about this payment
   */
  public SendBitcoinParameter(BitcoinURI bitcoinURI, Optional<PaymentRequestData> paymentRequestData) {
    this.bitcoinURI = Optional.fromNullable(bitcoinURI);
    this.paymentRequestData = paymentRequestData == null ? Optional.<PaymentRequestData>absent() : paymentRequestData;
  }

  /**
   * @return A BIP21 Bitcoin URI providing payment details
   */
  public Optional<BitcoinURI> getBitcoinURI() {
    return bitcoinURI;
  }

  /**
   * @return A Payment Request Data that represents a persisted BIP70 Payment Request
   */
  public Optional<PaymentRequestData> getPaymentRequestData() {
    return paymentRequestData;
  }
}
