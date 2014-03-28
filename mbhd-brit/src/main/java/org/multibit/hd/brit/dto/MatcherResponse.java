package org.multibit.hd.brit.dto;

import java.util.Date;
import java.util.List;

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

  private final Date replayDate;

  private final List<String> addressList;

  public MatcherResponse(Date replayDate, List<String> addressList) {
    this.replayDate = replayDate;
    this.addressList = addressList;
  }

  public int getVersion() {
    return version;
  }

  public List<String> getAddressList() {
    return addressList;
  }

  public Date getReplayDate() {
    return replayDate;
  }

  /**
   * Serialise a MatcherResponse
   */
  public byte[] serialise() {
    return null;
  }

  /**
   * Parse a serialised MatcherResponse
   * @param serialisedMatcherResponse te serialised MatcherResponse
   * @return a recreated MatcherResponse
   */
  public static MatcherResponse parse(byte[] serialisedMatcherResponse) {
    return null;
  }
}
