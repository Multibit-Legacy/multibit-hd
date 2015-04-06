package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.AccessibilityDecorator;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
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
 * <li>Show BIP70 payment request memo</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class BIP70PaymentRequestMemoPanelView extends AbstractWizardPanelView<PaymentsWizardModel, TransactionDetailPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(BIP70PaymentRequestMemoPanelView.class);

  private JTextArea paymentMemo;
  private JLabel paymentMemoLabel;

  private JTextArea paymentACKMemo;
  private JLabel paymentACKMemoLabel;

  /**
   * @param wizard The wizard managing the states
   */
  public BIP70PaymentRequestMemoPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {
    super(wizard, panelName, MessageKey.DISPLAY_PAYMENT_REQUEST_MEMO_TITLE, AwesomeIcon.FILE_TEXT_O);
  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    TransactionDetailPanelModel panelModel = new TransactionDetailPanelModel(
            getPanelName()
    );
    setPanelModel(panelModel);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {
    contentPanel.setLayout(
            new MigLayout(
                    Panels.migXYLayout(),
                    "[][]", // Column constraints
                    "[shrink][shrink][grow]" // Row constraints
            ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // BIP70 Payment memo
    paymentMemo = TextBoxes.newReadOnlyTextArea(6, MultiBitUI.PASSWORD_LENGTH);

    // BIP70 PaymentACK memo
    paymentACKMemo = TextBoxes.newReadOnlyTextArea(6, MultiBitUI.PASSWORD_LENGTH);

    // Apply any Payment Request parameters
    if (getWizardModel().getPaymentRequestData() != null) {
      PaymentRequestData paymentRequestData = getWizardModel().getPaymentRequestData();
      // Fill in the payment memo
      if (paymentRequestData.getPayment().isPresent()) {
        paymentMemo.setText(paymentRequestData.getPayment().get().getMemo());
      }
      // Fill in the paymentACK memo
      if (paymentRequestData.getPaymentACK().isPresent()) {
        paymentACKMemo.setText(paymentRequestData.getPaymentACK().get().getMemo());
      }
    }

    paymentMemoLabel = Labels.newBlankLabel();
    paymentMemoLabel.setText(Languages.safeText(MessageKey.SEND_PAYMENT_MEMO_LABEL_READ_ONLY));
    AccessibilityDecorator.apply(paymentMemoLabel, MessageKey.SEND_PAYMENT_MEMO_LABEL_READ_ONLY);

    paymentACKMemoLabel = Labels.newBlankLabel();
    paymentACKMemoLabel.setText(Languages.safeText(MessageKey.SEND_PAYMENT_ACK_MEMO_LABEL));
    AccessibilityDecorator.apply(paymentACKMemoLabel, MessageKey.SEND_PAYMENT_ACK_MEMO_LABEL);

    contentPanel.add(paymentMemoLabel, "wrap");
    contentPanel.add(paymentMemo, "growx,push,wrap");
    contentPanel.add(paymentACKMemoLabel, "wrap");
    contentPanel.add(paymentACKMemo, "growx,push,wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {
    PanelDecorator.addExitCancelPreviousFinish(this, wizard);
  }

  @Override
  public void afterShow() {
    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }
}
