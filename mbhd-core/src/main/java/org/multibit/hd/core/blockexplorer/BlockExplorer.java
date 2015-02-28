package org.multibit.hd.core.blockexplorer;

import java.text.MessageFormat;

/**
 *  <p>Interface to provide the following to BlockExplorers:<br>
 *  <ul>
 *  <li>Information about an individual web based block explorer </li>
 *  </ul>
 *  </p>
 *  
 */
public interface BlockExplorer {
  /**
   * Get the identifier for this block explorer (for persisting in configuration data)
   */
  public String getId();

  /**
   * Get the name of the block explorer (to show to the end user e.g. blockchain.info)
   */
  public String getName();

  /**
   * The message format of the URI to use to lookup an individual transaction
   * e.g. "https://blockchain.info/tx-index/{0}" for blockchain.info
   *
   * It should have one argument to set, the transaction id, with something like:
   * form.setFormatByArgumentIndex(0, txide);
   */
  public MessageFormat getTransactionLookupMessageFormat();
}
