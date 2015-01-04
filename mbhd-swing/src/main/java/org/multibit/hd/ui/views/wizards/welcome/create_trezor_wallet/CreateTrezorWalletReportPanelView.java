package org.multibit.hd.ui.views.wizards.welcome.create_trezor_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.hardware.core.HardwareWalletService;
import org.multibit.hd.hardware.core.fsm.HardwareWalletContext;
import org.multibit.hd.hardware.core.messages.Features;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to create a Trezor wallet</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class CreateTrezorWalletReportPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> {

  private static final Logger log = LoggerFactory.getLogger(CreateTrezorWalletReportPanelView.class);

  // View
  private JLabel reportStatusLabel;

  private JLabel timestampLabel;
  private JTextField timestampText;
  private JLabel timestampNote;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public CreateTrezorWalletReportPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.USE_TREZOR_REPORT_TITLE, AwesomeIcon.FILE_TEXT);

  }

  @Override
  public void newPanelModel() {

    // No need to bind this to the wizard model

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[][]", // Column constraints
        "[][]" // Row constraints
      ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Provide an empty status label (populated after show)
    reportStatusLabel = Labels.newStatusLabel(Optional.of(MessageKey.TREZOR_FAILURE_OPERATION), null, Optional.<Boolean>absent());
    reportStatusLabel.setVisible(false);

    timestampLabel = Labels.newTimestamp();
    timestampText = TextBoxes.newReadOnlyTextField(10);
    timestampNote = Labels.newTimestampNote();

    // Start off invisible in case wallet creation fails
    timestampLabel.setVisible(false);
    timestampText.setVisible(false);
    timestampNote.setVisible(false);

    contentPanel.add(reportStatusLabel, "span 2,aligny top,wrap");

    contentPanel.add(timestampLabel, "shrink");
    contentPanel.add(timestampText, "shrink,wrap");
    contentPanel.add(timestampNote, "span 2,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {
    PanelDecorator.addFinish(this, wizard);
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is read only
  }

  @Override
  public boolean beforeShow() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on EDT");

    // Check for report message from hardware wallet
    LabelDecorator.applyReportMessage(reportStatusLabel, getWizardModel().getReportMessageKey(), getWizardModel().getReportMessageStatus());

    if (getWizardModel().getReportMessageStatus()) {
      String nowTimestamp = Dates.newSeedTimestamp();
      log.debug("The timestamp for the new wallet is {}", nowTimestamp);
      timestampText.setText(nowTimestamp);

      // Remember the timestamp and walletId for use later
      // (specifically for setting the fast catch up when this wallet first syncs when it is opened)
      Configuration currentConfiguration = Configurations.currentConfiguration;

      Optional<HardwareWalletService> hardwareWalletService = CoreServices.getOrCreateHardwareWalletService();
      if (hardwareWalletService.isPresent()) {
        HardwareWalletContext context = hardwareWalletService.get().getContext();

        if (currentConfiguration != null
          && currentConfiguration.getWallet() != null
          && context != null) {

          Optional<Features> features = context.getFeatures();
          if (features.isPresent()) {
            long now = System.currentTimeMillis();
            Configurations.currentConfiguration.getWallet().setRecentWalletDataValidity(now);
            Configurations.currentConfiguration.getWallet().setRecentWalletLabel(getWizardModel().getTrezorWalletLabel());
            log.debug("Saving for wallet with label '{}' the data validity time '{}'", getWizardModel().getTrezorWalletLabel(), now);
          }

        }
      }
      timestampLabel.setVisible(true);
      timestampText.setVisible(true);
      timestampNote.setVisible(true);
    }

    return true;
  }

}
