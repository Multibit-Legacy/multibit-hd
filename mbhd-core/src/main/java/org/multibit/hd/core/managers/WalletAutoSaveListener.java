package org.multibit.hd.core.managers;

import com.google.common.base.Optional;
import org.bitcoinj.wallet.WalletFiles;
import org.multibit.hd.core.crypto.EncryptedFileReaderWriter;
import org.multibit.hd.core.dto.WalletSummary;
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
    log.debug("Just about to save wallet to tempFile '{}'", tempFile.getAbsolutePath());
  }

  @Override
  public void onAfterAutoSave(File newlySavedFile) {

    log.debug("Have just saved wallet to newlySavedFile: '{}'", newlySavedFile.getAbsolutePath());

    Optional<WalletSummary> walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();

    if (walletSummary.isPresent()) {
      // Save an encrypted copy of the wallet
      CharSequence password = walletSummary.get().getPassword();
      File encryptedWalletFile = EncryptedFileReaderWriter.makeAESEncryptedCopyAndDeleteOriginal(newlySavedFile, password);
      if (encryptedWalletFile != null && encryptedWalletFile.exists()) {
        log.debug("Save encrypted copy of wallet as '{}'. Size was {} bytes.", encryptedWalletFile.getAbsolutePath(), encryptedWalletFile.length());
      } else {
        log.debug("No encrypted copy of wallet '{}' made.", newlySavedFile.getAbsolutePath());
      }

      // Remember the info required for the next backups
      BackupService backupService = CoreServices.getOrCreateBackupService();
      backupService.rememberWalletSummaryAndPasswordForRollingBackup(walletSummary.get(), password);
      backupService.rememberWalletIdAndPasswordForLocalZipBackup(walletSummary.get().getWalletId(), password);
      backupService.rememberWalletIdAndPasswordForCloudZipBackup(walletSummary.get().getWalletId(), password);

      log.debug("Have just saved wallet: " + walletSummary.get().getWallet());

    } else {
      log.error("No AES wallet encryption nor backups created as there was no wallet data to backup.");
    }
  }
}
