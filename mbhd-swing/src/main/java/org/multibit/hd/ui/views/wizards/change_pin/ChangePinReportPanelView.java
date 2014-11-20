package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.ChangePasswordResultEvent;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.events.view.ViewEvents;
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
import org.multibit.hd.ui.views.wizards.WizardButton;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to change the credentials of a wallet</li>
 * </ul>
 *
 * @since 0.0.5
 *
 */
public class ChangePinReportPanelView extends AbstractWizardPanelView<ChangePinWizardModel, String> {

  // View
  private JLabel passwordChangedStatusLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public ChangePinReportPanelView(AbstractWizard<ChangePinWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.CHANGE_PASSWORD_TITLE, AwesomeIcon.FILE_TEXT);

  }

  @Override
  public void newPanelModel() {

    String model = "TODO replace with a proper model";
    setPanelModel(model);

    // No need to bind this to the wizard model

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

    // Initialise to failure
    passwordChangedStatusLabel = Labels.newPasswordChangedStatus();
    AccessibilityDecorator.apply(passwordChangedStatusLabel, MessageKey.PASSWORD_CHANGED_STATUS);

    contentPanel.add(passwordChangedStatusLabel, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ChangePinWizardModel> wizard) {
    PanelDecorator.addFinish(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {

    // Disable the finish button while the process is occurring
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, false);

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Override
  public boolean beforeShow() {

    ChangePinWizardModel model = getWizardModel();
    String oldPassword = model.getCurrentPin();
    String newPassword = model.getNewPin();

    Optional<WalletSummary> walletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

    passwordChangedStatusLabel.setText(Languages.safeText(CoreMessageKey.CHANGE_PASSWORD_WORKING));
    if (walletSummaryOptional.isPresent()) {
      WalletSummary walletSummary = walletSummaryOptional.get();
      // Change the wallet credentials.
      // The result of the credentials change is emitted as a ChangePasswordResultEvent

      WalletService.changeWalletPassword(walletSummary, oldPassword, newPassword);
    }

    return true;
  }

  @Subscribe
  public void onChangePasswordResultEvent(final ChangePasswordResultEvent changePasswordResultEvent) {

    // Enable and focus the finish button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        passwordChangedStatusLabel.setText(Languages.safeText(changePasswordResultEvent.getChangePasswordResultKey(), changePasswordResultEvent.getChangePasswordResultData()));
        Labels.decorateStatusLabel(passwordChangedStatusLabel, Optional.of(changePasswordResultEvent.isChangePasswordWasSuccessful()));
        getFinishButton().requestFocusInWindow();
      }
    });

  }
}
