package org.multibit.hd.ui.views.components.enter_password;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.models.Model;

import javax.swing.*;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Entry of password</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class EnterPasswordModel implements Model<String> {

  private Optional<char[]> password = Optional.absent();

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
    if (password.isPresent()) {
      return String.valueOf(password.get());
    } else {
      return "";
    }
  }

  @Override
  public void setValue(String value) {

    Preconditions.checkNotNull(value, "'value' must be present");

    setPassword(value.toCharArray());
  }

  /**
   * @param password The current value of the password and fire a component changed event
   */
  public void setPassword(char[] password) {

    this.password = Optional.of(password);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        // Alert the panel model that a component has changed
        ViewEvents.fireComponentChangedEvent(panelName, Optional.of(this));
      }
    });

  }
}
