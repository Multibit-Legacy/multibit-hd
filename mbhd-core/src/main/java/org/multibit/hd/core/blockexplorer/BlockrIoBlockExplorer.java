package org.multibit.hd.core.blockexplorer;

import java.text.MessageFormat;

/**
 *  <p>blockr.io BlockExplorer<br>
 *  </p>
 *  
 */
public class BlockrIoBlockExplorer implements BlockExplorer {
  public static final String ID = "blockr";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "blockr.io";
  }

  @Override
  public MessageFormat getTransactionLookupMessageFormat() {
    return new MessageFormat("http://btc.blockr.io/tx/info/{0}");
  }
}
