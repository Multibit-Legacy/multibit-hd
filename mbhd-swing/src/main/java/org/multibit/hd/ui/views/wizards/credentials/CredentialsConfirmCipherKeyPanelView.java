package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

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

public class CredentialsConfirmCipherKeyPanelView extends AbstractWizardPanelView<CredentialsWizardModel, CredentialsConfirmCipherKeyPanelModel> {

  private JTextArea deviceDisplayTextArea;

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public CredentialsConfirmCipherKeyPanelView(AbstractWizard<CredentialsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PRESS_CONFIRM_ON_TREZOR_TITLE, AwesomeIcon.SHIELD);

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
    deviceDisplayTextArea.setText(Languages.safeText(MessageKey.TREZOR_ENCRYPT_MULTIBIT_HD_UNLOCK_TEXT));

    contentPanel.add(Labels.newPressConfirmOnDevice(), "wrap");
    contentPanel.add(deviceDisplayTextArea, "aligny top,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<CredentialsWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    // Don't block an exit
    if (isExitCancel) {
      return true;
    }

    // Disable the buttons while the processing is going on
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        // Ensure the view shows the spinner and disables components
        getFinishButton().setEnabled(false);
        getExitButton().setEnabled(false);
        getRestoreButton().setEnabled(false);

      }
    });

    // Defer the hide operation
    return false;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

    // Determine any events
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.FINISH,
      isUnlockEnabled()
    );

  }

  /**
   * @return True if the "unlock" button should be enabled
   */
  private boolean isUnlockEnabled() {

    return getWizardModel().getEntropy().isPresent();

  }

}
