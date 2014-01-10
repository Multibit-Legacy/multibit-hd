package org.multibit.hd.ui.views.components.confirm_password;

import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.wizards.WizardButton;

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

  private char[] password2;
  private char[] password1;

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "verification status" and "next" buttons
   */
  public ConfirmPasswordModel(String panelName) {
    this.panelName = panelName;
  }

  /**
   * <p>Compares the passwords and fires a password status event</p>
   */
  public void comparePasswords() {

    final boolean passwordsEqual;

    if (password1 == null || password2 == null) {
      passwordsEqual = false;
    } else if (password1.length == 0 || password2.length == 0) {
      passwordsEqual = false;
    } else if (password1.length != password2.length) {
      passwordsEqual = false;
    } else {

      // Time-constant comparison (overkill but useful exercise)
      int result = 0;
      for (int i = 0; i < password1.length; i++) {
        result |= password1[i] ^ password2[i];
      }

      // Check for a match
      passwordsEqual = (result == 0);
    }

    // Fire the UI events for "verification status" message and "next" button
    ViewEvents.fireVerificationStatusChangedEvent(panelName, passwordsEqual);
    ViewEvents.fireWizardButtonEnabledEvent(panelName, WizardButton.NEXT, passwordsEqual);
  }

  /**
   * @return The panel name that this component is associated with
   */
  public String getPanelName() {
    return panelName;
  }

  @Override
  public String getValue() {
    return String.valueOf(password1);
  }

  @Override
  public void setValue(String value) {
    this.password1 = value.toCharArray();
  }

  public void setPassword1(char[] password1) {
    this.password1 = password1;
  }

  public void setPassword2(char[] password2) {
    this.password2 = password2;
  }
}
