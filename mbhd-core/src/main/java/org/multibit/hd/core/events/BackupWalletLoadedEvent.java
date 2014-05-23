package org.multibit.hd.core.events;

import org.multibit.hd.core.dto.WalletId;

import java.io.File;

/**
 * <p>Event to provide the following to application API:</p>
 * <ul>
 * <li>Notification that a backup wallet has been loaded</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class BackupWalletLoadedEvent implements CoreEvent {

  /**
   * The walletId of the wallet backup loaded
   */
  private final WalletId walletId;

  /**
   * The backup file that was loaded
   */
  private final File backupLoaded;

  public BackupWalletLoadedEvent(WalletId walletId, File backupLoaded) {
    this.walletId = walletId;
    this.backupLoaded = backupLoaded;
  }

  public WalletId getWalletId() {
    return walletId;
  }

  public File getBackupLoaded() {
    return backupLoaded;
  }
}
