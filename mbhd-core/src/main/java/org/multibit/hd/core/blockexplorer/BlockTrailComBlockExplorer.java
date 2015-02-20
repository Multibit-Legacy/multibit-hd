package org.multibit.hd.core.blockexplorer;

import java.text.MessageFormat;

/**
 *  <p>blocktrail.com BlockExplorer<br>
 *  </p>
 *  
 */
public class BlockTrailComBlockExplorer implements BlockExplorer {
  public static final String ID = "blocktrail";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "blocktrail.com";
  }

  @Override
  public MessageFormat getTransactionLookupMessageFormat() {
    return new MessageFormat("https://www.blocktrail.com/BTC/tx/{0}");
  }
}
