package org.multibit.hd.core.events;

import com.google.common.base.Optional;
import org.multibit.hd.core.dto.WalletId;

import javax.annotation.Nullable;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification that a wallet has been loaded (or failed to load)</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class WalletLoadEvent implements CoreEvent {

  /**
   * The walletId of the wallet backup loaded
   */
  private final Optional<WalletId> walletId;

  /**
   * Whether the wallet was loaded successfully
   */
  private final boolean walletLoadWasSuccessful;

  /**
   * The core message key
   */
  private final String walletLoadMessageKey;

  /**
   * The raw load error if applicable
   */
  private final Optional<Throwable> error;



  public WalletLoadEvent(Optional<WalletId> walletId, boolean walletLoadWasSuccessful, String walletLoadMessageKey, @Nullable Throwable error) {
    this.walletId = walletId;
    this.walletLoadWasSuccessful = walletLoadWasSuccessful;
    this.walletLoadMessageKey = walletLoadMessageKey;
    this.error = Optional.fromNullable(error);

  }

  public Optional<WalletId> getWalletId() {
    return walletId;
  }

  public boolean isWalletLoadWasSuccessful() {
    return walletLoadWasSuccessful;
  }

  public String getWalletLoadMessageKey() {
    return walletLoadMessageKey;
  }

  public Optional<Throwable> getError() {
    return error;
  }

  @Override
  public String toString() {
    return "WalletLoadEvent{" +
            "walletId=" + walletId +
            ", walletLoadWasSuccessful=" + walletLoadWasSuccessful +
            ", walletLoadMessageKey='" + walletLoadMessageKey + '\'' +
            ", error=" + error +
            '}';
  }
}
