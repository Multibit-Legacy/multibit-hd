package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Restore wallet choices</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */

public class RestoreWalletChoicesPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> implements ActionListener {

  /**
   * @param wizard The wizard managing the states
   * @param panelName   The panel name to filter events from components
   */
  public RestoreWalletChoicesPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.RESTORE_WALLET_TITLE);

    PanelDecorator.addExitCancelPreviousNext(this, wizard);
  }

  @Override
  public JPanel newWizardViewPanel() {

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constraints
      "[][][]", // Column constraints
      "[]" // Row constraints
    ));

    // TODO fill this in

    return panel;
  }

  @Override
  public boolean updateFromComponentModels() {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
    return true;
  }

  /**
   * <p>Handle the "select wallet" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    // Do nothing

  }
}
