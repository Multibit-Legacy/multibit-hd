package org.multibit.hd.ui.events.view;

import com.google.common.base.Preconditions;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

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
  private final AbstractWizardModel wizardModel;
  private final boolean isExitCancel;

  /**
   * @param panelName    The panel name (usually taken from the state enum)
   * @param wizardModel  The wizard model
   * @param isExitCancel True if the hide event comes from an exit or cancel
   */
  public WizardHideEvent(String panelName, AbstractWizardModel wizardModel, boolean isExitCancel) {

    Preconditions.checkNotNull(panelName, "'panelName' must be present");
    Preconditions.checkNotNull(wizardModel, "'wizardModel' must be present");

    this.panelName = panelName;
    this.wizardModel = wizardModel;
    this.isExitCancel = isExitCancel;
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
  public AbstractWizardModel getWizardModel() {
    return wizardModel;
  }

  /**
   * @return True if the hide event comes from an exit or cancel
   */
  public boolean isExitCancel() {
    return isExitCancel;
  }

}


