package org.multibit.hd.ui.views.wizards.use_hardware_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
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
 * <li>Credentials: Enter pin</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class UseHardwareWalletRequestCipherKeyPanelView extends AbstractWizardPanelView<UseHardwareWalletWizardModel, UseHardwareWalletEnterPinPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(UseHardwareWalletRequestCipherKeyPanelView.class);

  private JLabel message = Labels.newCommunicatingWithHardware();

  /**
   * @param wizard The wizard managing the states
   */
  public UseHardwareWalletRequestCipherKeyPanelView(AbstractWizard<UseHardwareWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.LOCK, MessageKey.PIN_TITLE);

  }

  @Override
  public void newPanelModel() {

    // Bind it to the wizard model in case of failure
    getWizardModel().setRequestCipherKeyPanelView(this);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXLayout(),
        "[120][][][40]", // Column constraints
        "[]12[][][30]" // Row constraints
      ));

    // Need some text here in case device fails just as we being the process
    contentPanel.add(Labels.newBlankLabel());
    contentPanel.add(message, "align left,span 2,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseHardwareWalletWizardModel> wizard) {

    PanelDecorator.addExitCancelUnlock(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Initialise with "Unlock" disabled to force users to enter credentials
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      false
    );

  }

  @Override
  public void afterShow() {

    registerDefaultButton(getFinishButton());

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing we are a transitional view

  }

  /**
   * @param message The message text
   */
  public void setMessage(String message) {
    this.message.setText(message);
  }

}