package org.multibit.hd.core.blockexplorer;

import java.text.MessageFormat;

/**
 *  <p>bitpay.com BlockExplorer<br>
 *  </p>
 *  
 */
public class BitPayComBlockExplorer implements BlockExplorer {
  public static final String ID = "bitpay";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "bitpay.com";
  }

  @Override
  public MessageFormat getTransactionLookupMessageFormat() {
    return new MessageFormat("https://insight.bitpay.com/tx/{0}");
  }
}
