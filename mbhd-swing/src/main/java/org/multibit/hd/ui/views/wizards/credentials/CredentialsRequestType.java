package org.multibit.hd.ui.views.wizards.credentials;

/**
 * <p>Enum to provide the following to CredentialsWizard. The type of credential request to show to the user. Possible values are:<br>
 * <ul>
 * <li>User is to enter a password (MultiBit HD soft wallets)</li>
 * <li>User is to authenticate using a Trezor (most likely with a PINentry)</li>
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
   * Trezor authentication
   */
  TREZOR,
}
