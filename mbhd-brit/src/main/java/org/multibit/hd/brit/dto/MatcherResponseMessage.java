package org.multibit.hd.brit.dto;

/**
 *  <p>DTO to provide the following to BRIT:<br>
 *  <ul>
 *  <li>This is the response message from the Matcher to the Payer</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class MatcherResponseMessage implements Message {
  private final int version = 1;

  private final AddressGenerator addressGenerator;

  public MatcherResponseMessage(AddressGenerator addressGenerator) {
    this.addressGenerator = addressGenerator;
  }

  @Override
  public int getVersion() {
    return version;
  }

  public AddressGenerator getAddressGenerator() {
    return addressGenerator;
  }
}
