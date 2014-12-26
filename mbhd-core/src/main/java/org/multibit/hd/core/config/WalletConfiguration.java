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
   * The timestamp of the recent wallet id (note is not related to the current wallet root)
   */
  private String recentWalletTimestamp;

  /**
   * The WalletId in string form (e.g 11111111-22222222-33333333-44444444-55555555) of the wallet matching the recentWalletTimestamp
   */
  private String recentWalletId;

  /**
   * @return The current wallet root (e.g. "mbhd-11111111-22222222-33333333-44444444-55555555")
   */
  public String getCurrentWalletRoot() {
    return currentWalletRoot;
  }

  public void setCurrentWalletRoot(String currentWalletRoot) {
    this.currentWalletRoot = currentWalletRoot;
  }

  public String getRecentWalletTimestamp() {
    return recentWalletTimestamp;
  }

  public void setRecentWalletTimestamp(String recentWalletTimestamp) {
    this.recentWalletTimestamp = recentWalletTimestamp;
  }

  public String getRecentWalletId() {
    return recentWalletId;
  }

  public void setRecentWalletId(String recentWalletId) {
    this.recentWalletId = recentWalletId;
  }

  /**
   * @return A deep copy of this object
   */
  public WalletConfiguration deepCopy() {

    WalletConfiguration configuration = new WalletConfiguration();

    configuration.setCurrentWalletRoot(getCurrentWalletRoot());
    configuration.setRecentWalletId(getRecentWalletId());
    configuration.setRecentWalletTimestamp(getRecentWalletTimestamp());

    return configuration;
  }

}
