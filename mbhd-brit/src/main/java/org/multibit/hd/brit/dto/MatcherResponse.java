package org.multibit.hd.brit.dto;

/**
 *  <p>DTO to provide the following to BRIT:<br>
 *  <ul>
 *  <li>This is the response message from the Matcher to the Matcher</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class MatcherResponse {
  private final int version = 1;

  private final AddressGenerator addressGenerator;

  public MatcherResponse(AddressGenerator addressGenerator) {
    this.addressGenerator = addressGenerator;
  }

  public int getVersion() {
    return version;
  }

  public AddressGenerator getAddressGenerator() {
    return addressGenerator;
  }
}
