package org.multibit.hd.core.dto;

import com.google.common.base.Preconditions;

/**
 *  <p>DTO to provide the following to WalletManager:<br>
 *  <ul>
 *  <li>Encapsulated wallet id and password</li>
 *  </ul>
 *  Example:<br>
 *  <pre>
 *  </pre>
 *  </p>
 *  
 */
public class WalletPassword {
  private final CharSequence password;

  private final WalletId walletId;

  public WalletPassword(CharSequence password, WalletId walletId) {
    Preconditions.checkNotNull(password, "Password cannot be null");
    Preconditions.checkNotNull(walletId, "WalletId cannot be null");

    this.password = password;
    this.walletId = walletId;
  }

  public CharSequence getPassword() {
    return password;
  }

  public WalletId getWalletId() {
    return walletId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WalletPassword that = (WalletPassword) o;

    if (password != null ? !password.equals(that.password) : that.password != null) return false;
    if (walletId != null ? !walletId.equals(that.walletId) : that.walletId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = password != null ? password.hashCode() : 0;
    result = 31 * result + (walletId != null ? walletId.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "WalletPassword{" +
            "password length=" + (password == null ? 0 : password.length()) +
            ", walletId=" + walletId +
            '}';
  }
}
