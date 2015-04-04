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
 * <li>Send bitcoin: Enter BIP70 payment memo</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SendBitcoinEnterPaymentMemoPanelView extends AbstractWizardPanelView<SendBitcoinWizardModel, SendBitcoinEnterPaymentMemoPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(SendBitcoinEnterPaymentMemoPanelView.class);

  // View components
  private JTextArea paymentMemo;


  private JLabel paymentMemoLabel;

  private SendBitcoinEnterPaymentMemoPanelModel panelModel;


  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name for filtering component events
   */
  public SendBitcoinEnterPaymentMemoPanelView(AbstractWizard<SendBitcoinWizardModel> wizard, String panelName) {
    super(wizard, panelName, MessageKey.SEND_PAYMENT_MEMO_TITLE, AwesomeIcon.CLOUD_UPLOAD);
  }

  @Override
  public void newPanelModel() {
    // Configure the panel model
    panelModel = new SendBitcoinEnterPaymentMemoPanelModel(
            getPanelName());
    setPanelModel(panelModel);

    // Bind it to the wizard model
    getWizardModel().setEnterPaymentMemoPanelModel(panelModel);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {
    // BIP70 Payment memo
    paymentMemo = TextBoxes.newEnterNotes(getWizardModel());

    // Apply any Payment Request parameters
    if (getWizardModel().getPaymentRequestData().isPresent()) {
      PaymentRequestData paymentRequestData = getWizardModel().getPaymentRequestData().get();

      // Fill in the payment memo
      if (paymentRequestData.getPayment().isPresent()) {
        paymentMemo.setText(paymentRequestData.getPayment().get().getMemo());
      }
    }

    paymentMemoLabel = Labels.newBlankLabel();
    AccessibilityDecorator.apply(paymentMemoLabel, MessageKey.SEND_PAYMENT_MEMO_TITLE);
    paymentMemoLabel.setText(Languages.safeText(MessageKey.SEND_PAYMENT_MEMO_LABEL));

    contentPanel.setLayout(
            new MigLayout(
                    Panels.migXYLayout(),
                    "[]", // Column constraints
                    "[]10[]" // Row constraints
            ));

    contentPanel.add(paymentMemoLabel, "wrap");
    contentPanel.add(paymentMemo, "growx,push,wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<SendBitcoinWizardModel> wizard) {
    PanelDecorator.addNext(this, wizard);
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
    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                paymentMemo.requestFocusInWindow();
              }
            });
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    panelModel.setPaymentMemo(paymentMemo.getText());
  }
}