package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>Service to provide the following to application API:</p>
 * <ul>
 * <li>Access to rolling, local zip and cloud zip backups</li>
 * </ul>
 * <p/>
 * Wallet backup strategy:
 * <p/>
 * <p/>
 * Bitcoinj wallet writes
 * <p/>
 * this is a two stage write
 * wallet.saveNow is done instantly
 * wallet.saveLater - set to a 30 seconds period
 * make a save at MBHD exit
 * <p/>
 * <p/>
 * Rolling backups
 * <p/>
 * make saves every 2 minutes
 * make first save 2 minutes after MBHD start (most likely after initial sync)
 * make a save at MBHD exit
 * <p/>
 * <p/>
 * Local zip backups
 * <p/>
 * make saves every 10 minutes
 * make first save 2 minutes after MBHD start
 * make a save at MBHD exit
 * <p/>
 * <p/>
 * Cloud backups
 * <p/>
 * make saves every 30 minutes
 * make first save 2 minutes after MBHD start
 * make a save at MBHD exit
 * <p/>
 * <p/>
 * Do not bother tracking if the wallet is dirty, this only really affects the rolling backups and
 * is not worth the bother of writing a wallet extension to track it.
 *
 * @since 0.0.1
 *  
 */
public class BackupService extends AbstractService {

  /**
   * This is the fastest tick used for backups.
   * Everything else is done on a multiple of this
   */
  private static final int TICK_TIME_SECONDS = 120;
  /**
   * The slowdown rate for performing local zip backups
   */
  private static final int LOCAL_ZIP_BACKUP_MODULO = 5;
  /**
   * The slowdown rate for performing local zip backups
   */

  private static final int CLOUD_ZIP_BACKUP_MODULO = 15;

  private static final Logger log = LoggerFactory.getLogger(BackupService.class);

  /**
   * The number of times the backup main loop has incremented
   */
  private static int tickCount;

  /**
   * The wallet summary to use for the next rolling backup
   */
  private Optional<WalletSummary> rememberedWalletSummaryForRollingBackup = Optional.absent();

  /**
   * The password to use for the next rolling backup
   */
  private Optional<CharSequence> rememberedPasswordForRollingBackup = Optional.absent();

  /**
   * The wallet id to use for the next local zip backup
   */
  private Optional<WalletId> rememberedWalletIdForLocalBackup = Optional.absent();

  /**
   * The password to use for the next local zip backup
   */
  private Optional<CharSequence> rememberedPasswordForLocalBackup = Optional.absent();

  /**
   * The wallet id to use for the next cloud zip backup
   */
  private Optional<WalletId> rememberedWalletIdForCloudBackup = Optional.absent();

  /**
   * The password to use for the next cloud zip backup
   */
  private Optional<CharSequence> rememberedPasswordForCloudBackup = Optional.absent();

  /**
   * Whether backups are enabled or not
   */
  private boolean backupsAreEnabled = true;

  /**
   * Whether backups are currently being performed
   */
  private boolean backupsAreRunning = false;


  public BackupService() {

  }

  @Override
  public boolean start() {

    log.debug("Starting service");

    // The first tick (at time TICK_TIME_SECONDS seconds) all of a rolling backup,
    // local backup and a cloud backup
    // The users copy of MBHD will most likely be fully synchronised by then
    tickCount = 0;

    // Use the provided executor service management
    requireSingleThreadScheduledExecutor("backup");

    // Use the provided executor service management
    getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {
      public void run() {
        backupsAreRunning = true;
        try {
          // Main backup loop
          //log.debug("The tickCount is {}", tickCount);

          // A rolling backup is performed every tick
          if (backupsAreEnabled) {
            performRollingBackup();
          }

          // Local zip backups are done every LOCAL_ZIP_BACKUP_MODULO number of ticks
          if (backupsAreEnabled && tickCount % LOCAL_ZIP_BACKUP_MODULO == 0) {
            performLocalZipBackup();
          }

          // Check if a cloud zip backup is required
          // Cloud backups are done every CLOUD_ZIP_BACKUP_MODULO number of ticks
          if (backupsAreEnabled && tickCount % CLOUD_ZIP_BACKUP_MODULO == 0) {
            performCloudZipBackup();
          }

        } finally {
          tickCount++;
          backupsAreRunning = false;
        }
      }
    }
            , TICK_TIME_SECONDS, TICK_TIME_SECONDS, TimeUnit.SECONDS);

