package org.multibit.hd.core.dto;

/**
 *  <p>Interface to provide the following to payments view:<br>
 *  <ul>
 *  <li>Unified common interface for TransactionData and PaymentRequestData </li>
 *  </ul>
 *  
 */
public interface PaymentData {

  public String getNote();

  public void setNote(String note);

}
