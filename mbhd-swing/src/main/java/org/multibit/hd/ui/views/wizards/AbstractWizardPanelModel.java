package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.services.CoreServices;

/**
 * <p>Abstract base class wizard models:</p>
 * <ul>
 * <li>Access to standard implementations of required methods</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public abstract class AbstractWizardPanelModel implements WizardPanelModel {

  /**
   * The panel name
   */
  protected String panelName;

  protected AbstractWizardPanelModel(String panelName) {

    Preconditions.checkNotNull(panelName, "'panelName' must be present");

    this.panelName = panelName;

    // Register for events
    CoreServices.uiEventBus.register(this);

  }

  @Override
  public String getPanelName() {

    return panelName;

  }

}
