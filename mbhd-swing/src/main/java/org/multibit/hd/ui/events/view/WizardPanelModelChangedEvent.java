package org.multibit.hd.ui.events.view;

import com.google.common.base.Optional;

/**
 * <p>Event to provide the following to View Event API:</p>
 * <ul>
 * <li>Indicates a wizard panel model has changed</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WizardPanelModelChangedEvent implements ViewEvent {

  private final Optional panelModel;

  public WizardPanelModelChangedEvent(Optional panelModel) {
    this.panelModel = panelModel;
  }

  /**
   * @return The panel model
   */
  public Optional getPanelModel() {
    return panelModel;
  }
}
