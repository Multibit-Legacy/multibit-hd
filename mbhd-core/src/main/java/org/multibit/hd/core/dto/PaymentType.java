package org.multibit.hd.core.dto;

/**
 *  <p>DTO to provide the following to PaymentData:<br>
 *  <ul>
 *  <li>The type of the transaction or payment</li>
 *  </ul>
 *  </p>
 *  
 */
public enum PaymentType {
  /**
   * The payment has been requested, using the 'Request bitcoin' screen but no transaction to that address
   * has been received.
   */
  REQUESTED(CoreMessageKey.PAYMENT_REQUESTED),

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

