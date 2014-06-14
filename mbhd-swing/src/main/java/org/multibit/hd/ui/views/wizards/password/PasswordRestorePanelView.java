package org.multibit.hd.ui.views.wizards.password;

import com.google.common.base.Optional;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Password: Restore</li>
 * </ul>
 *
 * <p>This view is a transition view to ease the hand over process</p>
 *
 * @since 0.0.1
 *  
 */
public class PasswordRestorePanelView extends AbstractWizardPanelView<PasswordWizardModel, String> {

  /**
   * @param wizard The wizard managing the states
   */
  public PasswordRestorePanelView(AbstractWizard<PasswordWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PASSWORD_TITLE, AwesomeIcon.LOCK);

  }

  @Override
  public void newPanelModel() {

    // Do nothing
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    // Do nothing

  }

  @Override
  protected void initialiseButtons(AbstractWizard<PasswordWizardModel> wizard) {

    // Do nothing
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing

  }

}