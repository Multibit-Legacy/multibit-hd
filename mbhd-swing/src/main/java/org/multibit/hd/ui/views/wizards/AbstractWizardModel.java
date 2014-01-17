package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.WizardPanelModelChangedEvent;

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
public abstract class AbstractWizardModel<S> implements WizardModel {

  /**
   * The current state
   */
  protected S state;

  protected AbstractWizardModel(S state) {

    Preconditions.checkNotNull(state, "'state' must be present");

    this.state = state;

    // Register for events
    CoreServices.uiEventBus.register(this);

  }

  @Override
  public void next() {
    // Do nothing
  }

  @Override
  public void previous() {
    // Do nothing
  }

  @Override
  public String getPanelName() {

    // Enums are commonly used for state and toString is equivalent to name()
    return state.toString();

  }

  @Override
  public void update(Optional panelModel) {
    // Do nothing
  }

  /**
   * <p>Provides default behaviour of updating the wizard model from the panel model if the panel name matches the current state</p>
   *
   * @param event The event
   */
  @Override
  public void onWizardPanelModelChangedEvent(WizardPanelModelChangedEvent event) {

    if (state.toString().equals(event.getPanelName())) {

      // Default behaviour is to update
      update(event.getPanelModel());

    }

  }
}
