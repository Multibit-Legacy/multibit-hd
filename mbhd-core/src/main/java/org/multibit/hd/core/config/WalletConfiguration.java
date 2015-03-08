package org.multibit.hd.core.config;

import org.multibit.hd.brit.services.FeeService;

/**
 * <p>Configuration to provide the following to application:</p>
 * <ul>
 * <li>Configuration of current and available wallets</li>
 * <li>Fee per KB to be used in spends</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class WalletConfiguration {

  private String lastSoftWalletRoot;

  /**
   * The time at which the recent wallet data was stored/ valid (milliseconds since epoch)
   */
  private long recentWalletDataValidity;

  /**
   * The label of the recent Trezor wallet
   */
  private String recentWalletLabel;

  /**
   * The fee per kilobyte of transaction to use in spends
   * Note feePerKB is a long rather than a Coin as Coin does not round trip as is via JSON
   */
  private long feePerKB = FeeService.DEFAULT_FEE_PER_KB.longValue();

  /**
   * @return The last/current soft wallet root (e.g. "mbhd-11111111-22222222-33333333-44444444-55555555")
   * (This is only updated for soft wallets so it will not give you the a plugged in Trezor's wallet root)
   */
  public String getLastSoftWalletRoot() {
    return lastSoftWalletRoot;
  }

  /**
   * The current soft wallet root
   * @param lastSoftWalletRoot the last soft wallet root
   */
  public void setLastSoftWalletRoot(String lastSoftWalletRoot) {
    this.lastSoftWalletRoot = lastSoftWalletRoot;
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

  public long getFeePerKB() {
    return feePerKB;
  }

  public void setFeePerKB(long feePerKB) {
    this.feePerKB = feePerKB;
  }

  /**
   * @return A deep copy of this object
   */
  public WalletConfiguration deepCopy() {

    WalletConfiguration configuration = new WalletConfiguration();

    configuration.setLastSoftWalletRoot(getLastSoftWalletRoot());
    configuration.setRecentWalletDataValidity(getRecentWalletDataValidity());
    configuration.setRecentWalletLabel(getRecentWalletLabel());
    configuration.setFeePerKB(getFeePerKB());

    return configuration;
  }
}
