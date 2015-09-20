package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Ask the user to press ok on their Trezor in response to an Encrypt Code message</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class UseTrezorPressConfirmForEncryptCodePanelView extends AbstractWizardPanelView<UseTrezorWizardModel, UseTrezorPressConfirmForEncryptCodePanelModel> {

  private JTextArea deviceDisplayTextArea;

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public UseTrezorPressConfirmForEncryptCodePanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.HARDWARE_PRESS_CONFIRM_TITLE, AwesomeIcon.SHIELD);

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

    deviceDisplayTextArea = TextBoxes.newReadOnlyTextArea(5,50);
    deviceDisplayTextArea.setText(Languages.safeText(MessageKey.TREZOR_ENCRYPT_MULTIBIT_HD_UNLOCK_DISPLAY));

    contentPanel.add(Labels.newPressConfirmOnDevice(), "wrap");
    contentPanel.add(deviceDisplayTextArea,"aligny top,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

  }

}
