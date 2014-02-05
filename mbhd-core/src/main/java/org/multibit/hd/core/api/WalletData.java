package org.multibit.hd.core.api;

import com.google.bitcoin.core.Wallet;

/**
 *  <p>Data object wrapping a Wallet and WalletId
 *  
 */
public class WalletData {

  private Wallet wallet;

  private WalletId walletId;

  public Wallet getWallet() {
    return wallet;
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }

  public WalletId getWalletId() {
    return walletId;
  }

  public void setWalletId(WalletId walletId) {
    this.walletId = walletId;
  }

  public WalletData(WalletId walletId, Wallet wallet) {

    this.walletId = walletId;
    this.wallet = wallet;
  }

  @Override
  public String toString() {
    return "WalletData{" +
      "walletId=" + walletId +
      ", wallet=" + wallet +
      '}';
  }
}
