package org.multibit.hd.ui.views.wizards.welcome.create_trezor_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;

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
public class CreateTrezorWalletReportPanelView extends AbstractWizardPanelView<WelcomeWizardModel, String> {

  // View
  private JLabel reportStatusLabel;

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

    contentPanel.setLayout(new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]" // Row constraints
      ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Provide an empty status label (populated after show)
    reportStatusLabel = Labels.newStatusLabel(Optional.of(MessageKey.TREZOR_FAILURE_OPERATION), null, Optional.<Boolean>absent());
    reportStatusLabel.setVisible(false);

    contentPanel.add(reportStatusLabel, "aligny top,wrap");

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

    // Use the outcome from the previous operations to decorate the existing status label
    final MessageKey reportMessageKey = getWizardModel().getReportMessageKey();
    reportStatusLabel.setText(Languages.safeText(reportMessageKey));
    AccessibilityDecorator.apply(
      reportStatusLabel,
      reportMessageKey
    );
    Labels.decorateStatusLabel(
      reportStatusLabel,
      Optional.of(getWizardModel().isReportMessageStatus())
    );
    reportStatusLabel.setVisible(true);

    return true;
  }

}
