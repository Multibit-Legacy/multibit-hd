package org.multibit.hd.ui.views.wizards.welcome;

/**
 * <p>Enum to provide the following to welcome wizard model:</p>
 * <ul>
 * <li>Mode selection (standard, Trezor etc)</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public enum WelcomeWizardMode {

  /**
   * Target a standard soft wallet (BIP 32 or BIP 44)
   */
  STANDARD,

  /**
   * Target a Trezor wallet (BIP 44 only)
   */
  TREZOR,

  // End of enum
  ;

}
