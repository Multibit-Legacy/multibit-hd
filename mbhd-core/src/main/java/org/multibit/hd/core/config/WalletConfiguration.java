package org.multibit.hd.core.config;

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
   * @return A deep copy of this object
   */
  public WalletConfiguration deepCopy() {

    WalletConfiguration configuration = new WalletConfiguration();

    configuration.setCurrentWalletRoot(getCurrentWalletRoot());

    return configuration;
  }

}
