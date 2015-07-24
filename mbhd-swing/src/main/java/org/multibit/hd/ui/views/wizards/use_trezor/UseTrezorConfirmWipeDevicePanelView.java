package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.trezor_display.TrezorDisplayModel;
import org.multibit.hd.ui.views.components.trezor_display.TrezorDisplayView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
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
public class UseTrezorConfirmWipeDevicePanelView extends AbstractWizardPanelView<UseTrezorWizardModel, UseTrezorPressConfirmForEncryptCodePanelModel> {

  private ModelAndView<TrezorDisplayModel, TrezorDisplayView> trezorDisplayMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public UseTrezorConfirmWipeDevicePanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TREZOR_PRESS_CONFIRM_TITLE, AwesomeIcon.ERASER);

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

    trezorDisplayMaV = Components.newTrezorDisplayMaV(getPanelName());

    contentPanel.add(trezorDisplayMaV.getView().newComponentPanel(), "align center,wrap");

    // Register the components
    registerComponents(trezorDisplayMaV);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

    // Set the confirm text
    trezorDisplayMaV.getView().setOperationText(MessageKey.TREZOR_PRESS_CONFIRM_OPERATION);

    // Show unlock message
    trezorDisplayMaV.getView().setDisplayText(MessageKey.TREZOR_WIPE_CONFIRM_DISPLAY);

    // Reassure users that this is an unlock screen but rely on the Trezor buttons to do it
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, false);
  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    // Don't block an exit
    if (isExitCancel) {

      // Ensure we send a Cancel to the device
      getWizardModel().requestCancel();

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
