package org.multibit.hd.core.config;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * <p>Configuration to provide the following to application:</p>
 * <ul>
 * <li>Configuration of current and available wallets</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WalletConfiguration {

  private String currentWalletRoot;
  private Map<String, String> wallets = Maps.newHashMap();

  /**
   * @return The current wallet root (e.g. "mbhd-11111111-22222222-33333333-44444444-55555555")
   */
  public String getCurrentWalletRoot() {
    return currentWalletRoot;
  }

  public void setCurrentWalletRoot(String currentWalletRoot) {
    this.currentWalletRoot = currentWalletRoot;
  }

  /**
   * @return A map of all wallets keyed on the wallet ID
   */
  public Map<String, String> getWallets() {
    return wallets;
  }

  public void setWallets(Map<String, String> wallets) {
    this.wallets = wallets;
  }

  /**
   * @return A deep copy of this object
   */
  public WalletConfiguration deepCopy() {

    WalletConfiguration configuration = new WalletConfiguration();

    configuration.setWallets(getWallets());
    configuration.setCurrentWalletRoot(getCurrentWalletRoot());

    return configuration;
  }

}
