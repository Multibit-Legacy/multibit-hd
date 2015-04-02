package org.multibit.hd.ui.views.components.display_environment_alert;

import org.multibit.hd.core.events.EnvironmentEvent;
import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Store the environment alert text</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class DisplayEnvironmentAlertModel implements Model<EnvironmentEvent> {

  private EnvironmentEvent environmentEvent;

  // Supporting values
  private final String panelName;

  /**
   * @param panelName The underlying panel name (to identify the correct subscriber)
   */
  public DisplayEnvironmentAlertModel(String panelName) {
    this.panelName = panelName;
  }

  @Override
  public EnvironmentEvent getValue() {
    return environmentEvent;
  }

  @Override
  public void setValue(EnvironmentEvent value) {
    this.environmentEvent = value;
  }

  /**
   * @return The underlying panel name (to identify the correct subscriber)
   */
  public String getPanelName() {
    return panelName;
  }

}
