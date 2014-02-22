package org.multibit.hd.ui.views.wizards.password;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelModel;

/**
 * <p>Panel model to provide the following to "send bitcoin report" wizard:</p>
 * <ul>
 * <li>Storage of state for the "send bitcoin report " panel</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PasswordReportPanelModel extends AbstractWizardPanelModel {

  private Optional<Boolean> passwordRecoveredSuccessfully=Optional.absent();

  /**
   * @param panelName The panel name
   */
  public PasswordReportPanelModel(String panelName) {

    super(panelName);

    passwordRecoveredSuccessfully = Optional.absent();

  }

  /**
   * @return True if recovered successully, false if not, absent if unknown
   */
  public Optional<Boolean> getPasswordRecoveredSuccessfully() {
    return passwordRecoveredSuccessfully;
  }
}
