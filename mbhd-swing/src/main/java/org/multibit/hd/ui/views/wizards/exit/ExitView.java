package org.multibit.hd.ui.views.wizards.exit;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter amount</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ExitView extends AbstractWizardView<ExitWizardModel, String> {

  /**
   * @param wizard The wizard managing the states
   */
  public ExitView(AbstractWizard<ExitWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.EXIT_TITLE);

    PanelDecorator.addExitCancel(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    setPanelModel("");

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill", // Layout constrains
      "[][][]", // Column constraints
      "[]10[]" // Row constraints
    ));

    panel.add(Components.newContactSearch(),"wrap");
    panel.add(Components.newBitcoinAmount(),"wrap");

    return panel;
  }

  @Override
  public void updatePanelModel() {
    // Do nothing - all work is handled in the actions
  }

}