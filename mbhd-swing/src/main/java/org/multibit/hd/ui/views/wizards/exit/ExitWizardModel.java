package org.multibit.hd.ui.views.wizards.exit;

import org.multibit.hd.ui.views.wizards.AbstractWizardModel;
import org.multibit.hd.ui.views.wizards.use_trezor.UseTrezorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger log = LoggerFactory.getLogger(ExitWizardModel.class);
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

  public ExitState getCurrentSelection() {
    return currentSelection;
  }
}
