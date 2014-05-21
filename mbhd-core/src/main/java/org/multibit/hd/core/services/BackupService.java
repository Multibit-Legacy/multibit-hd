package org.multibit.hd.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final int TICK_TIME_SECONDS = 30;   // TODO testing is set faster - want 120;
  /**
   * The slowdown rate for performing local zip backups
   */
  private static final int LOCAL_ZIP_BACKUP_MODULO = 2; // TODO testing is set faster - should be 5
  /**
   * The slowdown rate for performing local zip backups
   */

  private static final int CLOUD_ZIP_BACKUP_MODULO = 4; // TODO testing is set faster - should be 15
  private static final Logger log = LoggerFactory.getLogger(BackupService.class);
  /**
   * The rolling backup lock object
   */
  private static final Object rollingBackupLockObject = new Object();

  /**
   * The local zip backup lock object
   */
  private static final Object localZipBackupLockObject = new Object();

  /**
   * The cloud zip backup lock object
   */
  private static final Object cloudZipBackupLockObject = new Object();

  /**
   * The indicator for whether a rolling backup should be performed at the next scheduled time
   */
  private static Boolean doRollingBackup = Boolean.FALSE;

  /**
   * The indicator for whether a local zip backup should be performed at the next scheduled time
   */
  private static Boolean doLocalZipBackup = Boolean.FALSE;

  /**
   * The indicator for whether a cloud zip backup should be performed at the next scheduled time
   */
  private static Boolean doCloudZipBackup = Boolean.FALSE;

  /**
   * The number of times the backup main loop has incremented
   */
  private static int tickCount;

  public BackupService() {

  }

  /**
   * Schedule a rolling backup.
   * This can be called on a GUI threa
   */
  public static void scheduleRollingBackup() {
    synchronized (rollingBackupLockObject) {
      doRollingBackup = Boolean.TRUE;
    }
  }

  /**
   * Schedule a local zip backup.
   * This can be called on a GUI threa
   */
  public static void scheduleLocalZipBackup() {
    synchronized (localZipBackupLockObject) {
      doLocalZipBackup = Boolean.TRUE;
    }
  }

  /**
   * Schedule a cloud zip backup.
   * This can be called on a GUI thread
   */
  public static void scheduleCloudZipBackup() {
    synchronized (cloudZipBackupLockObject) {
      doCloudZipBackup = Boolean.TRUE;
    }
  }

  @Override
  public boolean start() {

    log.debug("Starting service");
    tickCount = 0;

    // Use the provided executor service management
    requireSingleThreadScheduledExecutor("backup");

    // Use the provided executor service management
    getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {
      public void run() {

        // Main backup loop
        log.debug("The tickCount is {}", tickCount);

        // Check to see if a rolling backup is required
        // This is done every tick
        boolean doRollingBackupNow = false;
        synchronized (rollingBackupLockObject) {
          if (doRollingBackup) {
            doRollingBackupNow = true;
            // Clear the scheduled flag
            doRollingBackup = Boolean.FALSE;
          }
        }

        if (doRollingBackupNow) {
          performRollingBackup();
        }

        // Check if a local zip backup is required
        // These are done every LOCAL_ZIP_BACKUP_MODULO number of ticks
        if (tickCount % LOCAL_ZIP_BACKUP_MODULO == 0) {
          boolean doLocalZipBackupNow = false;
          synchronized (localZipBackupLockObject) {
            if (doLocalZipBackup) {
              doLocalZipBackupNow = true;
              // Clear the scheduled flag
              doLocalZipBackup = Boolean.FALSE;
            }
          }

          if (doLocalZipBackupNow) {
            performLocalZipBackup();
          }
        }

        // Check if a cloud zip backup is required
        // These are done every CLOUD_ZIP_BACKUP_MODULO number of ticks
        if (tickCount % CLOUD_ZIP_BACKUP_MODULO == 0) {
          boolean doCloudZipBackupNow = false;
          synchronized (cloudZipBackupLockObject) {
            if (doCloudZipBackup) {
              doCloudZipBackupNow = true;
              // Clear the scheduled flag
              doCloudZipBackup = Boolean.FALSE;
            }
          }

          if (doCloudZipBackupNow) {
            performCloudZipBackup();
          }
        }


        tickCount++;
      }
    }
            , 0, TICK_TIME_SECONDS, TimeUnit.SECONDS);

    return true;

  }

  /**
   * Perform a rolling backup
   */
  private void performRollingBackup() {
    log.debug("Performing a rolling backup");
  }

  /**
   * Perform a local zip backup
   */
  private void performLocalZipBackup() {
    log.debug("Performing a local zip backup");
  }

  /**
   * Perform a cloud zip backup
   */
  private void performCloudZipBackup() {
    log.debug("Performing a cloud zip backup");
  }
}