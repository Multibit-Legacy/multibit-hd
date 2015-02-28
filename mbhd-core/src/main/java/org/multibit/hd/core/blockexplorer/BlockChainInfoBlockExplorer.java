package org.multibit.hd.core.blockexplorer;

import java.text.MessageFormat;

/**
 *  <p>blockchain.info BlockExplorer<br>
 *  </p>
 *  
 */
public class BlockChainInfoBlockExplorer implements BlockExplorer {
  public static final String ID = "blockchain";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "blockchain.info";
  }

  @Override
  public MessageFormat getTransactionLookupMessageFormat() {
    return new MessageFormat("https://blockchain.info/tx-index/{0}");
  }
}
