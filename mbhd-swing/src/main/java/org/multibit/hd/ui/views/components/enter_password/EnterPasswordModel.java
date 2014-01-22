package org.multibit.hd.ui.views.components.enter_password;

import com.google.common.base.Optional;
import org.multibit.hd.ui.events.view.ViewEvents;
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
public class EnterPasswordModel implements Model<String> {

  private char[] password;

  private final String panelName;

  /**
   * @param panelName The panel name to identify the "next" buttons
   */
  public EnterPasswordModel(String panelName) {
    this.panelName = panelName;
  }

  /**
   * @return The panel name that this component is associated with
   */
  public String getPanelName() {
    return panelName;
  }

  @Override
  public String getValue() {
    return String.valueOf(password);
  }

  @Override
  public void setValue(String value) {
    this.password = value.toCharArray();
  }

  /**
   * @param password The current value of the password and fire a component changed event
   */
  public void setPassword(char[] password) {
    this.password = password;

    // Alert the panel model that a component has changed
    ViewEvents.fireWizardComponentModelChangedEvent(panelName, Optional.of(this));

  }
}
