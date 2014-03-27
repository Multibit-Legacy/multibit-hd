package org.multibit.hd.brit.matcher;

import org.multibit.hd.brit.dto.MatcherResponse;
import org.multibit.hd.brit.dto.PayerRequest;

/**
 *  <p>Class to provide the following to BRIT:<br>
 *  <ul>
 *  <li>ability to match redeemers and payers</li>
 *  </ul>

 *  </p>
 *  
 */
public class BasicMatcher implements Matcher {

  private MatcherConfig matcherConfig;

  public BasicMatcher(MatcherConfig matcherConfig) {
    this.matcherConfig = matcherConfig;
  }

  @Override
  public MatcherConfig getConfig() {
    return matcherConfig;
  }

  @Override
  public MatcherResponse process(PayerRequest payerRequest) {
    return null;
  }
}
