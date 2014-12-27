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
   * The time at which the recent wallet data was stored/ valid (milliseconds since epoch)
   */
  private long recentWalletDataValidity;

  /**
   * The label of the recent Trezor wallet
   */
  private String recentWalletLabel;


  /**
   * @return The current wallet root (e.g. "mbhd-11111111-22222222-33333333-44444444-55555555")
   */
  public String getCurrentWalletRoot() {
    return currentWalletRoot;
  }

  /**
   * The current soft wallet root
   * @param currentWalletRoot
   */
  public void setCurrentWalletRoot(String currentWalletRoot) {
    this.currentWalletRoot = currentWalletRoot;
  }

  public long getRecentWalletDataValidity() {
    return recentWalletDataValidity;
  }

  public void setRecentWalletDataValidity(long recentWalletDataValidity) {
    this.recentWalletDataValidity = recentWalletDataValidity;
  }

  public String getRecentWalletLabel() {
    return recentWalletLabel;
  }

  public void setRecentWalletLabel(String recentWalletLabel) {
    this.recentWalletLabel = recentWalletLabel;
  }

  /**
   * @return A deep copy of this object
   */
  public WalletConfiguration deepCopy() {

    WalletConfiguration configuration = new WalletConfiguration();

    configuration.setCurrentWalletRoot(getCurrentWalletRoot());
    configuration.setRecentWalletDataValidity(getRecentWalletDataValidity());
    configuration.setRecentWalletLabel(getRecentWalletLabel());

    return configuration;
  }

}
