package org.multibit.hd.brit.matcher;

import org.multibit.hd.brit.dto.MatcherResponse;
import org.multibit.hd.brit.dto.PayerRequest;

/**
 *  <p>Interface to provide the following to BRIT:<br>
 *  <ul>
 *  <li>encapsulation of functionality required to match BRIT payers and redeemers</li>
 *  </ul>
 *  </p>
 *  
 */
public interface Matcher {

  public MatcherConfig getConfig();

  public MatcherResponse process(PayerRequest payerRequest);
}
