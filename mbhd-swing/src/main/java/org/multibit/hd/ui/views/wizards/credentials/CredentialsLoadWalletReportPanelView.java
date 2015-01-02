package org.multibit.hd.ui.views.wizards.credentials;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.events.WalletLoadEvent;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.LabelDecorator;
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
 * <li>Show result of attempting to load a wallet</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class CredentialsLoadWalletReportPanelView extends AbstractWizardPanelView<CredentialsWizardModel, String> {

  private static final Logger log = LoggerFactory.getLogger(CredentialsLoadWalletReportPanelView.class);

  // The status of whether the wallet loaded ok
  private JLabel walletLoadedStatusLabel;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public CredentialsLoadWalletReportPanelView(AbstractWizard<CredentialsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.LOAD_WALLET_REPORT_TITLE, AwesomeIcon.FILE_TEXT);

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

    // Initialise to loading
    walletLoadedStatusLabel = Labels.newCoreStatusLabel(Optional.of(CoreMessageKey.WALLET_LOADING), new Object[0], Optional.<Boolean>absent());

    // Make all labels visible initially
    walletLoadedStatusLabel.setVisible(true);

    contentPanel.add(walletLoadedStatusLabel, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<CredentialsWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void fireInitialStateViewEvents() {

    // Disable the finish button
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Override
  public boolean beforeShow() {
//    SwingUtilities.invokeLater(
//            new Runnable() {
//              @Override
//              public void run() {
//
//                LabelDecorator.applyWrappingLabel(walletLoadedStatusLabel, Languages.safeText(CoreMessageKey.WALLET_LOADING));
//              }
//            });
    return true;
  }

  @Override
  public void afterShow() {
    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                getFinishButton().requestFocusInWindow();

                // Check for report message from hardware wallet
//                LabelDecorator.applyReportMessage(walletLoadedStatusLabel, getWizardModel().getReportMessageKey(), getWizardModel().getReportMessageStatus());
//
//                if (getWizardModel().getReportMessageKey().isPresent() && !getWizardModel().getReportMessageStatus()) {
//                  // Hardware wallet report indicates cancellation
//                  // TODO
//                  log.debug("Cancel from hardware wallet");
//                } else {
//                  log.debug("Load is progressing");
//                }

              }
            });
  }

  @Subscribe
  public void onWalletLoadEvent(final WalletLoadEvent walletLoadEvent) {
    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                log.debug("Saw a wallet load event {}", walletLoadEvent);
                if (walletLoadEvent.isWalletLoadWasSuccessful()) {
                  // Wallet loaded ok
                  LabelDecorator.applyWrappingLabel(walletLoadedStatusLabel, Languages.safeText(CoreMessageKey.WALLET_LOADED_OK));
                  LabelDecorator.applyStatusLabel(walletLoadedStatusLabel, Optional.of(Boolean.TRUE));
                } else {
                  // Wallet failed to load
                  LabelDecorator.applyWrappingLabel(walletLoadedStatusLabel, Languages.safeText(CoreMessageKey.WALLET_FAILED_TO_LOAD));
                  LabelDecorator.applyStatusLabel(walletLoadedStatusLabel, Optional.of(Boolean.FALSE));
                }
              }
            });
  }
}
