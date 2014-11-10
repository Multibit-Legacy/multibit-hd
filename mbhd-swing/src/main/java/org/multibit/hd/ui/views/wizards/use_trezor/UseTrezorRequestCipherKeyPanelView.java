package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
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
public class UseTrezorRequestCipherKeyPanelView extends AbstractWizardPanelView<UseTrezorWizardModel, UseTrezorEnterPinPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(UseTrezorRequestCipherKeyPanelView.class);

  // Communicate with the device off the EDT
  private final ListeningExecutorService cipherKeyExecutorService = SafeExecutors.newSingleThreadExecutor("cipher-key");

  // TODO Add language support
  private JLabel message = Labels.newValueLabel("Talking to device...");
  private String status;

  /**
   * @param wizard The wizard managing the states
   */
  public UseTrezorRequestCipherKeyPanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PIN_TITLE, AwesomeIcon.LOCK);

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
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {

    PanelDecorator.addExitCancelRestoreUnlock(this, wizard);

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

    // Start the wallet access process by requesting a cipher key
    // to get a deterministic wallet ID
    //
    // This is done as a transitional panel to allow for a device
    // failure at each stage with the user having the option to
    // easily escape
    getWizardModel().requestCipherKey();

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