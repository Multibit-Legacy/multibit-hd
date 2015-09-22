package org.multibit.hd.ui.views.wizards.use_hardware_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Select which of the trezor related tools to run</li>
 * </ul>
 *
 * @since 0.0.1
 */

public class UseHardwareWalletSelectPanelView extends AbstractWizardPanelView<UseHardwareWalletWizardModel, UseHardwareWalletState> implements ActionListener {

  // Model
  private UseHardwareWalletState currentSelection;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public UseHardwareWalletSelectPanelView(AbstractWizard<UseHardwareWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.SHIELD, MessageKey.USE_HARDWARE_TITLE, wizard.getWizardModel().getWalletMode().brand());

  }

  @Override
  public void newPanelModel() {

    currentSelection = UseHardwareWalletState.BUY_DEVICE;
    setPanelModel(currentSelection);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]" // Row constraints
      ));

    contentPanel.add(
      Panels.newUseHardwareWalletSelector(
        this,
        UseHardwareWalletState.BUY_DEVICE.name(),
        UseHardwareWalletState.VERIFY_DEVICE.name(),
        UseHardwareWalletState.REQUEST_WIPE_DEVICE.name(),
        getWizardModel().getWalletMode().brand()
      ), "span 2, wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseHardwareWalletWizardModel> wizard) {
    PanelDecorator.addExitCancelNext(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {

    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT,true);

  }

  @Override
  public boolean beforeShow() {
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT,true);

    return true;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Next has been clicked

    setPanelModel(currentSelection);

    // Bind this to the wizard model
    getWizardModel().setCurrentSelection(currentSelection);

  }

  /**
   * <p>Handle the "select tool" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JRadioButton source = (JRadioButton) e.getSource();

    currentSelection = UseHardwareWalletState.valueOf(source.getActionCommand());

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Ensure the wizard model correctly reflects the contents of the components
      updateFromComponentModels(Optional.absent());

    }

    // Must be OK to proceed
    return true;
  }
}
