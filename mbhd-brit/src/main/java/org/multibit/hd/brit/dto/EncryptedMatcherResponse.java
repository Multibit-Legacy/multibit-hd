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

  /**
   * Version 1 response
   *
   * @param encryptedResponse The encrypted response with no HMAC
   */
  public EncryptedMatcherResponse(byte[] encryptedResponse) {

    this.payload = Arrays.copyOf(encryptedResponse, encryptedResponse.length);
  }

  /**
   * Version 2 response
   *
   * @param encryptedResponse The encrypted response
   * @param hmacResponse      The HMAC for the encrypted response
   */
  public EncryptedMatcherResponse(byte[] encryptedResponse, byte[] hmacResponse) {

    byte[] cloneEncryptedResponse = Arrays.copyOf(encryptedResponse, encryptedResponse.length);
    byte[] cloneHmacResponse = Arrays.copyOf(hmacResponse, hmacResponse.length);

    this.payload = new byte[cloneEncryptedResponse.length + cloneHmacResponse.length];
    System.arraycopy(encryptedResponse, 0, payload, 0, encryptedResponse.length);
    System.arraycopy(hmacResponse, 0, payload, encryptedResponse.length, hmacResponse.length);

  }

  /**
   * @return The payload
   */
  public byte[] getPayload() {

    return Arrays.copyOf(payload, payload.length);

  }

  @Override
  public String toString() {
    return "EncryptedMatcherResponse{" +
      "payload=" + Arrays.toString(payload) +
      '}';
  }
}
