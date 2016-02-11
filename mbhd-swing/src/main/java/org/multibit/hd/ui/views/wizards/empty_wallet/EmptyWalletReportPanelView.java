package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.events.BitcoinSendProgressEvent;
import org.multibit.hd.core.events.BitcoinSendingEvent;
import org.multibit.hd.core.events.BitcoinSentEvent;
import org.multibit.hd.core.events.TransactionCreationEvent;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.ViewKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
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
 * <li>Show empty wallet progress report</li>
 * </ul>
 * <p/>
 * <p>Much of this code is lifted straight from SendBitcoinReportPanelView</p>
 *
 * @since 0.0.1
 */
public class EmptyWalletReportPanelView extends AbstractWizardPanelView<EmptyWalletWizardModel, String> {

  private static final Logger log = LoggerFactory.getLogger(EmptyWalletReportPanelView.class);

  private JLabel transactionConstructionStatusSummary;
  private JLabel transactionConstructionStatusDetail;

  private JLabel transactionBroadcastStatusSummary;
  private JLabel transactionBroadcastStatusDetail;

  private JLabel reportStatusLabel;

  private TransactionCreationEvent lastTransactionCreationEvent;
  private BitcoinSendingEvent lastBitcoinSendingEvent;
  private BitcoinSentEvent lastBitcoinSentEvent;
  private BitcoinSendProgressEvent lastBitcoinSendProgressEvent;

  private boolean initialised = false;

