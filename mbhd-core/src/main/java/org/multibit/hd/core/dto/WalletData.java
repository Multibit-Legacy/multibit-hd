package org.multibit.hd.core.dto;

import com.google.bitcoin.core.Wallet;

/**
 * <p>Data object wrapping a Wallet and WalletId</p>
 *
 * @since 0.0.1 
 */
public class WalletData {

  private final Wallet wallet;

  private final WalletId walletId;

  private String name;

  private String description;

  private CharSequence password;

  public WalletData(WalletId walletId, Wallet wallet) {

    this.walletId = walletId;
    this.wallet = wallet;
  }

  public Wallet getWallet() {
    return wallet;
  }

  public WalletId getWalletId() {
    return walletId;
  }

  public CharSequence getPassword() {
    return password;
  }

  public void setPassword(CharSequence password) {
    this.password = password;
  }

  /**
   * @return The wallet name (e.g. "ACME Ltd")
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The longer description (e.g. "The ACME Ltd business wallet")
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "WalletData{" +
      "wallet=" + wallet +
      ", walletId=" + walletId +
      ", name='" + name + '\'' +
      ", description='" + description + '\'' +
      ", password=****" +
      '}';
  }
}
