package org.multibit.hd.brit.dto;

import java.math.BigInteger;

/**
 *  <p>DTO to provide the following to BRIT:<br>
 *  <ul>
 *  <li>This is the message sent by the Payer to the Matcher</li>
 *  </ul>
 *  </p>
 *  
 */
public class PayerRequest {

  private final int version = 1;

  private final BigInteger BRITWalletId;

  private final BigInteger sessionKey;

  public PayerRequest(BigInteger BRITWalletId, BigInteger sessionKey) {
    this.BRITWalletId = BRITWalletId;
    this.sessionKey = sessionKey;
  }

  public int getVersion() {
    return version;
  }

  public BigInteger getSessionKey() {
    return sessionKey;
  }

  public BigInteger getBRITWalletId() {
    return BRITWalletId;
  }
}
