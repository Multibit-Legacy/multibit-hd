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
  private String label;

  @Override
  public SecurityEvent getValue() {
    return securityEvent;
  }

  @Override
  public void setValue(SecurityEvent value) {
    this.securityEvent = value;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

}
