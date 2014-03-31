package org.multibit.hd.brit.dto;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.Strings;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 *  <p>DTO to provide the following to BRIT:</p>
 *  <ul>
 *  <li>This is the response message from the Matcher to the Matcher</li>
 *  </ul>
 *  
 */
public class MatcherResponse {
  private static final Logger log = LoggerFactory.getLogger(PayerRequest.class);

  private final Date replayDate;

  private final List<String> addressList;

  public MatcherResponse(Date replayDate, List<String> addressList) {
    this.replayDate = replayDate;
    this.addressList = addressList;
  }

  public int getVersion() {
    return 1;
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
    StringBuilder builder = new StringBuilder();
    builder.append(replayDate.getTime()).append(PayerRequest.SERIALISER_SEPARATOR);

    if (addressList != null) {
      for (String address : addressList) {
        builder.append(address).append(PayerRequest.SERIALISER_SEPARATOR);
      }
    }
    log.debug("Serialised matcherResponse = \n" + builder.toString());
    try {
      return builder.toString().getBytes("UTF8");
    } catch (UnsupportedEncodingException uee) {
      // Will not happen
      uee.printStackTrace();
      return null;
    }
  }

  /**
   * Parse a serialised MatcherResponse
   *
   * @param serialisedMatcherResponse te serialised MatcherResponse
   * @return a recreated MatcherResponse
   */
  public static MatcherResponse parse(byte[] serialisedMatcherResponse) throws UnsupportedEncodingException {
    String serialisedMatcherResponseAsString = new String(serialisedMatcherResponse, "UTF8");

    log.debug("Attempting to parse matcher response:\n" + serialisedMatcherResponseAsString);
    String[] rows = Strings.split(serialisedMatcherResponseAsString, '\n');
    if (rows.length > 0) {
      Date replayDate = new Date(Long.parseLong(rows[0]));
      List<String> bitcoinAddressList = Lists.newArrayList();
      if (rows.length > 1) {
        for (int i = 1; i < rows.length; i++) {
          if (rows[i] != null && rows[i].length() > 0) {
            bitcoinAddressList.add(rows[i]);
          }
        }
      }
      return new MatcherResponse(replayDate, bitcoinAddressList);

    } else {
      // Cannot parse
      log.error("Cannot parse"); // TODO throw exception
      return null;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MatcherResponse that = (MatcherResponse) o;

    if (addressList != null ? !addressList.equals(that.addressList) : that.addressList != null) return false;
    if (replayDate != null ? !replayDate.equals(that.replayDate) : that.replayDate != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = replayDate != null ? replayDate.hashCode() : 0;
    result = 31 * result + (addressList != null ? addressList.hashCode() : 0);
    return result;
  }
}
