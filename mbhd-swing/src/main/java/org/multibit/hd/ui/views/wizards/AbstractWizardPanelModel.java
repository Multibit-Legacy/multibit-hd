package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.events.CoreEvents;

/**
 * <p>Abstract base class wizard models:</p>
 * <ul>
 * <li>Access to standard implementations of required methods</li>
 * </ul>
 *
 * @since 0.0.1
 */
public abstract class AbstractWizardPanelModel implements WizardPanelModel {

  /**
   * The panel name
   */
  protected String panelName;

  protected AbstractWizardPanelModel(String panelName) {

    Preconditions.checkNotNull(panelName, "'panelName' must be present");

    this.panelName = panelName;

    // All wizard panel models can receive events
    CoreEvents.subscribe(this);

  }

  /**
   * <p>Called when the wizard is closing</p>
   */
  public void unsubscribe() {
    CoreEvents.unsubscribe(this);
  }

  @Override
  public String getPanelName() {

    return panelName;

  }

}
