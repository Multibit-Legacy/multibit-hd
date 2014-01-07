package org.multibit.hd.ui.views.wizards.exit;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.WizardModel;

import static org.multibit.hd.ui.views.wizards.exit.ExitState.CONFIRM_EXIT;

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
public class ExitWizardModel implements WizardModel {

  /**
   * The current state
   */
  private ExitState state = CONFIRM_EXIT;

  public ExitWizardModel(ExitState state) {
    this.state = state;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <P> void update(Optional<P> panelModel) {

    // No state transitions occur in this method

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
    return state.name();
  }

}
