package org.multibit.hd.ui.views.wizards.credentials;

/**
 * <p>Enum to provide the following to CredentialsWizard. The type of credential request to show to the user. Possible values are:<br>
 * <ul>
 * <li>User is to enter a password (MultiBit HD soft wallets)</li>
 * <li>User is to enter a PIN (Trezor wallet)</li>
 * <li>User does not need to enter a PIN (Trezor wallet)</li>
 * </ul>
 * </p>
 *
 */
public enum CredentialsRequestType {

  /**
   * Standard user entered password
   */
  PASSWORD,

  /**
   * Trezor cipher key method (may require a PIN as well)
   */
  TREZOR_CIPHER_KEY,
}
