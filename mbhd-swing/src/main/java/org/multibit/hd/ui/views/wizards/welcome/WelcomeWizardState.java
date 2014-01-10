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

  WELCOME,
  CREATE_WALLET_SEED_PHRASE,
  CONFIRM_WALLET_SEED_PHRASE,
  RESTORE_WALLET,
  HARDWARE_WALLET,
  SWITCH_WALLET,
  SELECT_WALLET,
  CREATE_WALLET_PASSWORD,
  SELECT_BACKUP_LOCATION,
  FINISH,

  // End of enum
  ;

}
