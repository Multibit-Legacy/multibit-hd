package org.multibit.hd.ui.views.wizards.welcome;

import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.api.MessageKey;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.PanelDecorator;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardView;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to create a wallet</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class CreateWalletReportView extends AbstractWizardView<WelcomeWizardModel, String> {

  // Model
  private String model;

  /**
   * @param wizard The wizard managing the states
   */
  public CreateWalletReportView(AbstractWizard<WelcomeWizardModel> wizard) {

    super(wizard.getWizardModel(), MessageKey.CREATE_WALLET_REPORT_TITLE);

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public JPanel newDataPanel() {

    model = "TODO replace with a proper model";
    setPanelModel(model);

    JPanel panel = Panels.newPanel(new MigLayout(
      "fill,insets 0", // Layout constrains
      "[][][]", // Column constraints
      "[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    panel.setBackground(Themes.currentTheme.detailPanelBackground());

    panel.add(Labels.newSeedPhraseCreatedStatus(true), "wrap");
    panel.add(Labels.newWalletPasswordCreatedStatus(true), "wrap");
    panel.add(Labels.newBackupLocationStatus(true), "wrap");

    return panel;
  }

  @Override
  public void fireViewEvents() {
    // Do nothing
  }

  @Override
  public boolean updatePanelModel() {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
    return true;
  }

}
