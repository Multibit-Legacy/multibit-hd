package org.multibit.hd.ui.views.wizards.send_bitcoin;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show send Bitcoin progress report</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SendBitcoinReportView extends AbstractWizardView<SendBitcoinWizardModel, String> {

  // Model
  private String model;

  /**
   * @param wizard The wizard managing the states
   */
  public SendBitcoinReportView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {

    super(wizard.getWizardModel(), panelName, MessageKey.SEND_PROGRESS_TITLE);

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public JPanel newWizardViewPanel() {

    model = "TODO replace with a proper model";
    setPanelModel(model);

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constraints
      "[][][]", // Column constraints
      "[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    panel.add(Panels.newBroadcastStatus(), "wrap");
    panel.add(Panels.newRelayStatus(), "wrap");
    panel.add(Panels.newConfirmationCountStatus("6+", true), "wrap");

    return panel;
  }

  @Override
  public boolean updateFromComponentModels() {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
    return true;
  }

}
