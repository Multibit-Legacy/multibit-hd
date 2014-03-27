package org.multibit.hd.brit.dto;

/**
 *  <p>DTO to provide the following to BRIT:<br>
 *  <ul>
 *  <li>This is the unencrypted version of the message sent by the Payer to the Matcher</li>
 *  <li>Typically 'encrypt' is called and the EncryptedPayerRequest is actually sent on the wire</li>
 *  </ul>
 *  </p>
 *  
 */
public class PayerRequest {

  private final BRITWalletId britWalletId;

  private final byte[] sessionKey;

  public PayerRequest(BRITWalletId britWalletId, byte[] sessionKey) {
    this.britWalletId = britWalletId;
    this.sessionKey = sessionKey;
  }

  public int getVersion() {
    return 1;
  }

  public byte[] getSessionKey() {
    return sessionKey;
  }

  public BRITWalletId getBRITWalletId() {
    return britWalletId;
  }
}
