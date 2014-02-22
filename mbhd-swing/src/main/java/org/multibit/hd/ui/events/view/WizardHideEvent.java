package org.multibit.hd.ui.events.view;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.views.wizards.WizardModel;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates a wizard has been hidden</li>
 * </ul>
 * <p>A detail screen model will typically subscribe and update in response to this event</p>
 *
 * @since 0.0.1
 *  
 */
public class WizardHideEvent implements ViewEvent {

  private final String panelName;
  private final WizardModel wizardModel;

  public WizardHideEvent(String panelName, WizardModel wizardModel) {

    Preconditions.checkNotNull(panelName, "'panelName' must be present");
    Preconditions.checkNotNull(wizardModel, "'wizardModel' must be present");

    this.panelName = panelName;
    this.wizardModel = wizardModel;
  }

  /**
   * @return The panel name (to identify the correct subscriber)
   */
  public String getPanelName() {
    return panelName;
  }

  /**
   * @return The wizard model
   */
  public WizardModel getWizardModel() {
    return wizardModel;
  }
}


