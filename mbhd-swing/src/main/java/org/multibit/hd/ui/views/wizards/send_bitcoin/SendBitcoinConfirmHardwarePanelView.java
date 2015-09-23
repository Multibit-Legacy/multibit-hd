package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.ViewKey;
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
 * <li>Ask the user to press ok on their Trezor whilst sending bitcoin</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class SendBitcoinConfirmHardwarePanelView extends AbstractHardwareWalletWizardPanelView<SendBitcoinWizardModel, SendBitcoinConfirmHardwarePanelModel> {

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public SendBitcoinConfirmHardwarePanelView(AbstractHardwareWalletWizard<SendBitcoinWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.SHIELD, MessageKey.HARDWARE_PRESS_CONFIRM_TITLE);

  }

  @Override
  public void newPanelModel() {

    getWizardModel().setSendBitcoinConfirmHardwarePanelView(this);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]" // Row constraints
    ));

    addCurrentHardwareDisplay(contentPanel);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<SendBitcoinWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {
    if (isExitCancel) {
      // Ensure Header is enabled
      final boolean viewHeader = Configurations.currentConfiguration.getAppearance().isShowBalance();
      ViewEvents.fireViewChangedEvent(ViewKey.HEADER, viewHeader);
    }
    return true;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

  }

  void setDisplayText(final MessageKey key, final Object... values) {

    SwingUtilities.invokeLater(new Runnable() {
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
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        hardwareDisplayMaV.getView().setDisplayVisible(visible);
      }
    });

  }

  public void setRecoveryText(final MessageKey key) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        hardwareDisplayMaV.getView().setRecoveryText(key);
      }
    });

  }
}
