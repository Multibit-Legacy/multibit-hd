package org.multibit.hd.core.dto;

import com.google.bitcoin.core.Wallet;

/**
 * <p>Data object wrapping a Wallet and WalletId
 *  
 */
public class WalletData {

  private final Wallet wallet;

  private final WalletId walletId;

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

  @Override
  public String toString() {
    return "WalletData{" +
      "walletId=" + walletId +
      ", wallet=" + wallet +
      '}';
  }
}
