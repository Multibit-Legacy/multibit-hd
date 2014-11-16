package org.multibit.hd.ui.views.wizards.use_trezor;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.concurrent.SafeExecutors;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.events.CoreEvents;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;

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

  private JLabel trezorWalletStatus;

  private ListeningExecutorService listeningExecutorService;

  boolean decryptHasBeenRequested = false;
  private boolean status;

  /**
   * @param wizard The wizard managing the states
   */
  public UseTrezorReportPanelView(AbstractWizard<UseTrezorWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.USE_TREZOR_REPORT_TITLE, AwesomeIcon.MAGIC);

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
        "[][][]", // Column constraints
        "[]10[]10[]" // Row constraints
      ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    trezorWalletStatus = Labels.newCoreStatusLabel(Optional.of(CoreMessageKey.CHANGE_PASSWORD_WORKING), null, Optional.<Boolean>absent());

    contentPanel.add(trezorWalletStatus, "wrap");

    listeningExecutorService = SafeExecutors.newSingleThreadExecutor("decrypt-trezor-wallet");

    decryptHasBeenRequested = false;

  }

  @Override
  protected void initialiseButtons(AbstractWizard<UseTrezorWizardModel> wizard) {

    PanelDecorator.addCancelFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    // Run the decryption on a different thread
    listeningExecutorService.submit(
      new Runnable() {
        @Override
        public void run() {

          // Enable the Finish button
          ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

        }
      });

  }

  public boolean beforeHide(boolean isExitCancel) {

    // Update everything
    // TODO- make more specific to only fire when new wallet has been switched to
    CoreEvents.fireConfigurationChangedEvent();
    return true;

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  public void setStatus(boolean status) {

    if (status) {

      Languages.safeText(MessageKey.USE_TREZOR_REPORT_MESSAGE_SUCCESS, true);
      AccessibilityDecorator.apply(trezorWalletStatus, MessageKey.USE_TREZOR_REPORT_MESSAGE_SUCCESS);
      AwesomeDecorator.applyIcon(AwesomeIcon.CHECK, trezorWalletStatus, true, MultiBitUI.NORMAL_ICON_SIZE);
    }

  }
}
