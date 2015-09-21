package org.multibit.hd.ui.views.wizards.welcome.create_trezor_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizardPanelView;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.credentials.CredentialsConfirmCipherKeyPanelModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Ask the user to press "confirm" on their Trezor in response to a Word message</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class CreateTrezorWalletConfirmWordPanelView extends AbstractHardwareWalletWizardPanelView<WelcomeWizardModel, CredentialsConfirmCipherKeyPanelModel> {

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public CreateTrezorWalletConfirmWordPanelView(AbstractHardwareWalletWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.EDIT, MessageKey.HARDWARE_PRESS_NEXT_TITLE);

  }

  @Override
  public void newPanelModel() {

    // Bind it to the wizard model in case of failure
    getWizardModel().setTrezorConfirmWordPanelView(this);

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
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

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
   *
   * @param wordCount The word count
   * @param checking True if the checking phrasing should be used
   */
  public void updateDisplay(int wordCount, boolean checking) {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on EDT");

    String wordCountOrdinal = Languages.getOrdinalFor(wordCount);

    if (checking) {
      hardwareDisplayMaV.getView().setDisplayText(MessageKey.TREZOR_CHECK_WORD_DISPLAY, wordCountOrdinal);
    } else {
      hardwareDisplayMaV.getView().setDisplayText(MessageKey.TREZOR_WORD_DISPLAY, wordCountOrdinal);
    }

  }
}
