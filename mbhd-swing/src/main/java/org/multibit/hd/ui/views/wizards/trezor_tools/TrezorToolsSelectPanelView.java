package org.multibit.hd.ui.views.wizards.trezor_tools;

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

public class TrezorToolsSelectPanelView extends AbstractWizardPanelView<TrezorToolsWizardModel, TrezorToolsState> implements ActionListener {

  // Model
  private TrezorToolsState currentSelection;

  // View components

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public TrezorToolsSelectPanelView(AbstractWizard<TrezorToolsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TREZOR_TOOLS_TITLE, AwesomeIcon.SHIELD);

  }

  @Override
  public void newPanelModel() {

    currentSelection = TrezorToolsState.VERIFY_DEVICE;
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
      Panels.newTrezorToolSelector(
        this,
        TrezorToolsState.VERIFY_DEVICE.name(),
        TrezorToolsState.WIPE_DEVICE.name()
      ), "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<TrezorToolsWizardModel> wizard) {
    PanelDecorator.addExitCancelPreviousNext(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Enable the "next" button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);

  }

  @Override
  public boolean beforeShow() {

    return true;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Next has been clicked

    setPanelModel(currentSelection);

    // Bind this to the wizard model
    //getWizardModel().setSelectWalletChoice(currentSelection);

    // The welcome wizard overrides the Next button behaviour to simulate a Finish
    // if this is a "use existing wallet" selection

  }

  /**
   * <p>Handle the "select tool" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JRadioButton source = (JRadioButton) e.getSource();

    currentSelection = TrezorToolsState.valueOf(source.getActionCommand());

  }
}
