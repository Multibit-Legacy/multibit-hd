package org.multibit.hd.core.managers;

import com.google.common.base.Optional;
import org.bitcoinj.wallet.WalletFiles;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.exceptions.WalletSaveException;
import org.multibit.hd.core.services.BackupService;
import org.multibit.hd.core.services.CoreServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * <p>Listener to provide the following to WalletManager:</p>
 * <ul>
 * <li>Saving of rolling wallet backups and zip backups</li>
 * </ul>
 * </p>
 */
public class WalletAutoSaveListener implements WalletFiles.Listener {

  private static final Logger log = LoggerFactory.getLogger(WalletAutoSaveListener.class);

  @Override
  public void onBeforeAutoSave(File tempFile) {
    log.debug("Auto-saving wallet to tempFile:\n'{}'", tempFile.getAbsolutePath());
  }

  @Override
  public void onAfterAutoSave(File newlySavedFile) {

    log.debug("Wallet auto-saved to newlySavedFile:\n'{}'", newlySavedFile.getAbsolutePath());

    Optional<WalletSummary> walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();

    if (walletSummary.isPresent()) {
      // Check the password is the correct password for this wallet
      // The walletSummary needs to be consistent and the wallet filename contains the formatted walletId
      WalletId walletId = walletSummary.get().getWalletId();
      if (!walletId.equals(walletSummary.get().getWalletPassword().getWalletId())
              || !newlySavedFile.getAbsolutePath().contains(walletId.toFormattedString()) ) {
        throw new WalletSaveException("The password specified is not the password for the wallet saved in '" + newlySavedFile.getAbsolutePath() + "'");
      }

      // Save an encrypted copy of the wallet
      CharSequence password = walletSummary.get().getWalletPassword().getPassword();
      File encryptedWalletFile = EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(newlySavedFile, password);
      if (encryptedWalletFile != null && encryptedWalletFile.exists()) {
        log.debug("Save encrypted copy of wallet (size: {} bytes) as:\n'{}'", encryptedWalletFile.length(), encryptedWalletFile.getAbsolutePath());
      } else {
        log.debug("No encrypted copy of wallet:\n'{}'", newlySavedFile.getAbsolutePath());
      }

      // Remember the info required for the next backups
      BackupService backupService = CoreServices.getOrCreateBackupService();
      backupService.rememberWalletSummaryAndPasswordForRollingBackup(walletSummary.get(), password);
      backupService.rememberWalletIdAndPasswordForLocalZipBackup(walletSummary.get().getWalletId(), password);
      backupService.rememberWalletIdAndPasswordForCloudZipBackup(walletSummary.get().getWalletId(), password);
    } else {
      log.error("No AES wallet encryption nor backups created as there was no wallet data to backup.");
    }
  }
}
