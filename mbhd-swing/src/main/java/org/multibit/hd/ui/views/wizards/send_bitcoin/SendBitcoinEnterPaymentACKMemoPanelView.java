package org.multibit.hd.ui.views.wizards.send_bitcoin;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
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
public class SendBitcoinEnterPaymentACKMemoPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinEnterPaymentACKMemoPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(SendBitcoinEnterPaymentACKMemoPanelView.class);

  // View components
  private JTextArea paymentACKMemo;

  private JLabel paymentACKMemoLabel;

  private SendBitcoinEnterPaymentACKMemoPanelModel panelModel;


  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name for filtering component events
   */
  public SendBitcoinEnterPaymentACKMemoPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {
    super(wizard, panelName, MessageKey.SEND_PAYMENT_ACK_MEMO_TITLE, AwesomeIcon.CLOUD_UPLOAD);
  }

  @Override
  public void newPanelModel() {
    // Configure the panel model
    panelModel = new SendBitcoinEnterPaymentACKMemoPanelModel(
      getPanelName());
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setSendBitcoinEnterPaymentACKMemoPanelModel(panelModel);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {
    // BIP70 PaymentACK memo
    paymentACKMemo = TextBoxes.newEnterPrivateNotes(getWizardModel());

    // Apply any Payment Request parameters
    if (getWizardModel().getPaymentRequestData().isPresent()) {
      PaymentRequestData paymentRequestData = getWizardModel().getPaymentRequestData().get();
      // Fill in the paymentACK memo
      if (paymentRequestData.getPaymentACK().isPresent()) {
        paymentACKMemo.setText(paymentRequestData.getPayment().get().getMemo());
      }
    }

    paymentACKMemoLabel = Labels.newBlankLabel();
    paymentACKMemoLabel.setText(Languages.safeText(MessageKey.SEND_PAYMENT_ACK_MEMO_LABEL));
    AccessibilityDecorator.apply(paymentACKMemoLabel, MessageKey.SEND_PAYMENT_ACK_MEMO_TITLE);

    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]", // Column constraints
        "[]10[]" // Row constraints
      ));

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
    return true;
  }

  @Override
  public void afterShow() {
    paymentACKMemo.requestFocusInWindow();
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    panelModel.setPaymentACKMemo(paymentACKMemo.getText());
  }
}