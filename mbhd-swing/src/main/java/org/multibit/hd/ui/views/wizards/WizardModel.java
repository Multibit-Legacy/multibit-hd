package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import org.multibit.hd.ui.events.view.WizardPanelModelChangedEvent;

/**
 * <p>Interface to provide the following to wizard model:</p>
 * <ul>
 * <li>Standard methods</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public interface WizardModel {

  /**
   * Called when the "next" button is clicked
   */
  void next();

  /**
   * Called when the "previous" button is clicked
   */
  void previous();

  /**
   * @return The name of the panel to show (decouples from a specific wizard's internal state enum)
   */
  String getPanelName();

  /**
   * Update the wizard model with the panel model based on the current state
   *
   * @param panelModel The panel model (can be absent)
   */
  void update(Optional panelModel);

  /**
   * Handle a "wizard panel model changed" event
   *
   * @param event The event
   */
  @Subscribe
  void onWizardPanelModelChangedEvent(WizardPanelModelChangedEvent event);
}
