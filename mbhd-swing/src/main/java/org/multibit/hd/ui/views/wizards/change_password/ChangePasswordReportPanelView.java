package org.multibit.hd.ui.views.wizards.change_password;

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
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show result of attempting to change the password of a wallet</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ChangePasswordReportPanelView extends AbstractWizardPanelView<ChangePasswordWizardModel, String> {

  private static final Logger log = LoggerFactory.getLogger(ChangePasswordReportPanelView.class);

  // View
  private JLabel passwordChangedStatusLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public ChangePasswordReportPanelView(AbstractWizard<ChangePasswordWizardModel> wizard, String panelName) {

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

    contentPanel.add(passwordChangedStatusLabel, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<ChangePasswordWizardModel> wizard) {
    PanelDecorator.addFinish(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {
    // Enable the finish button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Override
  public boolean beforeShow() {

    ChangePasswordWizardModel model = getWizardModel();
    String oldPassword = model.getEnteredPassword();
    String newPassword = model.getConfirmedPassword();

    Optional<WalletSummary> walletSummaryOptional = WalletManager.INSTANCE.getCurrentWalletSummary();

    passwordChangedStatusLabel.setText(Languages.safeText(CoreMessageKey.CHANGE_PASSWORD_WORKING));
    if (walletSummaryOptional.isPresent()) {
      WalletSummary walletSummary = walletSummaryOptional.get();
      // Change the wallet password.
      // The result of the password change is emitted as a ChangePasswordResultEvent

      WalletService.changeWalletPassword(walletSummary, oldPassword, newPassword);
    }

    return true;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getFinishButton().requestFocusInWindow();
      }
    });
  }

  @Subscribe
  public void onChangePasswordResultEvent(ChangePasswordResultEvent changePasswordResultEvent) {
    passwordChangedStatusLabel.setText(Languages.safeText(changePasswordResultEvent.getChangePasswordResultKey(), changePasswordResultEvent.getChangePasswordResultData()));
    Labels.decorateStatusLabel(passwordChangedStatusLabel, Optional.of(changePasswordResultEvent.isChangePasswordWasSuccessful()));
  }
}
