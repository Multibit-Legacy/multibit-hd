package org.multibit.hd.ui.views.wizards;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.ui.events.view.WizardComponentModelChangedEvent;

/**
 * <p>Interface to provide the following to wizard model:</p>
 * <ul>
 * <li>Standard methods</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public interface PanelModel {

  /**
   * @return The name of the panel to show (decouples from a specific wizard's internal state enum)
   */
  String getPanelName();

  /**
   * Handle a "component panel model changed" event (e.g. from a ModelAndView)
   *
   * @param event The event
   */
  @Subscribe
  void onWizardComponentModelChangedEvent(WizardComponentModelChangedEvent event);
}
