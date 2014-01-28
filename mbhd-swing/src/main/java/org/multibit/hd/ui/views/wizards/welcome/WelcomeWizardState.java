package org.multibit.hd.ui.views.wizards.welcome;

/**
 * <p>Enum to provide the following to welcome wizard model:</p>
 * <ul>
 * <li>State identification</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum WelcomeWizardState {

  /**
   * The initial welcome and language selection page
   */
  WELCOME_SELECT_LANGUAGE,
  /**
   * Select how the wallet will be referenced (created, restored, hardware, switch etc)
   */
  WELCOME_SELECT_WALLET,
  /**
   * Create wallet: Select backup location
   */
  CREATE_WALLET_SELECT_BACKUP_LOCATION,
  /**
   * Create wallet: Display seed phrase and timestamp
   */
  CREATE_WALLET_SEED_PHRASE,
  /**
   * Create wallet: Confirm seed phrase and timestamp
   */
  CREATE_WALLET_CONFIRM_SEED_PHRASE,
  /**
   * Create wallet: Create password
   */
  CREATE_WALLET_CREATE_PASSWORD,
  /**
   * Create wallet: Report on the outcome
   */
  CREATE_WALLET_REPORT,
  /**
   * Restore wallet: Select restore method (from seed phrase or cloud)
   */
  RESTORE_WALLET_SELECT_METHOD,
  /**
   * Restore wallet: Restore from seed phrase and timestamp
   */
  RESTORE_WALLET_SEED_PHRASE,
  /**
   * Restore wallet: Restore from encrypted backup
   */
  RESTORE_WALLET_BACKUP,
  /**
   * Restore wallet: Report on the restoration outcome
   */
  RESTORE_WALLET_REPORT,
  /**
   * Select wallet: Select a hardware wallet (Trezor etc)
   */
  SELECT_WALLET_HARDWARE,
  /**
   * Select wallet: Switch to another wallet (from application directory)
   */
  SELECT_WALLET_SWITCH,

  // End of enum
  ;

}
