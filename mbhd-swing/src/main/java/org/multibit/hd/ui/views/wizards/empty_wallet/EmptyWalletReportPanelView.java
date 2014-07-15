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
 *  
 */
public class EmptyWalletReportPanelView extends AbstractWizardPanelView<EmptyWalletWizardModel, EmptyWalletReportPanelModel> {

  private JLabel transactionConstructionStatusSummary;
  private JLabel transactionConstructionStatusDetail;

  private JLabel transactionBroadcastStatusSummary;
  private JLabel transactionBroadcastStatusDetail;

  private JLabel transactionConfirmationStatus;

  private TransactionCreationEvent lastTransactionCreationEvent;
  private BitcoinSentEvent lastBitcoinSentEvent;
  private TransactionSeenEvent lastTransactionSeenEvent;

  private boolean initialised = false;

  /**
   * @param wizard The wizard managing the states
   */
  public EmptyWalletReportPanelView(AbstractWizard<EmptyWalletWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.EMPTY_WALLET_PROGRESS_TITLE, AwesomeIcon.FIRE);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    EmptyWalletReportPanelModel panelModel = new EmptyWalletReportPanelModel(
      getPanelName()
    );
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setReportPanelModel(panelModel);

    lastTransactionCreationEvent = null;
    lastBitcoinSentEvent = null;
    lastTransactionSeenEvent = null;

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][][]", // Column constraints
      "[]10[]10[]" // Row constraints
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
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getFinishButton().requestFocusInWindow();
        if (lastTransactionCreationEvent != null) {
          onTransactionCreationEvent(lastTransactionCreationEvent);
        }
        if (lastBitcoinSentEvent != null) {
          onBitcoinSentEvent(lastBitcoinSentEvent);
        }
        if (lastTransactionSeenEvent != null) {
          onTransactionSeenEvent(lastTransactionSeenEvent);
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
      getPanelModel().get().setTransactionId(transactionCreationEvent.getTransactionId());

      transactionConstructionStatusSummary.setText(Languages.safeText(CoreMessageKey.TRANSACTION_CREATED_OK));
      transactionConstructionStatusDetail.setText("");
      Labels.decorateStatusLabel(transactionConstructionStatusSummary, Optional.of(Boolean.TRUE));
    } else {
      String detailMessage = Languages.safeText(transactionCreationEvent.getTransactionCreationFailureReasonKey(),
        (Object[]) transactionCreationEvent.getTransactionCreationFailureReasonData());
      transactionConstructionStatusSummary.setText(Languages.safeText(CoreMessageKey.TRANSACTION_CREATION_FAILED));
      transactionConstructionStatusDetail.setText(detailMessage);
      Labels.decorateStatusLabel(transactionConstructionStatusSummary, Optional.of(Boolean.FALSE));
    }
  }

  @Subscribe
  public void onBitcoinSentEvent(BitcoinSentEvent bitcoinSentEvent) {

    log.debug("Received the BitcoinSentEvent: " + bitcoinSentEvent.toString());

    lastBitcoinSentEvent = bitcoinSentEvent;
    // The event may be fired before the UI has initialised
    if (!initialised) {
      return;
    }

    if (bitcoinSentEvent.isSendWasSuccessful()) {
      transactionBroadcastStatusSummary.setText(Languages.safeText(CoreMessageKey.BITCOIN_SENT_OK));
      Labels.decorateStatusLabel(transactionBroadcastStatusSummary, Optional.of(Boolean.TRUE));
    } else {
      String summaryMessage = Languages.safeText(CoreMessageKey.BITCOIN_SEND_FAILED);
      String detailMessage = Languages.safeText(bitcoinSentEvent.getSendFailureReasonKey(), (Object[]) bitcoinSentEvent.getSendFailureReasonData());
      transactionBroadcastStatusSummary.setText(summaryMessage);
      transactionBroadcastStatusDetail.setText(detailMessage);
      Labels.decorateStatusLabel(transactionBroadcastStatusSummary, Optional.of(Boolean.FALSE));
    }
  }

  @Subscribe
  public void onTransactionSeenEvent(TransactionSeenEvent transactionSeenEvent) {

    lastTransactionSeenEvent = transactionSeenEvent;
    // The event may be fired before the UI has initialised
    if (!initialised) {
      return;
    }

    // Is this an event about the transaction that was just sent ?
    // If so, update the UI
    if (getPanelModel().get() != null) {
      String currentTransactionId = getPanelModel().get().getTransactionId();
      if (transactionSeenEvent.getTransactionId().equals(currentTransactionId)) {
        PaymentStatus paymentStatus = WalletService.calculateStatus(
          transactionSeenEvent.getConfidenceType(),
          transactionSeenEvent.getDepthInBlocks(),
          transactionSeenEvent.getNumberOfPeers()
        );
        transactionConfirmationStatus.setText(
          Languages.safeText(
            paymentStatus.getStatusKey(),
            transactionSeenEvent.getNumberOfPeers()
          )
        );

        LabelDecorator.applyPaymentStatusIconAndColor(paymentStatus, transactionConfirmationStatus, transactionSeenEvent.isCoinbase(), MultiBitUI.NORMAL_ICON_SIZE);
      }
    }
  }
}
