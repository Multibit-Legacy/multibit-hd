package org.multibit.hd.ui.views.components.display_security_alert;

import org.multibit.hd.core.events.SecurityEvent;
import org.multibit.hd.ui.models.Model;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Store the security alert text</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class DisplaySecurityAlertModel implements Model<SecurityEvent> {

  private SecurityEvent securityEvent;

  // Supporting values
  private final String panelName;

  /**
   * @param panelName The underlying panel name (to identify the correct subscriber)
   */
  public DisplaySecurityAlertModel(String panelName) {
    this.panelName = panelName;
  }

  @Override
  public SecurityEvent getValue() {
    return securityEvent;
  }

  @Override
  public void setValue(SecurityEvent value) {
    this.securityEvent = value;
  }

  /**
   * @return The underlying panel name (to identify the correct subscriber)
   */
  public String getPanelName() {
    return panelName;
  }

}
