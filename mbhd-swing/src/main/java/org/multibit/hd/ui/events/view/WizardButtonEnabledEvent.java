package org.multibit.hd.ui.events.view;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.views.wizards.WizardButton;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates a wizard button enabled status has changed</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WizardButtonEnabledEvent implements ViewEvent {

  private final String panelName;
  private final WizardButton wizardButton;
  private final boolean enabled;

  public WizardButtonEnabledEvent(String panelName, WizardButton wizardButton, boolean enabled) {

    Preconditions.checkNotNull(panelName, "'panelName' must be present");
    Preconditions.checkNotNull(wizardButton, "'wizardButton' must be present");

    this.panelName = panelName;
    this.wizardButton = wizardButton;
    this.enabled = enabled;
  }

  /**
   * @return The panel name
   */
  public String getPanelName() {
    return panelName;
  }

  /**
   * @return The wizard button
   */
  public WizardButton getWizardButton() {
    return wizardButton;
  }

  /**
   * @return True if the button is to be enabled
   */
  public boolean isEnabled() {
    return enabled;
  }
}
