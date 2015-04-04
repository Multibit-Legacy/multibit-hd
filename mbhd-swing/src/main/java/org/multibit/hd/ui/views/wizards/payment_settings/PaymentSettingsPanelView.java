package org.multibit.hd.ui.views.wizards.payment_settings;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.blockexplorer.BlockExplorer;
import org.multibit.hd.core.blockexplorer.BlockExplorers;
import org.multibit.hd.core.config.AppearanceConfiguration;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
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
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Payment settings: switch block explorer</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */

public class PaymentSettingsPanelView extends AbstractWizardPanelView<PaymentSettingsWizardModel, PaymentSettingsPanelModel> implements ActionListener {

  // Panel specific components
  private JComboBox<String> blockExplorerComboBox;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public PaymentSettingsPanelView(AbstractWizard<PaymentSettingsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.PAYMENT_SETTINGS_TITLE, AwesomeIcon.MONEY);

  }

  @Override
  public void newPanelModel() {

    // Use a deep copy to avoid reference leaks
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    // Configure the panel model
    setPanelModel(new PaymentSettingsPanelModel(
      getPanelName(),
      configuration
    ));

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[][]" // Row constraints
    ));

    AppearanceConfiguration appearanceConfiguration = Configurations.currentConfiguration.getAppearance().deepCopy();

    blockExplorerComboBox = ComboBoxes.newBlockExplorerComboBox(this, appearanceConfiguration.getBlockExplorerId());

    contentPanel.add(Labels.newBlockExplorer(), "shrink");
    contentPanel.add(blockExplorerComboBox, "growx,shrinky,width min:250:,push,wrap");

    contentPanel.add(Labels.newBlankLabel(), "grow,span 2,push,wrap"); // Fill out the remainder

  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentSettingsWizardModel> wizard) {

    PanelDecorator.addCancelApply(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Apply button starts off enabled
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.APPLY, true);

  }

  @Override
  public void afterShow() {

    blockExplorerComboBox.requestFocusInWindow();

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    if (!isExitCancel) {

      // Switch the main configuration over to the new one
      Configurations.switchConfiguration(getWizardModel().getConfiguration());

    }

    // Must be OK to proceed
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // Do nothing

  }

  /**
   * <p>Handle one of the combo boxes changing</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    JComboBox source = (JComboBox) e.getSource();

    // Block explorer
    if (ComboBoxes.BLOCK_EXPLORER_COMMAND.equalsIgnoreCase(e.getActionCommand())) {

      int blockExplorerIndex = source.getSelectedIndex();

      if (0 <= blockExplorerIndex && blockExplorerIndex < BlockExplorers.getAll().size()) {
        BlockExplorer blockExplorer = BlockExplorers.getAll().get(blockExplorerIndex);

        Configuration configuration = getWizardModel().getConfiguration();
        configuration.getAppearance().setBlockExplorerId(blockExplorer.getId());

        // Update the model
        getWizardModel().setConfiguration(configuration);
      }

    }
  }

}
