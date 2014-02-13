package org.multibit.hd.core.dto;

/**
 *  <p>[Pattern] to provide the following to [related classes]:<br>
 *  <ul>
 *  <li></li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public enum TransactionType {

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
   * The transaction has been requested, using the 'Request bitcoin' screen but no transaction to that address
   * has been received.
   */
  REQUESTED

  // End of enum
  ;

}

