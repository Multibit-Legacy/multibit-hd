package org.multibit.hd.ui.views.wizards.use_hardware_wallet;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show report wallet progress report</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class UseHardwareWalletVerifyDevicePanelView extends AbstractWizardPanelView<UseHardwareWalletWizardModel, UseHardwareWalletVerifyDevicePanelModel> {

  private JLabel hardwareCommunicationsStatusLabel;

  private JTextArea featuresTextArea;

  /**
   * @param wizard The wizard managing the states
   */
  public UseHardwareWalletVerifyDevicePanelView(AbstractWizard<UseHardwareWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.MEDKIT, MessageKey.HARDWARE_VERIFY_DEVICE_TITLE, wizard.getWizardModel().getWalletMode().brand());

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    UseHardwareWalletVerifyDevicePanelModel panelModel = new UseHardwareWalletVerifyDevicePanelModel(
            getPanelName()
    );
    setPanelModel(panelModel);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
            Panels.migXYLayout(),
            "[][][]", // Column constraints
            "[]10[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Hardware communications status label
    hardwareCommunicationsStatusLabel = Labels.newStatusLabel(
            Optional.of(MessageKey.COMMUNICATING_WITH_HARDWARE_OPERATION),
            new Object[] {getWizardModel().getWalletMode().brand()},
            Optional.<Boolean>absent());
    AccessibilityDecorator.apply(hardwareCommunicationsStatusLabel, MessageKey.COMMUNICATING_WITH_HARDWARE_OPERATION);
    contentPanel.add(hardwareCommunicationsStatusLabel, "wrap");

    // The hardware wallet features is a wall of text so needs scroll bars
    featuresTextArea = TextBoxes.newReadOnlyTextArea(10, 80);
    featuresTextArea.setBorder(null);
    featuresTextArea.setText("");
    featuresTextArea.setCaretPosition(0);

    // Raw transaction requires its own scroll pane
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setOpaque(true);
    scrollPane.setBackground(Themes.currentTheme.readOnlyBackground());
    scrollPane.setBorder(null);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // View port requires special handling
    scrollPane.setViewportView(featuresTextArea);
    scrollPane.getViewport().setBackground(Themes.currentTheme.readOnlyBackground());
    scrollPane.setViewportBorder(new TextBubbleBorder(Themes.currentTheme.readOnlyBorder()));

    // Ensure we maintain the overall theme (no vertical since we're using rounded border)
    ScrollBarUIDecorator.apply(scrollPane, false);

    contentPanel.add(scrollPane, "grow,push,span 2," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseHardwareWalletWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    // Ensure the Finish button is enabled - user can dismiss the Verify screen at will
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

    // Ask the Trezor for features
    getWizardModel().requestFeatures();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

  }


  @Subscribe
  public void onComponentChangedEvent(final ComponentChangedEvent event) {

    if (getPanelName().equals(event.getPanelName())) {

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          // Put the features into the text area if available
          Optional<Features> optionalFeatures = getWizardModel().getFeaturesOptional();
          if (optionalFeatures.isPresent()) {
            // Got features ok
            hardwareCommunicationsStatusLabel.setText(Languages.safeText(MessageKey.HARDWARE_FOUND, getWizardModel().getWalletMode().brand()));
            AccessibilityDecorator.apply(hardwareCommunicationsStatusLabel, MessageKey.HARDWARE_FOUND);

            featuresTextArea.setText(optionalFeatures.get().toString());
          } else {
            // No features
            hardwareCommunicationsStatusLabel.setText(Languages.safeText(MessageKey.NO_HARDWARE_FOUND, getWizardModel().getWalletMode().brand()));
            AccessibilityDecorator.apply(hardwareCommunicationsStatusLabel, MessageKey.NO_HARDWARE_FOUND);
          }

        }
      });

    }

  }
}
