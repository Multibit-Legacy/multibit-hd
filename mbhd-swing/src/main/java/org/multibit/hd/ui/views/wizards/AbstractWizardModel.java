package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.services.CoreServices;

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
public abstract class AbstractWizardModel<S> {

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

  public void showNext() {
    // Do nothing
  }

  public void showPrevious() {
    // Do nothing
  }

  public String getPanelName() {

    // Enums are commonly used for state and toString is equivalent to name()
    return state.toString();

  }

}
