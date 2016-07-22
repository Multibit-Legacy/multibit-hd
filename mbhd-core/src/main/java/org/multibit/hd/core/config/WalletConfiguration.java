package org.multibit.hd.core.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.collect.Maps;
import org.multibit.hd.core.services.FeeService;


import java.util.Map;

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

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Any unknown objects in the configuration go here (preserve order of insertion)
   */
  private Map<String, Object> other = Maps.newLinkedHashMap();

  /**
   * @return The map of any unknown objects in the configuration at this level
   */
  @JsonAnyGetter
  public Map<String, Object> any() {
    return other;
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    other.put(name, value);
  }

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

    // Unknown properties
    for (Map.Entry<String, Object> entry : any().entrySet()) {
      configuration.set(entry.getKey(), entry.getValue());
    }

    // Known properties
    configuration.setLastSoftWalletRoot(getLastSoftWalletRoot());
    configuration.setRecentWalletDataValidity(getRecentWalletDataValidity());
    configuration.setRecentWalletLabel(getRecentWalletLabel());
    configuration.setFeePerKB(getFeePerKB());

    return configuration;
  }
}
