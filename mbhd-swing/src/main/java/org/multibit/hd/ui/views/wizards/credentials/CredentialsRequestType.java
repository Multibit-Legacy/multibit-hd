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
  PASSWORD,
  TREZOR_PIN,
  NO_TREZOR_PIN
}
