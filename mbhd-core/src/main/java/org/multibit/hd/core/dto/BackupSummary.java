package org.multibit.hd.core.dto;

import com.google.common.base.Preconditions;
import org.joda.time.DateTime;

import java.io.File;

/**
 * <p>DTO to provide the following to Backup API:</p>
 * <ul>
 * <li>Wallet backup summary information</li>
 * </ul>
 * <p>The backup summary allows the user to select wallet backups from a list.</p>
 *
 * @since 0.0.1
 *  
 */
public class BackupSummary {

  private final WalletId walletId;

  private final String name;

  private final File file;

  private DateTime created;

  /**
   * @param walletId The unique wallet identifier
   * @param name     The name given to the wallet
   * @param file     The backup filename to load
   */
  public BackupSummary(WalletId walletId, String name, File file) {

    Preconditions.checkNotNull(walletId, "'walletId' must be present");
    Preconditions.checkNotNull(name, "'name' must be present");
    Preconditions.checkNotNull(file, "'file' must be present");

    this.walletId = walletId;
    this.name = name;
    this.file = file;
  }

  /**
   * @return The unique identifier for this contact
   */
  public WalletId getWalletId() {
    return walletId;
  }

  /**
   * @return The name (shown to the user)
   */
  public String getName() {
    return name;
  }

  public File getFile() {
    return file;
  }

  /**
   * @return The date time the wallet was created
   */
  public DateTime getCreated() {
    return created;
  }

  public void setCreated(DateTime created) {
    this.created = created;
  }

  @Override
  public String toString() {
    return "BackupSummary{" +
      "walletId=" + walletId +
      ", name='" + name + '\'' +
      ", created=" + created +
      '}';
  }
}
