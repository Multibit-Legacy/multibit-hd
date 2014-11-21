package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.trezor_screen.TrezorScreenModel;
import org.multibit.hd.ui.views.components.trezor_screen.TrezorScreenView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Change PIN: Request remove PIN</li>
 * </ul>
 *
 * @since 0.0.5
 *  
 */
public class ChangePinRequestRemovePinPanelView extends AbstractWizardPanelView<ChangePinWizardModel, ChangePinEnterPinPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(ChangePinRequestRemovePinPanelView.class);

  private ModelAndView<TrezorScreenModel, TrezorScreenView> trezorScreenMaV;

  private JLabel message = Labels.newCommunicatingWithTrezor();

  private JTextArea deviceDisplayTextArea = TextBoxes.newTrezorV1Display();

  /**
   * @param wizard The wizard managing the states
   */
  public ChangePinRequestRemovePinPanelView(AbstractWizard<ChangePinWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PIN_TITLE, AwesomeIcon.LOCK);

  }

  @Override
  public void newPanelModel() {

    // Bind it to the wizard model in case of failure
    getWizardModel().setRequestRemovePinPanelView(this);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    // Hide the display area initially
    deviceDisplayTextArea.setVisible(false);

    // Need some text here in case device fails just as we being the process
    contentPanel.add(message, "align left,wrap");
    contentPanel.add(deviceDisplayTextArea, "align center," + MultiBitUI.TREZOR_SCREEN_MAX_WIDTH_MIG + ",wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ChangePinWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Initialise with "Unlock" disabled to force users to enter credentials
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      false
    );

  }

  @Override
  public void afterShow() {

    // Start the wallet access process by requesting a cipher key
    // to get a deterministic wallet ID
    //
    // This is done as a transitional panel to allow for a device
    // failure at each stage with the user having the option to
    // easily escape
    getWizardModel().requestChangeOrRemovePin();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing we are a transitional view

  }

  /**
   * @param deviceText The text showing on the device
   */
  public void setDeviceText(String deviceText) {

    this.deviceDisplayTextArea.setVisible(true);
    this.deviceDisplayTextArea.setText(deviceText);

    this.trezorScreenMaV.getView().setDeviceText(MessageKey.TREZOR_ENCRYPT_MULTIBIT_HD_UNLOCK_TEXT);

  }

}