package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Select how wallet is to be accessed (create/restore/hardware/switch)</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class SelectWalletView extends AbstractWizardView<WelcomeWizardModel, WelcomeWizardState> implements ActionListener {

  // Model
  private WelcomeWizardState currentSelection;

  /**
   * @param wizard The wizard managing the states
   */
  public SelectWalletView(AbstractWizard<WelcomeWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.SELECT_WALLET_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    currentSelection = CREATE_WALLET_SEED_PHRASE;
    setPanelModel(currentSelection);

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,ins 0", // Layout constrains
      "[]", // Column constraints
      "[]" // Row constraints
    ));

    panel.add(Panels.newWalletSelector(
      this,
      CREATE_WALLET_SEED_PHRASE.name(),
      RESTORE_WALLET.name(),
      HARDWARE_WALLET.name(),
      SWITCH_WALLET.name()
    ), "wrap");

    return panel;
  }

  @Override
  public boolean updatePanelModel() {

    setPanelModel(currentSelection);
    return false;

  }

  /**
   * <p>Handle the "select wallet" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JRadioButton source = (JRadioButton) e.getSource();

    currentSelection = WelcomeWizardState.valueOf(source.getActionCommand());

  }
}
