package org.multibit.hd.brit.dto;

/**
 *  <p>DTO to provide the following to Payer and matcher:</p>
 *  <ul>
 *  <li>PGP encrypted version of PayerRequest</li>
 *  </ul>
 *  </p>
 *  
 */
public class EncryptedPayerRequest {
  /**
   * The encrypted payload
   */
  private byte[] payload;

  public EncryptedPayerRequest(byte[] payload) {
    this.payload = payload;
  }

  public byte[] getPayload() {
    return payload;
  }
}
