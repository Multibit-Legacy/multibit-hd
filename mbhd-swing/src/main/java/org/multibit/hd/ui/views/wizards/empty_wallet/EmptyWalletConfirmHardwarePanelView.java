package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.MessageKey;
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
 * <li>Ask the user to press OK on their hardware wallet whilst sending bitcoin</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class EmptyWalletConfirmHardwarePanelView extends AbstractHardwareWalletWizardPanelView<EmptyWalletWizardModel, EmptyWalletConfirmHardwarePanelModel> {

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public EmptyWalletConfirmHardwarePanelView(AbstractHardwareWalletWizard<EmptyWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.SHIELD, MessageKey.HARDWARE_PRESS_CONFIRM_TITLE);

  }

  @Override
  public void newPanelModel() {

    getWizardModel().setEmptyWalletConfirmHardwarePanelView(this);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]" // Row constraints
      ));

    addCurrentHardwareDisplay(contentPanel);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<EmptyWalletWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

  }

  void setDisplayText(final MessageKey key, final Object... values) {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          hardwareDisplayMaV.getView().setDisplayText(key, values);
        }
      });

  }

  public void setOperationText(final MessageKey key) {
    hardwareDisplayMaV.getView().setOperationText(key);
  }

  public void setDisplayVisible(final boolean visible) {
    hardwareDisplayMaV.getView().setDisplayVisible(visible);

  }

  public void setRecoveryText(final MessageKey key) {
    hardwareDisplayMaV.getView().setRecoveryText(key);

  }
}
