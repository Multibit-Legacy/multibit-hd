package org.multibit.hd.core.blockexplorer;

import java.text.MessageFormat;

/**
 *  <p>blockonomics.co BlockExplorer</p>
 */
public class BlockonomicsBlockExplorer implements BlockExplorer {
  public static final String ID = "blockonomics";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "blockonomics.co";
  }

  @Override
  public MessageFormat getTransactionLookupMessageFormat() {
    return new MessageFormat("https://www.blockonomics.co/api/tx?txid={0}");
  }
}
