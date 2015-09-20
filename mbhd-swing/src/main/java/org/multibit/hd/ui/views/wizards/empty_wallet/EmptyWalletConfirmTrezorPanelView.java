package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
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

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Ask the user to press OK on their Trezor whilst sending bitcoin</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class EmptyWalletConfirmTrezorPanelView extends AbstractWizardPanelView<EmptyWalletWizardModel, EmptyWalletConfirmTrezorPanelModel> {

  private ModelAndView<TrezorDisplayModel, TrezorDisplayView> trezorDisplayMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public EmptyWalletConfirmTrezorPanelView(AbstractWizard<EmptyWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.SHIELD, MessageKey.HARDWARE_PRESS_CONFIRM_TITLE, null);

  }

  @Override
  public void newPanelModel() {

    getWizardModel().setEmptyWalletConfirmTrezorPanelView(this);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]" // Row constraints
      ));

    trezorDisplayMaV = Components.newTrezorDisplayMaV(getPanelName());

    contentPanel.add(trezorDisplayMaV.getView().newComponentPanel(), "align center,wrap");

    registerComponents(trezorDisplayMaV);
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
          trezorDisplayMaV.getView().setDisplayText(key, values);
        }
      });

  }

  public void setOperationText(final MessageKey key) {
    trezorDisplayMaV.getView().setOperationText(key);
  }

  public void setDisplayVisible(final boolean visible) {
    trezorDisplayMaV.getView().setDisplayVisible(visible);

  }

  public void setRecoveryText(final MessageKey key) {
    trezorDisplayMaV.getView().setRecoveryText(key);

  }
}
