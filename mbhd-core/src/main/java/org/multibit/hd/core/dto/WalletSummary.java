package org.multibit.hd.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.bitcoin.core.Wallet;
import com.google.common.base.Preconditions;

/**
 * <p>Value object to provide the following to application:</p>
 * <ul>
 * <li>Access to optional Bitcoinj Wallet and mandatory WalletId</li>
 * <li>Provision of a name and description for identifying a wallet</li>
 * </ul>
 *
 * <p>A wallet summary can be built from a wallet root and an appropriate YAML file</p>
 *
 * @since 0.0.1 
 */
public class WalletSummary {

  @JsonIgnore
  private Wallet wallet;

  @JsonIgnore
  private WalletId walletId;

  @JsonIgnore
  private CharSequence password;

  private String name;

  private String description;

  /**
   * Default constructor for Jackson
   */
  public WalletSummary() {
  }

  /**
   * <p>Standard constructor</p>
   *
   * @param walletId The wallet ID
   * @param wallet   The Bitcoinj wallet
   */
  public WalletSummary(WalletId walletId, Wallet wallet) {

    Preconditions.checkNotNull(walletId, "'walletId' must be present");
    Preconditions.checkNotNull(wallet, "'wallet' must be present");

    this.wallet = wallet;
    this.walletId = walletId;
  }

  /**
   * @return The Bitcoinj wallet associated with this summary (can be null)
   */
  public Wallet getWallet() {
    return wallet;
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }

  /**
   * @return The wallet ID
   */
  public WalletId getWalletId() {
    return walletId;
  }

  public void setWalletId(WalletId walletId) {
    this.walletId = walletId;
  }

  /**
   * @return The wallet password
   */
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
    return "WalletSummary{" +
      "wallet=" + wallet +
      ", walletId=" + walletId +
      ", name='" + name + '\'' +
      ", description='" + description + '\'' +
      ", password=****" +
      '}';
  }
}