  /**
   * @param wizard The wizard managing the states
   */
  public EmptyWalletReportPanelView(AbstractWizard<EmptyWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.FIRE, MessageKey.EMPTY_WALLET_PROGRESS_TITLE);

  }

  @Override
  public void newPanelModel() {
    lastTransactionCreationEvent = null;
    lastBitcoinSendingEvent = null;
    lastBitcoinSendProgressEvent = null;
    lastBitcoinSentEvent = null;
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {
    contentPanel.setLayout(
            new MigLayout(
                    Panels.migXYLayout(),
                    "[][][]", // Column constraints
                    "10[24]10[24]15[24]10[24]15[24]10" // Row constraints
            ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    transactionConstructionStatusSummary = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    AccessibilityDecorator.apply(transactionConstructionStatusSummary, MessageKey.TRANSACTION_CONSTRUCTION_STATUS_SUMMARY);

    transactionConstructionStatusDetail = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    AccessibilityDecorator.apply(transactionConstructionStatusDetail, MessageKey.TRANSACTION_CONSTRUCTION_STATUS_DETAIL);

    transactionBroadcastStatusSummary = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    AccessibilityDecorator.apply(transactionBroadcastStatusSummary, MessageKey.TRANSACTION_BROADCAST_STATUS_SUMMARY);

    transactionBroadcastStatusDetail = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    AccessibilityDecorator.apply(transactionBroadcastStatusDetail, MessageKey.TRANSACTION_BROADCAST_STATUS_DETAIL);

    // Provide an empty status label (populated after show)
    reportStatusLabel = Labels.newStatusLabel(Optional.of(MessageKey.HARDWARE_FAILURE_OPERATION), new Object[] { getWizardModel().getWalletMode().brand()}, Optional.<Boolean>absent());
    reportStatusLabel.setVisible(false);

    contentPanel.add(reportStatusLabel, "aligny top,wrap");

    // Ensure the labels wrap if the error messages are too wide
    contentPanel.add(transactionConstructionStatusSummary, "grow,push," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(transactionConstructionStatusDetail, "grow,push," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(transactionBroadcastStatusSummary, "grow,push," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(transactionBroadcastStatusDetail, "grow,push," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    initialised = true;
  }

  @Override
  protected void initialiseButtons(AbstractWizard<EmptyWalletWizardModel> wizard) {
    PanelDecorator.addFinish(this, wizard);
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, false);
  }

  @Override
  public boolean beforeShow() {
    LabelDecorator.applyWrappingLabel(transactionConstructionStatusSummary, Languages.safeText(CoreMessageKey.CHANGE_PASSWORD_WORKING));
    transactionConstructionStatusDetail.setText("");
    transactionBroadcastStatusSummary.setText("");
    transactionBroadcastStatusDetail.setText("");

    // Ensure the header is switched off whilst the send is in progress
    ViewEvents.fireViewChangedEvent(ViewKey.HEADER, false);

    return true;
  }

  @Override
  public void afterShow() {

    // Check for report message from hardware wallet
    LabelDecorator.applyReportMessage(reportStatusLabel, getWizardModel().getReportMessageKey(), null, getWizardModel().getReportMessageStatus());

    if (getWizardModel().getReportMessageKey().isPresent() && !getWizardModel().getReportMessageStatus()) {
      // Hardware wallet report indicates cancellation
      transactionConstructionStatusSummary.setVisible(false);
      transactionConstructionStatusDetail.setVisible(false);

      ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

      // Switch header back to regular visibility
      switchHeaderOn();
    } else {
      // Transaction must be progressing in some manner
      if (lastTransactionCreationEvent != null) {
        onTransactionCreationEvent(lastTransactionCreationEvent);
        lastTransactionCreationEvent = null;
      }

      if (lastBitcoinSendingEvent != null) {
        onBitcoinSendingEvent(lastBitcoinSendingEvent);
        lastBitcoinSendingEvent = null;
      }
      if (lastBitcoinSendProgressEvent != null) {
        onBitcoinSendProgressEvent(lastBitcoinSendProgressEvent);
        lastBitcoinSendProgressEvent = null;
      }

      if (lastBitcoinSentEvent != null) {
        onBitcoinSentEvent(lastBitcoinSentEvent);
        lastBitcoinSentEvent = null;
      }
    }
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Subscribe
  public void onTransactionCreationEvent(final TransactionCreationEvent transactionCreationEvent) {

    log.debug("Received the TransactionCreationEvent: " + transactionCreationEvent.toString());

    lastTransactionCreationEvent = transactionCreationEvent;

    // The event may be fired before the UI has initialised
    if (!initialised) {
      return;
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (transactionCreationEvent.isTransactionCreationWasSuccessful()) {
          LabelDecorator.applyWrappingLabel(transactionConstructionStatusSummary, Languages.safeText(CoreMessageKey.TRANSACTION_CREATED_OK));
          transactionConstructionStatusDetail.setText("");
          LabelDecorator.applyStatusIcon(transactionConstructionStatusSummary, Optional.of(Boolean.TRUE));
        } else {
          String detailMessage = Languages.safeText(
                  transactionCreationEvent.getTransactionCreationFailureReasonKey(),
                  (Object[]) transactionCreationEvent.getTransactionCreationFailureReasonData()
          );
          LabelDecorator.applyWrappingLabel(transactionConstructionStatusSummary, Languages.safeText(CoreMessageKey.TRANSACTION_CREATION_FAILED));
          LabelDecorator.applyWrappingLabel(transactionConstructionStatusDetail, detailMessage);
          LabelDecorator.applyStatusIcon(transactionConstructionStatusSummary, Optional.of(Boolean.FALSE));

          ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);
          switchHeaderOn();
        }
      }
    });
  }

  @Subscribe
  public void onBitcoinSendingEvent(final BitcoinSendingEvent bitcoinSendingEvent) {
    log.debug("Received the BitcoinSendingEvent: " + bitcoinSendingEvent);

    lastBitcoinSendingEvent = bitcoinSendingEvent;

    // The event may be fired before the UI has initialised
    if (!initialised) {
      return;
    }

    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                LabelDecorator.applyWrappingLabel(transactionBroadcastStatusSummary, Languages.safeText(CoreMessageKey.SENDING_BITCOIN));
                AwesomeDecorator.bindIcon(AwesomeIcon.BULLHORN, transactionBroadcastStatusSummary, true, MultiBitUI.NORMAL_ICON_SIZE);
              }
            });
  }


  @Subscribe
  public void onBitcoinSendProgressEvent(final BitcoinSendProgressEvent bitcoinSendProgressEvent) {
    log.debug("Received the BitcoinSendProgressEvent: " + bitcoinSendProgressEvent);

    lastBitcoinSendProgressEvent = bitcoinSendProgressEvent;

    // The event may be fired before the UI has initialised
    if (!initialised) {
      return;
    }

    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                double progress = bitcoinSendProgressEvent.getProgress();

                if (0 < progress && progress < 0.4) {
                  // bullhorn-quarter
                  Icon icon = Images.newBullhornQuarterIcon();
                  transactionBroadcastStatusSummary.setIcon(icon);
                } else {
                  if (0.4 <= progress && progress < 0.6) {
                    // bullhorn-half
                    Icon icon = Images.newBullhornHalfIcon();
                    transactionBroadcastStatusSummary.setIcon(icon);
                  } else {
                    if (0.6 <= progress && progress < 1.0) {
                      // bullhorn-three-quarters
                      Icon icon = Images.newBullhornThreeQuartersIcon();
                      transactionBroadcastStatusSummary.setIcon(icon);
                    }
                  }
                }
              }

            });
  }

  @Subscribe
  public void onBitcoinSentEvent(final BitcoinSentEvent bitcoinSentEvent) {

    log.debug("Received the BitcoinSentEvent: " + bitcoinSentEvent.toString());

    lastBitcoinSentEvent = bitcoinSentEvent;
    // The event may be fired before the UI has initialised
    if (!initialised) {
      return;
    }

    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                // Enable the finish button once the transaction is sent
                ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);

                if (bitcoinSentEvent.isSendWasSuccessful()) {
                  LabelDecorator.applyWrappingLabel(transactionBroadcastStatusSummary, Languages.safeText(CoreMessageKey.BITCOIN_SENT_OK));
                  LabelDecorator.applyStatusIcon(transactionBroadcastStatusSummary, Optional.of(Boolean.TRUE));
                } else {
                  String summaryMessage = Languages.safeText(CoreMessageKey.BITCOIN_SEND_FAILED);
                  String detailMessage = Languages.safeText(bitcoinSentEvent.getSendFailureReason(), (Object[]) bitcoinSentEvent.getSendFailureReasonData());
                  LabelDecorator.applyWrappingLabel(transactionBroadcastStatusSummary, summaryMessage);
                  LabelDecorator.applyWrappingLabel(transactionBroadcastStatusDetail, detailMessage);
                  LabelDecorator.applyStatusIcon(transactionBroadcastStatusSummary, Optional.of(Boolean.FALSE));
                }

                switchHeaderOn();
              }
            });
  }

  private void switchHeaderOn() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        final boolean viewHeader = Configurations.currentConfiguration.getAppearance().isShowBalance();
        log.debug("Firing event to header viewable to:  {}", viewHeader);
        ViewEvents.fireBalanceChangedEvent(
                WalletManager.INSTANCE.getCurrentWalletBalance().get(),
                WalletManager.INSTANCE.getCurrentWalletBalanceWithUnconfirmed().get(),
                null,
                Optional.<String>absent());
        ViewEvents.fireViewChangedEvent(ViewKey.HEADER, viewHeader);
      }
    });
  }
}
