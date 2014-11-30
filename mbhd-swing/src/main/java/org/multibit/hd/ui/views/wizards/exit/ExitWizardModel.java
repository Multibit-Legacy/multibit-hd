package org.multibit.hd.ui.views.wizards.exit;

import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

/**
 * <p>Model object to provide the following to "exit" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ExitWizardModel extends AbstractWizardModel<ExitState> {

  private ExitState currentSelection;

  /**
   * @param state The state object
   */
  public ExitWizardModel(ExitState state) {
    super(state);
  }

  public void setCurrentSelection(ExitState currentSelection) {
    this.currentSelection = currentSelection;
  }

  @Override
  public void showNext() {

    switch (currentSelection) {

      case CONFIRM_EXIT:
        // User wishes to exit the application
        CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.HARD);
        break;
      case SWITCH_WALLET:
        // User wishes to switch to a different wallet
        CoreEvents.fireShutdownEvent(ShutdownEvent.ShutdownType.SOFT);
        break;
    }

  }
}
