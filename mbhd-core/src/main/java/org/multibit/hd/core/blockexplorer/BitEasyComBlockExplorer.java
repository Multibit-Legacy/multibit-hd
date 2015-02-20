package org.multibit.hd.core.blockexplorer;

import java.text.MessageFormat;

/**
 *  <p>biteasy.com BlockExplorer<br>
 *  </p>
 *  
 */
public class BitEasyComBlockExplorer implements BlockExplorer {
  public static final String ID = "biteasy";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "biteasy.com";
  }

  @Override
  public MessageFormat getTransactionLookupMessageFormat() {
    return new MessageFormat("https://www.biteasy.com/blockchain/transactions/{0}");
  }
}
