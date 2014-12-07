package org.multibit.hd.ui.views.wizards.exit;

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

  /**
   * @param state The state object
   */
  public ExitWizardModel(ExitState state) {
    super(state);
  }

  public void setState(ExitState state) {
    this.state = state;
  }

}
