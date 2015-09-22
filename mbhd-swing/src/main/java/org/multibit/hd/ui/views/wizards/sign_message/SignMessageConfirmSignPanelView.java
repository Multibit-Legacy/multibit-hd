package org.multibit.hd.ui.views.wizards.sign_message;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardPanelView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Ask the user to press "confirm" on their Trezor in response to a Sign message</li>
 * </ul>
 *
 * @since 0.0.8
 *  
 */
public class SignMessageConfirmSignPanelView extends AbstractHardwareWalletWizardPanelView<SignMessageWizardModel, String> {

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public SignMessageConfirmSignPanelView(AbstractHardwareWalletWizard<SignMessageWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.SHIELD, MessageKey.HARDWARE_PRESS_CONFIRM_TITLE);

  }

  @Override
  public void newPanelModel() {

    // Bind it to the wizard model in case of failure
    getWizardModel().setConfirmSignPanelView(this);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]10[]" // Row constraints
      ));

    addCurrentHardwareDisplay(contentPanel);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<SignMessageWizardModel> wizard) {

    PanelDecorator.addNext(this, wizard);

  }

  @Override
  public void afterShow() {

    String truncatedMessage = getWizardModel().getMessage().substring(0, Math.min(getWizardModel().getMessage().length(), 64));

    // Set the confirm text
    hardwareDisplayMaV.getView().setOperationText(MessageKey.HARDWARE_PRESS_CONFIRM_OPERATION, getWizardModel().getWalletMode().brand());

    // Show sign message
    hardwareDisplayMaV.getView().setDisplayText(MessageKey.TREZOR_SIGN_MESSAGE_CONFIRM_DISPLAY, truncatedMessage);

    // Reassure users that this is a sign screen but rely on the Trezor buttons to do it
    getNextButton().setEnabled(false);

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    // Don't block an exit
    if (isExitCancel) {
      return true;
    }

    // Defer the hide operation
    return false;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

  }

}
