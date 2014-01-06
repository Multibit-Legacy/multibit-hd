package org.multibit.hd.ui.views.components.confirm_password;

import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Show/hide the seed phrase (initially hidden)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ConfirmPasswordModel implements Model<String> {

  char[] password=null;

  /**
   * @param password1 Password 1
   * @param password2 Password 2
   *
   * @return True if the passwords are equal (compared in time-constant manner)
   */
  public boolean passwordsEqual(char[] password1, char[] password2) {

    if (password1.length != password2.length) {
      return false;
    }

    // Time-constant comparison
    int result = 0;
    for (int i = 0; i < password1.length; i++) {
      result |= password1[i] ^ password2[i];
    }

    // Check for a match
    if (result == 0) {

      password = password1;
      return true;
    }

    return false;
  }

  @Override
  public String getValue() {
    return String.valueOf(password);
  }

  @Override
  public void setValue(String value) {
    this.password = value.toCharArray();
  }

}
