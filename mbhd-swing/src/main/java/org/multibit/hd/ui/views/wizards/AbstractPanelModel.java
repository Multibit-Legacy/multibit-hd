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
      updateFromComponentModel(event.getComponentModel());

    }

  }

  /**
   * <p>Called when a component model has changed and the panel model should be made aware</p>
   *
   * <p>Implementers must update the panel model with the contents of the component model (if no direct reference is available)</p>
   *
   * @param componentModel The component model triggering the update
   */
  protected abstract void updateFromComponentModel(Optional componentModel);
}
