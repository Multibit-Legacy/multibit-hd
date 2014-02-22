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
   * The transaction is currently being received to the wallet i.e. zero confirmation
   */
  RECEIVING,

  /**
   * The transaction has been received i.e at least one confirmation
   */
  RECEIVED,

  /**
   * The transaction is currently being sent i.e. zero confirmation
   */
  SENDING,

  /**
   * The transaction has been sent i.e. at least one confirmation
   */
  SENT,

  /**
   * The payment has been requested, using the 'Request bitcoin' screen but no transaction to that address
   * has been received.
   */
  REQUESTED

  // End of enum
  ;

}

