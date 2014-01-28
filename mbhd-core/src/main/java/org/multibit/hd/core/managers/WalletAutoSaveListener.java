package org.multibit.hd.core.managers;

import com.google.bitcoin.wallet.WalletFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *  <p>Listener to provide the following to WalletManager:<br>
 *  <ul>
 *  <li>Saving of rolling wallet backups</li>
 *  </ul>
 *  </p>
 *  
 */
public class WalletAutoSaveListener implements WalletFiles.Listener {
  private static final Logger log = LoggerFactory.getLogger(WalletManager.class);

  @Override
  public void onBeforeAutoSave(File tempFile) {
    log.debug("Just about to save wallet to tempFile '" + tempFile.getAbsolutePath() +"'");
  }

  @Override
  public void onAfterAutoSave(File newlySavedFile) {
    log.debug("Have just saved wallet to newlySavedFile '" + newlySavedFile.getAbsolutePath() + "'");
  }
}
