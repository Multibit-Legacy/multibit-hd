package org.multibit.hd.core.events;

import com.google.common.base.Optional;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.WalletId;

import javax.annotation.Nullable;
import java.io.File;

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
  private final CoreMessageKey walletLoadMessageKey;

  /**
   * The raw load error if applicable
   */
  private final Optional<Throwable> error;

  /**
   * The backup file that was loaded, or absent if not applicable
   */
  private final Optional<File> backupLoaded;



  public WalletLoadEvent(Optional<WalletId> walletId, boolean walletLoadWasSuccessful, CoreMessageKey walletLoadMessageKey, @Nullable Throwable error, Optional<File> backupLoaded) {
    this.walletId = walletId;
    this.walletLoadWasSuccessful = walletLoadWasSuccessful;
    this.walletLoadMessageKey = walletLoadMessageKey;
    this.error = Optional.fromNullable(error);
    this.backupLoaded = backupLoaded;

  }

  public Optional<WalletId> getWalletId() {
    return walletId;
  }

  public boolean isWalletLoadWasSuccessful() {
    return walletLoadWasSuccessful;
  }

  public CoreMessageKey getWalletLoadMessageKey() {
    return walletLoadMessageKey;
  }

  public Optional<Throwable> getError() {
    return error;
  }

  public Optional<File> getBackupLoaded() {
    return backupLoaded;
  }

  @Override
  public String toString() {
    return "WalletLoadEvent{" +
            "walletId=" + walletId +
            ", walletLoadWasSuccessful=" + walletLoadWasSuccessful +
            ", walletLoadMessageKey='" + walletLoadMessageKey + '\'' +
            ", error=" + error +
            ", backupLoaded=" + backupLoaded +
            '}';
  }
}
