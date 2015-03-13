package org.multibit.hd.core.dto;

/**
 * <p>DTO to provide the following to PaymentData:</p>
 * <ul>
 * <li>The type of the transaction or payment</li>
 * </ul>
 * </p>
 *
 */
public enum PaymentType {
  /**
   * A BIP70 payment request has been received but it is not paid yet
   */
  THEY_REQUESTED(CoreMessageKey.PAYMENT_REQUESTED_BY_THEM),

  /**
    * The payment has been requested, using the 'Request bitcoin' screen but no transaction to that address
    * has been received.
    */
  YOU_REQUESTED(CoreMessageKey.PAYMENT_REQUESTED_BY_YOU),

  /**
   * The payment has been partly paid - some bitcoin has been received but less than the amount requested
   */
  PART_PAID(CoreMessageKey.PAYMENT_PART_PAID),

  /**
   * The payment has been fully paid
   */
  PAID(CoreMessageKey.PAYMENT_PAID),

  /**
   * The transaction is currently being received to the wallet i.e. zero confirmation
   */
  RECEIVING(CoreMessageKey.PAYMENT_RECEIVING),

  /**
   * The transaction has been received i.e at least one confirmation
   */
  RECEIVED(CoreMessageKey.PAYMENT_RECEIVED),

  /**
   * The transaction is currently being sent i.e. zero confirmation
   */
  SENDING(CoreMessageKey.PAYMENT_SENDING),

  /**
   * The transaction has been sent i.e. at least one confirmation
   */
  SENT(CoreMessageKey.PAYMENT_SENT),;

  // End of enum;

  private CoreMessageKey localisationKey;

  private PaymentType(CoreMessageKey localisationKey) {
    this.localisationKey = localisationKey;
  }

  public CoreMessageKey getLocalisationKey() {
    return localisationKey;
  }

}

