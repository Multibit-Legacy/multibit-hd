package org.multibit.hd.ui.views.components.display_address;

import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Store the Bitcoin address</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplayBitcoinAddressModel implements Model<String> {

  private final String bitcoinAddress;

  /**
   * @param bitcoinAddress The Bitcoin address to display
   */
  public DisplayBitcoinAddressModel(String bitcoinAddress) {
    this.bitcoinAddress = bitcoinAddress;
  }

  @Override
  public String getValue() {
    return bitcoinAddress;
  }

  @Override
  public void setValue(String value) {
    // Do nothing the value is already set
  }

}
