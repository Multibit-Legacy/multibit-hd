package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.LocalisedDateUtils;
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
import java.math.BigInteger;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show payment request overview</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PaymentRequestDetailPanelView extends AbstractWizardPanelView<PaymentsWizardModel, PaymentRequestDetailPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(PaymentRequestDetailPanelView.class);

   private JLabel dateValue;
   private JLabel statusValue;
   private JLabel qrCodeLabelValue;
   private JLabel noteValue;
   private JLabel amountBTCValue;
   private JLabel amountFiatValue;

  private boolean showPrev;

  /**
   * @param wizard The wizard managing the states
   */
  public PaymentRequestDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName, boolean showPrev) {
    super(wizard, panelName, MessageKey.PAYMENT_REQUEST, AwesomeIcon.FILE_TEXT_ALT);
    this.showPrev = showPrev;
  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    PaymentRequestDetailPanelModel panelModel = new PaymentRequestDetailPanelModel(
      getPanelName()
    );
    setPanelModel(panelModel);
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

    JLabel dateLabel = Labels.newValueLabel(Languages.safeText(MessageKey.DATE));
    dateValue = Labels.newValueLabel("");

    JLabel statusLabel = Labels.newValueLabel(Languages.safeText(MessageKey.STATUS));
    statusValue = Labels.newValueLabel("");

    JLabel qrCodeLabelLabel = Labels.newValueLabel(Languages.safeText(MessageKey.QR_CODE_LABEL_LABEL));
    qrCodeLabelValue = Labels.newValueLabel("");

    JLabel noteLabel = Labels.newValueLabel(Languages.safeText(MessageKey.NOTES));
    noteValue = Labels.newValueLabel("");

    JLabel amountBTCLabel = Labels.newValueLabel("");
    amountBTCValue = Labels.newValueLabel("");
    // Bitcoin column
    LabelDecorator.applyBitcoinSymbolLabel(
            amountBTCLabel,
            Configurations.currentConfiguration.getBitcoinConfiguration(),
            Languages.safeText(MessageKey.AMOUNT) + " ");

    JLabel amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.AMOUNT) + " " + Configurations.currentConfiguration.getBitcoinConfiguration().getLocalCurrencySymbol());
    amountFiatValue = Labels.newValueLabel("");

    update();

    contentPanel.add(statusLabel);
    contentPanel.add(statusValue, "wrap");
    contentPanel.add(dateLabel);
    contentPanel.add(dateValue, "wrap");
    contentPanel.add(qrCodeLabelLabel);
    contentPanel.add(qrCodeLabelValue, "wrap");
    contentPanel.add(noteLabel);
    contentPanel.add(noteValue, "wrap");
    contentPanel.add(amountBTCLabel);
    contentPanel.add(amountBTCValue, "wrap");
    contentPanel.add(amountFiatLabel);
    contentPanel.add(amountFiatValue, "wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {

    if (showPrev) {
      PanelDecorator.addCancelPreviousFinish(this, wizard);
    } else {
      PanelDecorator.addCancelFinish(this, wizard);
    }
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getFinishButton().requestFocusInWindow();
        getFinishButton().setEnabled(true);
      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  public void update() {
    PaymentData paymentData = getWizardModel().getPaymentData();
    if (paymentData != null) {
      DateTime date = paymentData.getDate();
      dateValue.setText(LocalisedDateUtils.formatFriendlyDate(date));

      if (paymentData instanceof PaymentRequestData) {
        qrCodeLabelValue.setText(((PaymentRequestData)paymentData).getLabel());
      }

      statusValue.setText(paymentData.getStatus().getStatusText());
      LabelDecorator.applyStatusIconAndColor(paymentData, statusValue, MultiBitUI.SMALL_ICON_SIZE);

      noteValue.setText(paymentData.getNote());

      BigInteger amountBTC = paymentData.getAmountBTC();
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguageConfiguration();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();

      String[] balanceArray = Formats.formatSatoshisAsSymbolic(amountBTC, languageConfiguration, bitcoinConfiguration);
      amountBTCValue.setText(balanceArray[0] + balanceArray[1]);

      FiatPayment amountFiat = paymentData.getAmountFiat();
      amountFiatValue.setText((Formats.formatLocalAmount(amountFiat.getAmount(), languageConfiguration.getLocale(), bitcoinConfiguration)));

      if (paymentData instanceof TransactionData) {
        // It should be as payment requests are routed to their own screen but check all the same
        TransactionData transactionData = (TransactionData) paymentData;
        String transactionId = transactionData.getTransactionId();
        Optional<BigInteger> feeOnSendBTC = transactionData.getFeeOnSendBTC();
      }
    }
  }
}
