package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.hardware.core.messages.Features;
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
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Panel telling the user to press the continue button to wipe their Trezor</li>
 * </ul>
 *
 * @since 0.0.5
 *
 */
public class UseTrezorRequestWipeDevicePanelView extends AbstractWizardPanelView<UseTrezorWizardModel, UseTrezorWipeDevicePanelModel> {

  private ModelAndView<TrezorDisplayModel, TrezorDisplayView> trezorDisplayMaV;

  /**
   * @param wizard The wizard managing the states
   */
  public UseTrezorRequestWipeDevicePanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.ERASER, MessageKey.HARDWARE_WIPE_DEVICE_TITLE, null);

  }

  @Override
  public void newPanelModel() {

    // Bind it to the wizard model in case of failure
    //getWizardModel().setRequestWipeDevicePanelView(this);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXLayout(),
        "[]", // Column constraints
        "[]" // Row constraints
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
  public void fireInitialStateViewEvents() {

    // Initialise with "Next" disabled to force users to work with Trezor
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      false
    );

  }

  @Override
  public void afterShow() {

    // Check if the attached Trezor is initialised (the hardware wallet service must be OK to be here)
    Optional<Features> features = CoreServices.getCurrentHardwareWalletService().get().getContext().getFeatures();

    final MessageKey operationKey;
    final boolean showReportView;
    if (!features.isPresent()) {
      operationKey = MessageKey.HARDWARE_FAILURE_OPERATION;
      showReportView = true;
    } else {
      if (features.get().isInitialized()) {
        operationKey = MessageKey.COMMUNICATING_WITH_HARDWARE_OPERATION;
        showReportView = false;
      } else {
        operationKey = MessageKey.HARDWARE_NO_WALLET_OPERATION;
        showReportView = true;
      }
    }
    getWizardModel().setReportMessageKey(operationKey);

    // Set the communication message
    trezorDisplayMaV.getView().setOperationText(operationKey);

    if (showReportView) {
      trezorDisplayMaV.getView().setRecoveryText(MessageKey.CLICK_NEXT_TO_CONTINUE);
    }

    // This could take a while (device may tarpit after failed PINs etc)
    trezorDisplayMaV.getView().setSpinnerVisible(!showReportView);

    // Override the earlier button enable setting
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      showReportView
    );

    // Update the wizard model so we can change state
    //getWizardModel().setShowReportView(showReportView);

    if (!showReportView) {

      // Start the wipe process
      //
      // This is done as a transitional panel to allow for a device
      // failure at each stage with the user having the option to
      // easily escape
      getWizardModel().requestWipeDevice();

    }

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing we are a transitional view

  }

  /**
   * @param key The key to the operation text
   */
  public void setOperationText(MessageKey key) {
    this.trezorDisplayMaV.getView().setOperationText(key);
  }

}
