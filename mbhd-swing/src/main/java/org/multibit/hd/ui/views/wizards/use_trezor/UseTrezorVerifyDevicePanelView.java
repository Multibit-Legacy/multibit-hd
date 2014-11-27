package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ComponentChangedEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.ViewKey;
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
public class UseTrezorVerifyDevicePanelView extends AbstractWizardPanelView<UseTrezorWizardModel, UseTrezorVerifyDevicePanelModel> {

  private JLabel trezorCommunicationsStatusLabel;

  private JTextArea featuresTextArea;

  /**
   * @param wizard The wizard managing the states
   */
  public UseTrezorVerifyDevicePanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.VERIFY_DEVICE_TITLE, AwesomeIcon.MEDKIT);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    UseTrezorVerifyDevicePanelModel panelModel = new UseTrezorVerifyDevicePanelModel(
            getPanelName()
    );
    setPanelModel(panelModel);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    // Register for the high level hardware wallet events
    HardwareWalletService.hardwareWalletEventBus.register(this);

    contentPanel.setLayout(new MigLayout(
            Panels.migXYLayout(),
            "[][][]", // Column constraints
            "[]10[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Trezor communications status label
    trezorCommunicationsStatusLabel = Labels.newStatusLabel(
            Optional.of(MessageKey.COMMUNICATING_WITH_TREZOR_OPERATION),
            null,
            Optional.<Boolean>absent());
    AccessibilityDecorator.apply(trezorCommunicationsStatusLabel, MessageKey.COMMUNICATING_WITH_TREZOR_OPERATION);

    contentPanel.add(trezorCommunicationsStatusLabel, "wrap");
    // The Trezor features is a wall of text so needs scroll bars
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
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    // Ensure the Finish button is enabled - user can dismiss the Verify screen at will
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

    // Hide the header balance
    ViewEvents.fireViewChangedEvent(ViewKey.HEADER, false);

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
            trezorCommunicationsStatusLabel.setText(Languages.safeText(MessageKey.TREZOR_FOUND));
            AccessibilityDecorator.apply(trezorCommunicationsStatusLabel, MessageKey.TREZOR_FOUND);

            featuresTextArea.setText(optionalFeatures.get().toString());
          } else {
            // No features
            trezorCommunicationsStatusLabel.setText(Languages.safeText(MessageKey.NO_TREZOR_FOUND));
            AccessibilityDecorator.apply(trezorCommunicationsStatusLabel, MessageKey.NO_TREZOR_FOUND);
          }

        }
      });

    }

  }
}
