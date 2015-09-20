package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractHardwareWalletWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Use Trezor progress report</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class UseTrezorReportPanelView extends AbstractWizardPanelView<UseTrezorWizardModel, Boolean> {

  // View
  private JLabel reportStatusLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public UseTrezorReportPanelView(AbstractHardwareWalletWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.FILE_TEXT, MessageKey.USE_HARDWARE_REPORT_TITLE, null);

  }

  @Override
  public void newPanelModel() {

    // No need to bind this to the wizard model

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Provide an empty status label (populated after show)
    reportStatusLabel = Labels.newStatusLabel(Optional.of(MessageKey.HARDWARE_FAILURE_OPERATION), null, Optional.<Boolean>absent());
    reportStatusLabel.setVisible(false);

    contentPanel.add(reportStatusLabel, "aligny top,wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {
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
    LabelDecorator.applyReportMessage(
      reportStatusLabel,
      getWizardModel().getReportMessageKey(),
      null,
      getWizardModel().getReportMessageStatus()
    );

    return true;
  }

}
