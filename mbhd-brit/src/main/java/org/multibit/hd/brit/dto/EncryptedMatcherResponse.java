package org.multibit.hd.brit.dto;

/**
 * <p>DTO to provide the following to Payer and matcher:</p>
 * <ul>
 * <li>PGP encrypted version of MatcherResponse</li>
 * </ul>
 */
public class EncryptedMatcherResponse {
  /**
   * The encrypted payload
   */
  private byte[] payload;

  public EncryptedMatcherResponse(byte[] payload) {
    this.payload = payload;
  }

  /**
   * @return The payload
   */
  public byte[] getPayload() {
    return payload;
  }
}
