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
   * The initial welcome and licence page
   */
  WELCOME_LICENCE,
  /**
   * The language selection page
   */
  WELCOME_SELECT_LANGUAGE,
  /**
   * Select how the wallet will be referenced (created, restored, hardware, switch etc)
   */
  WELCOME_SELECT_WALLET,
  /**
   * Create wallet: Preparation instructions
   */
  CREATE_WALLET_PREPARATION,
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
   * Create wallet: Create credentials
   */
  CREATE_WALLET_CREATE_PASSWORD,
  /**
   * Create wallet: Report on the outcome
   */
  CREATE_WALLET_REPORT,
  /**
   * Trezor Create wallet: Preparation instructions
   */
  TREZOR_CREATE_WALLET_PREPARATION,
  /**
   * Trezor Create wallet: Select backup location
   */
  TREZOR_CREATE_WALLET_SELECT_BACKUP_LOCATION,
  /**
   * Trezor Create wallet: Request secure create wallet (wipe)
   */
  TREZOR_CREATE_WALLET_REQUEST_CREATE_WALLET,
  /**
   * Trezor Create wallet: Display confirm wipe
   */
  TREZOR_CREATE_WALLET_CONFIRM_CREATE_WALLET,
  /**
   * Trezor Create wallet: Confirm supplied entropy
   */
  TREZOR_CREATE_WALLET_CONFIRM_ENTROPY,
  /**
   * Trezor Create wallet: Enter new PIN
   */
  TREZOR_CREATE_WALLET_ENTER_NEW_PIN,
  /**
   * Trezor Create wallet: Confirm new PIN
   */
  TREZOR_CREATE_WALLET_CONFIRM_NEW_PIN,
  /**
   * Trezor Create wallet: Confirm next word from Trezor (seed phrase)
   */
  TREZOR_CREATE_WALLET_CONFIRM_WORD,
  /**
   * Trezor Create wallet: Report on the outcome
   */
  TREZOR_CREATE_WALLET_REPORT,
  /**
   * Restore credentials: Enter seed phrase to begin the process
   */
  RESTORE_PASSWORD_SEED_PHRASE,
  /**
   * Restore credentials: Report on the restoration outcome
   */
  RESTORE_PASSWORD_REPORT,
  /**
   * Restore wallet: Enter seed phrase to begin the process
   */
  RESTORE_WALLET_SEED_PHRASE,
  /**
   * Restore wallet: Select the backup location (if available)
   */
  RESTORE_WALLET_SELECT_BACKUP_LOCATION,
  /**
   * Restore wallet: Select the backup (if multiple found)
   */
  RESTORE_WALLET_SELECT_BACKUP,
  /**
   * Restore wallet: Enter timestamp for seed restoration
   */
  RESTORE_WALLET_TIMESTAMP,
  /**
   * Restore wallet: Report on the restoration outcome
   */
  RESTORE_WALLET_REPORT,
  /**
   * Select wallet: Select a hardware wallet (Trezor etc)
   */
  SELECT_WALLET_HARDWARE,
  /**
   * Select wallet: Select a the unlock screen (bypass create/restore)
   */
  SELECT_EXISTING_WALLET,

  // End of enum
  ;

}
