package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.events.PaymentSentToRequestorEvent;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Enter BIP70 payment ack memo</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendBitcoinShowPaymentACKMemoPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinShowPaymentACKMemoPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(SendBitcoinShowPaymentACKMemoPanelView.class);

  // View components
  private JLabel paymentSentOKSummary;

  private JTextArea paymentACKMemo;

  private JLabel paymentACKMemoLabel;

  private SendBitcoinShowPaymentACKMemoPanelModel panelModel;

  private PaymentSentToRequestorEvent lastPaymentSentToRequestorEvent;


  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name for filtering component events
   */
  public SendBitcoinShowPaymentACKMemoPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {
    super(wizard, panelName, AwesomeIcon.CLOUD_UPLOAD, MessageKey.SEND_PAYMENT_ACK_MEMO_TITLE);
  }

  @Override
  public void newPanelModel() {
    // Configure the panel model
    panelModel = new SendBitcoinShowPaymentACKMemoPanelModel(
      getPanelName());
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setSendBitcoinShowPaymentACKMemoPanelModel(panelModel);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {
    paymentSentOKSummary = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    AccessibilityDecorator.apply(paymentSentOKSummary, CoreMessageKey.PAYMENT_SENDING_TO_REQUESTER);

    // BIP70 PaymentACK memo
    paymentACKMemo = TextBoxes.newReadOnlyTextArea(6, MultiBitUI.PASSWORD_LENGTH);

    // Apply any Payment Request parameters
    if (getWizardModel().getPaymentRequestData().isPresent()) {
      PaymentRequestData paymentRequestData = getWizardModel().getPaymentRequestData().get();
      // Fill in the paymentACK memo
      if (paymentRequestData.getPaymentACK().isPresent()) {
        paymentACKMemo.setText(paymentRequestData.getPaymentACK().get().getMemo());
      }
    }

    paymentACKMemoLabel = Labels.newBlankLabel();
    paymentACKMemoLabel.setText(Languages.safeText(MessageKey.SEND_PAYMENT_ACK_MEMO_LABEL));
    AccessibilityDecorator.apply(paymentACKMemoLabel, MessageKey.SEND_PAYMENT_ACK_MEMO_TITLE);

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]40[]2[]" // Row constraints
      ));

    contentPanel.add(paymentSentOKSummary, "wrap");
    contentPanel.add(paymentACKMemoLabel, "wrap");
    contentPanel.add(paymentACKMemo, "growx,push,wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<SendBitcoinWizardModel> wizard) {
    PanelDecorator.addFinish(this, wizard);
  }

  @Override
  public void fireInitialStateViewEvents() {
  }

  @Override
  public boolean beforeShow() {
    paymentSentOKSummary.setText(Languages.safeText(CoreMessageKey.PAYMENT_SENDING_TO_REQUESTER));
    return true;
  }

  @Override
  public void afterShow() {
    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          paymentACKMemo.requestFocusInWindow();

          // Transaction must be progressing in some manner
          if (lastPaymentSentToRequestorEvent != null) {
            onPaymentSentToRequestorEvent(lastPaymentSentToRequestorEvent);
            lastPaymentSentToRequestorEvent = null;
          }

        }
      });
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
  }

  @Subscribe
  public void onPaymentSentToRequestorEvent(final PaymentSentToRequestorEvent paymentSentToRequestorEvent) {
    lastPaymentSentToRequestorEvent = paymentSentToRequestorEvent;
    // The event may be fired before the UI has initialised
    if (!isInitialised()) {
      return;
    }

    SwingUtilities.invokeLater(
      new Runnable() {
        @Override
        public void run() {
          paymentACKMemo.setText(getPanelModel().get().getPaymentACKMemo());

          if (paymentSentToRequestorEvent.isSendWasSuccessful()) {
            LabelDecorator.applyWrappingLabel(paymentSentOKSummary, Languages.safeText(CoreMessageKey.PAYMENT_SENT_TO_REQUESTER_OK));
            LabelDecorator.applyStatusIcon(paymentSentOKSummary, Optional.of(Boolean.TRUE));

          } else {
            // Add (Object) cast to suppress varargs warning
            String summaryMessage = Languages.safeText(CoreMessageKey.PAYMENT_SENT_TO_REQUESTER_FAILED, (Object) paymentSentToRequestorEvent.getSendFailureReasonData());
            LabelDecorator.applyWrappingLabel(paymentSentOKSummary, summaryMessage);
            LabelDecorator.applyStatusIcon(paymentSentOKSummary, Optional.of(Boolean.FALSE));
          }
        }
      });
  }
}