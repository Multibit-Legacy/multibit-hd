package org.multibit.hd.brit.dto;

import java.util.Arrays;

/**
 * <p>DTO to provide the following to Payer and Matcher:</p>
 * <ul>
 * <li>PGP encrypted version of MatcherResponse</li>
 * </ul>
 *
 * @since 0.0.1
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

  @Override
  public String toString() {
    return "EncryptedMatcherResponse{" +
      "payload=" + Arrays.toString(payload) +
      '}';
  }
}
