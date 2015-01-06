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

    byte[] copy = new byte[payload.length];
    System.arraycopy(payload, 0, copy, 0, payload.length);

    this.payload = copy;
  }

  /**
   * @return The payload
   */
  public byte[] getPayload() {

    byte[] copy = new byte[payload.length];
    System.arraycopy(payload, 0, copy, 0, payload.length);

    return copy;
  }

  @Override
  public String toString() {
    return "EncryptedMatcherResponse{" +
      "payload=" + Arrays.toString(payload) +
      '}';
  }
}
