package org.multibit.hd.ui.views.wizards.use_hardware_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardPanelView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Ask the user to press Confirm on their Trezor in response to a Wipe Device message</li>
 * </ul>
 *
 * @since 0.0.5
 *         
 */
public class UseHardwareWalletConfirmWipeDevicePanelView extends AbstractHardwareWalletWizardPanelView<UseHardwareWalletWizardModel, UseHardwareWalletConfirmWipeDevicePanelModel> {

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public UseHardwareWalletConfirmWipeDevicePanelView(AbstractHardwareWalletWizard<UseHardwareWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.ERASER, MessageKey.HARDWARE_PRESS_CONFIRM_TITLE);

  }

  @Override
  public void newPanelModel() {
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    addCurrentHardwareDisplay(contentPanel);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseHardwareWalletWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

    // Set the confirm text
    hardwareDisplayMaV.getView().setOperationText(MessageKey.HARDWARE_PRESS_CONFIRM_OPERATION, getWizardModel().getWalletMode().brand());

    // Show confirm wipe message
    switch (getWizardModel().getWalletMode()) {
      case TREZOR:
        hardwareDisplayMaV.getView().setDisplayText(MessageKey.TREZOR_WIPE_CONFIRM_DISPLAY);
        break;
      case KEEP_KEY:
        hardwareDisplayMaV.getView().setDisplayText(MessageKey.KEEP_KEY_WIPE_CONFIRM_DISPLAY);
        break;
      default:
        throw new IllegalStateException("Unknown hardware wallet: " + getWizardModel().getWalletMode().name());
    }

    // Reassure users that this is an unlock screen but rely on the Trezor buttons to do it
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);
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
