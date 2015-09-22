package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AbstractHardwareWalletComponentView;
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
 * <li>Ask the user to press "confirm" on their Trezor in response to an Encrypt message</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class CredentialsConfirmCipherKeyPanelView extends AbstractHardwareWalletWizardPanelView<CredentialsWizardModel, CredentialsConfirmCipherKeyPanelModel> {

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public CredentialsConfirmCipherKeyPanelView(AbstractHardwareWalletWizard<CredentialsWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.SHIELD, MessageKey.HARDWARE_PRESS_CONFIRM_TITLE);

  }

  @Override
  public void newPanelModel() {

    // Bind it to the wizard model in case of failure
    getWizardModel().setConfirmCipherKeyPanelView(this);

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
  protected void initialiseButtons(AbstractWizard<CredentialsWizardModel> wizard) {

    PanelDecorator.addExitCancelUnlock(this, wizard);

  }

  @Override
  public void afterShow() {

    // Set the confirm text
    hardwareDisplayMaV.getView().setOperationText(MessageKey.HARDWARE_UNLOCK_OPERATION, getWizardModel().getWalletMode().brand());

    // Show unlock message
    switch (getWizardModel().getWalletMode()) {
      case TREZOR:
        hardwareDisplayMaV.getView().setDisplayText(MessageKey.TREZOR_ENCRYPT_MULTIBIT_HD_UNLOCK_DISPLAY);
        break;
      case KEEP_KEY:
        hardwareDisplayMaV.getView().setDisplayText(MessageKey.KEEP_KEY_ENCRYPT_MULTIBIT_HD_UNLOCK_DISPLAY);
        break;
      default:
        throw new IllegalStateException("Unknown hardware wallet: " + getWizardModel().getWalletMode().name());
    }

    // Reassure users that this is an unlock screen but rely on the Trezor buttons to do it
    getFinishButton().setEnabled(false);

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

  /**
   * @return The Trezor display view to avoid method duplication
   */
  public AbstractHardwareWalletComponentView getHardwareDisplayView() {
    return hardwareDisplayMaV.getView();
  }

  public void enableForFailedUnlock() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on EDT");

    getFinishButton().setEnabled(false);
    getExitButton().setEnabled(true);

    hardwareDisplayMaV.getView().setSpinnerVisible(false);

  }

  public void incorrectEntropy() {

    hardwareDisplayMaV.getView().incorrectEntropy();

  }
}