    return true;
  }


  /**
   * Remember a wallet summary and password.
   * This will be used at the next rolling backup.
   */
  public void rememberWalletSummaryAndPasswordForRollingBackup(WalletSummary walletSummary, CharSequence password) {
    rememberedWalletSummaryForRollingBackup = Optional.of(walletSummary);
    rememberedPasswordForRollingBackup = Optional.of(password);
  }

  /**
   * Perform a rolling backup using the last remembered wallet summary and password
   */
  private void performRollingBackup() {
    if (rememberedWalletSummaryForRollingBackup.isPresent() && rememberedPasswordForRollingBackup.isPresent()) {
      log.debug("Performing a rolling backup");

      try {
        BackupManager.INSTANCE.createRollingBackup(rememberedWalletSummaryForRollingBackup.get(), rememberedPasswordForRollingBackup.get());

        // Don't use anything remembered in the past at this point again
        // (This will miss anything newly remembered whilst the backup is taking place
        rememberedWalletSummaryForRollingBackup = Optional.absent();
        rememberedPasswordForRollingBackup = Optional.absent();
      } catch (IOException ioe) {
        ioe.printStackTrace();
        // TODO handle exception (which is thrown inside the main runnable)
      }
    }
  }

  /**
   * Remember a wallet id and password.
   * This will be used at the next local zip backup.
   */
  public void rememberWalletIdAndPasswordForLocalZipBackup(WalletId walletId, CharSequence password) {
    rememberedWalletIdForLocalBackup = Optional.of(walletId);
    rememberedPasswordForLocalBackup = Optional.of(password);
  }

  /**
   * Perform a local zip backup
   */
  private void performLocalZipBackup() {
    if (rememberedWalletIdForLocalBackup.isPresent() && rememberedPasswordForLocalBackup.isPresent()) {
      log.debug("Performing a local zip backup");

      try {
        BackupManager.INSTANCE.createLocalBackup(rememberedWalletIdForLocalBackup.get(), rememberedPasswordForLocalBackup.get());

        // Don't use anything remembered in the past at this point again
        // (This will miss anything newly remembered whilst the backup is taking place
        rememberedWalletIdForLocalBackup = Optional.absent();
        rememberedPasswordForLocalBackup = Optional.absent();
      } catch (IOException ioe) {
        ioe.printStackTrace();
        // TODO handle exception (which is thrown inside the main runnable)
      }
    }
  }

  /**
   * Remember a wallet id and password.
   * This will be used at the next cloud zip backup.
   */
  public void rememberWalletIdAndPasswordForCloudZipBackup(WalletId walletId, CharSequence password) {
    rememberedWalletIdForCloudBackup = Optional.of(walletId);
    rememberedPasswordForCloudBackup = Optional.of(password);
  }

  /**
   * Perform a cloud zip backup
   */
  private void performCloudZipBackup() {
    if (rememberedWalletIdForCloudBackup.isPresent() && rememberedPasswordForCloudBackup.isPresent()) {
      log.debug("Performing a cloud zip backup");

      try {
        BackupManager.INSTANCE.createCloudBackup(rememberedWalletIdForCloudBackup.get(), rememberedPasswordForCloudBackup.get());

        // Don't use anything remembered in the past at this point again
        // (This will miss anything newly remembered whilst the backup is taking place
        rememberedWalletIdForCloudBackup = Optional.absent();
        rememberedPasswordForCloudBackup = Optional.absent();
      } catch (IOException ioe) {
        ioe.printStackTrace();
        // TODO handle exception (which is thrown inside the main runnable)
      }
    }
  }

  /**
   * On shutdown disable any more backups, wait until any current backup is finished and then perform
   * a rolling, local and cloud backup
   * @param shutdownEvent Shutdown event
   */
  @Subscribe
  public void onShutdownEvent(ShutdownEvent shutdownEvent) {

    // A hard shutdown does not give enough time to wait gracefully
    // A soft shutdown occurs during FEST testing so the backups may not be running
    if (shutdownEvent.getShutdownType() == ShutdownEvent.ShutdownType.SOFT && isBackupsAreRunning()) {
      log.debug("Performing backups at shutdown");

      // Disable any new backups
      this.setBackupsAreEnabled(false);

      getScheduledExecutorService().schedule(new Runnable() {
        public void run() {
          // Wait for any current backups to complete
          while (isBackupsAreRunning()) {
            Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
          }

          performRollingBackup();

          performLocalZipBackup();

          performCloudZipBackup();

        }

      }, 0, TimeUnit.MILLISECONDS);
    }

  }

  /**
   * Set whether backups are enabled.
   * Can be called on any thread.
   * Only affects backups starting subsequently
   * @param backupsAreEnabled whether backups should be performed (true) or not (false)
   */
  public void setBackupsAreEnabled(boolean backupsAreEnabled) {
    this.backupsAreEnabled = backupsAreEnabled;
  }

  /**
   * Indicates whether backups are currently running in the main scheduled loop
   * @return true if a backup is running in the main scheduled loop
   */
  public boolean isBackupsAreRunning() {
    return backupsAreRunning;
  }
}