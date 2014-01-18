package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.WizardComponentModelChangedEvent;

/**
 * <p>Abstract base class wizard models:</p>
 * <ul>
 * <li>Access to standard implementations of required methods</li>
 * </ul>
 *
 * @param <S> The state object type
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractPanelModel implements PanelModel {

  /**
   * The panel name
   */
  protected String panelName;

  protected AbstractPanelModel(String panelName) {

    Preconditions.checkNotNull(panelName, "'panelName' must be present");

    this.panelName = panelName;

    // Register for events
    CoreServices.uiEventBus.register(this);

  }

  @Override
  public String getPanelName() {

    return panelName;

  }

  /**
   * <p>Provides default behaviour of updating the panel model from the component model if the panel name matches</p>
   *
   * @param event The event
   */
  @Override
  public void onWizardComponentModelChangedEvent(WizardComponentModelChangedEvent event) {

    if (panelName.equals(event.getPanelName())) {

      // Default behaviour is to update
      update(event.getComponentModel());

    }

  }

  /**
   * <p>Update the panel model with the contents of the component model</p>
   *
   * <p>After updating this will normally fire "wizard panel model changed" event:</p>
   * <pre>
   *   ViewEvents.fireWizardPanelModelChangedEvent(panelName, Optional.of(this));
   * </pre>
   *
   * @param componentModel The component model triggering the update
   */
  protected abstract void update(Optional componentModel);
}
