package org.multibit.hd.ui.views.wizards.welcome.create_hardware_wallet;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.brit.core.seed_phrase.SeedPhraseSize;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.ComboBoxes;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Create wallet: Enter details (label, seed phrase etc)</li>
 * </ul>
 *
 * @since 0.0.5
 *  
 */
public class CreateHardwareWalletEnterDetailsPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> implements ActionListener {

  private JTextField trezorLabel;
  private JComboBox<String> seedSize;

  /**
   * @param wizard The wizard managing the states
   */
  public CreateHardwareWalletEnterDetailsPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.EDIT, MessageKey.CREATE_HARDWARE_WALLET_ENTER_DETAILS_TITLE);

  }

  @Override
  public void newPanelModel() {

    // Nothing to bind

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]10[]" // Row constraints
      ));

    trezorLabel = TextBoxes.newEnterTrezorLabel();
    seedSize = ComboBoxes.newSeedSizeComboBox(this);

    contentPanel.add(Labels.newEnterTrezorLabel(),"shrink");
    contentPanel.add(trezorLabel, MultiBitUI.TREZOR_DISPLAY_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newSeedSize(), "shrink");
    contentPanel.add(seedSize, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelPreviousNext(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Initialise with "Unlock" enabled to allow users to choose defaults
    ViewEvents.fireWizardButtonEnabledEvent(
      getPanelName(),
      WizardButton.NEXT,
      true
    );

  }

  @Override
  public void afterShow() {

    trezorLabel.setText(getWizardModel().getTrezorWalletLabel());
    trezorLabel.selectAll();
    trezorLabel.requestFocusInWindow();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    getWizardModel().setTrezorWalletLabel(trezorLabel.getText());
    getWizardModel().setTrezorSeedPhraseSize(SeedPhraseSize.fromOrdinal(seedSize.getSelectedIndex()));

  }

  /**
   * <p>Handle the "change seed phrase size" action event</p>
   *
   * @param e The action event
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    // Do nothing - we update on Next

  }

}