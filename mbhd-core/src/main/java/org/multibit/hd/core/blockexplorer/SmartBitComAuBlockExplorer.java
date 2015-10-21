package org.multibit.hd.core.blockexplorer;

import java.text.MessageFormat;

/**
 *  <p>smartbit.com.au BlockExplorer<br>
 *  </p>
 *  
 */
public class SmartBitComAuBlockExplorer implements BlockExplorer {
  public static final String ID = "smartbit";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "smartbit.com.au";
  }

  @Override
  public MessageFormat getTransactionLookupMessageFormat() {
    return new MessageFormat("https://www.smartbit.com.au/tx/{0}");
  }
}
