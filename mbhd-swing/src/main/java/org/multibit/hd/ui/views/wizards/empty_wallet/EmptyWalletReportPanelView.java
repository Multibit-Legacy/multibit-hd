package org.multibit.hd.ui.views.wizards.empty_wallet;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.PaymentStatus;
import org.multibit.hd.core.events.BitcoinSentEvent;
import org.multibit.hd.core.events.TransactionCreationEvent;
import org.multibit.hd.core.events.TransactionSeenEvent;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.LabelDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show empty wallet progress report</li>
 * </ul>
 *
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

  private JLabel transactionConfirmationStatus;

  private JLabel reportStatusLabel;

  private TransactionCreationEvent lastTransactionCreationEvent;
  private BitcoinSentEvent lastBitcoinSentEvent;
  private TransactionSeenEvent lastTransactionSeenEvent;

  private boolean initialised = false;

  // The current transaction ID
  private String currentTransactionId;

  /**
   * @param wizard The wizard managing the states
   */
  public EmptyWalletReportPanelView(AbstractWizard<EmptyWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.EMPTY_WALLET_PROGRESS_TITLE, AwesomeIcon.FIRE);

  }

  @Override
  public void newPanelModel() {

    lastTransactionCreationEvent = null;
    lastBitcoinSentEvent = null;
    lastTransactionSeenEvent = null;

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

    transactionConfirmationStatus = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    AccessibilityDecorator.apply(transactionConfirmationStatus, MessageKey.TRANSACTION_CONFIRMATION_STATUS);

    // Provide an empty status label (populated after show)
    reportStatusLabel = Labels.newStatusLabel(Optional.of(MessageKey.TREZOR_FAILURE_OPERATION), null, Optional.<Boolean>absent());
    reportStatusLabel.setVisible(false);

    contentPanel.add(reportStatusLabel, "aligny top,wrap");

    // Ensure the labels wrap if the error messages are too wide
    contentPanel.add(transactionConstructionStatusSummary, "grow,push," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(transactionConstructionStatusDetail, "grow,push," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(transactionBroadcastStatusSummary, "grow,push," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(transactionBroadcastStatusDetail, "grow,push," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");
    contentPanel.add(transactionConfirmationStatus, "grow,push," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    initialised = true;
  }

  @Override
  protected void initialiseButtons(AbstractWizard<EmptyWalletWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }


  @Override
  public boolean beforeShow() {
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          LabelDecorator.applyWrappingLabel(transactionConstructionStatusSummary, Languages.safeText(CoreMessageKey.CHANGE_PASSWORD_WORKING));
          transactionConstructionStatusDetail.setText("");
          transactionBroadcastStatusSummary.setText("");
          transactionBroadcastStatusDetail.setText("");
          transactionConfirmationStatus.setText("");
        }
      });
    return true;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {

          // Check for report message from hardware wallet
          LabelDecorator.applyReportMessage(reportStatusLabel, getWizardModel().getReportMessageKey(), getWizardModel().getReportMessageStatus());

          if (getWizardModel().getReportMessageKey().isPresent() && !getWizardModel().getReportMessageStatus()) {
            // Hardware wallet report indicates cancellation
            transactionConstructionStatusSummary.setVisible(false);
            transactionConstructionStatusDetail.setVisible(false);
          } else {
            // Transaction must be progressing in some manner
            if (lastTransactionCreationEvent != null) {
              onTransactionCreationEvent(lastTransactionCreationEvent);
              lastTransactionCreationEvent = null;
            }
            if (lastBitcoinSentEvent != null) {
              onBitcoinSentEvent(lastBitcoinSentEvent);
              lastBitcoinSentEvent = null;
            }
            if (lastTransactionSeenEvent != null) {
              onTransactionSeenEvent(lastTransactionSeenEvent);
              lastTransactionSeenEvent = null;
            }
          }
        }
      });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  @Subscribe
  public void onTransactionCreationEvent(TransactionCreationEvent transactionCreationEvent) {

    log.debug("Received the TransactionCreationEvent: " + transactionCreationEvent.toString());

    lastTransactionCreationEvent = transactionCreationEvent;

    // The event may be fired before the UI has initialised
    if (!initialised) {
      return;
    }

    if (transactionCreationEvent.isTransactionCreationWasSuccessful()) {
      // We now have a transactionId so keep that in the panel model for filtering TransactionSeenEvents later
      currentTransactionId = transactionCreationEvent.getTransactionId();

      LabelDecorator.applyWrappingLabel(transactionConstructionStatusSummary, Languages.safeText(CoreMessageKey.TRANSACTION_CREATED_OK));
      transactionConstructionStatusDetail.setText("");
      LabelDecorator.applyStatusLabel(transactionConstructionStatusSummary, Optional.of(Boolean.TRUE));
    } else {
      String detailMessage = Languages.safeText(
        transactionCreationEvent.getTransactionCreationFailureReasonKey(),
        (Object[]) transactionCreationEvent.getTransactionCreationFailureReasonData()
      );
      LabelDecorator.applyWrappingLabel(transactionConstructionStatusSummary, Languages.safeText(CoreMessageKey.TRANSACTION_CREATION_FAILED));
      LabelDecorator.applyWrappingLabel(transactionConstructionStatusDetail, detailMessage);
      LabelDecorator.applyStatusLabel(transactionConstructionStatusSummary, Optional.of(Boolean.FALSE));
    }
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
          if (bitcoinSentEvent.isSendWasSuccessful()) {
            LabelDecorator.applyWrappingLabel(transactionBroadcastStatusSummary, Languages.safeText(CoreMessageKey.BITCOIN_SENT_OK));
            LabelDecorator.applyStatusLabel(transactionBroadcastStatusSummary, Optional.of(Boolean.TRUE));
          } else {
            String summaryMessage = Languages.safeText(CoreMessageKey.BITCOIN_SEND_FAILED);
            String detailMessage = Languages.safeText(bitcoinSentEvent.getSendFailureReasonKey(), (Object[]) bitcoinSentEvent.getSendFailureReasonData());
            LabelDecorator.applyWrappingLabel(transactionBroadcastStatusSummary, summaryMessage);
            LabelDecorator.applyWrappingLabel(transactionBroadcastStatusDetail, detailMessage);
            LabelDecorator.applyStatusLabel(transactionBroadcastStatusSummary, Optional.of(Boolean.FALSE));
          }
        }
      });
  }

  /**
   * @param transactionSeenEvent The event (very high frequency during synchronisation)
   */
  @Subscribe
  public void onTransactionSeenEvent(final TransactionSeenEvent transactionSeenEvent) {

    lastTransactionSeenEvent = transactionSeenEvent;
    // The event may be fired before the UI has initialised
    if (!initialised) {
      return;
    }

    currentTransactionId = transactionSeenEvent.getTransactionId();

    // Is this an event about the transaction that was just sent ?
    if (transactionSeenEvent.getTransactionId().equals(currentTransactionId)) {

      final PaymentStatus paymentStatus = WalletService.calculateStatus(
        transactionSeenEvent.getConfidenceType(),
        transactionSeenEvent.getDepthInBlocks(),
        transactionSeenEvent.getNumberOfPeers()
      );

      SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {

            transactionConfirmationStatus.setText(
              Languages.safeText(
                paymentStatus.getStatusKey(),
                transactionSeenEvent.getNumberOfPeers()
              )
            );

            LabelDecorator.applyPaymentStatusIconAndColor(
              paymentStatus,
              transactionConfirmationStatus,
              transactionSeenEvent.isCoinbase(),
              MultiBitUI.NORMAL_ICON_SIZE
            );

          }
        });

    }
  }
}
