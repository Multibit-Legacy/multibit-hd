package org.multibit.hd.core.dto;

import org.joda.time.DateTime;

import java.math.BigInteger;

/**
 *  <p>Interface to provide the following to payments view:<br>
 *  <ul>
 *  <li>Unified common interface for TransactionData and PaymentRequestData </li>
 *  </ul>
 *  
 */
public interface PaymentData {

  public PaymentType getType();

  public RAGStatus getStatus();

  public DateTime getDate();

  public BigInteger getAmountBTC();

  public String getNote();

  public String getDescription();
}
