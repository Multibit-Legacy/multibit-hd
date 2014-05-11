package org.multibit.hd.ui.views.wizards.payments;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.LocalisedDateUtils;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeModel;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;
import java.awt.event.ActionEvent;
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

  private JLabel dateValue;

  private JLabel statusValue;

  private JLabel addressValue;

  private JLabel qrCodeLabelValue;

  private JLabel noteValue;

  private JLabel amountBTCValue;

  private JLabel amountFiatValue;

  private JLabel exchangeRateValue;

  // Support QR code popover
  private ModelAndView<DisplayQRCodeModel, DisplayQRCodeView> displayQRCodePopoverMaV;
  private JButton showQRCode;


  /**
   * @param wizard The wizard managing the states
   */
  public PaymentRequestDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {
    super(wizard, panelName, MessageKey.PAYMENT_REQUEST, AwesomeIcon.FILE_TEXT_ALT);
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
    dateValue = Labels.newBlankLabel();

    JLabel statusLabel = Labels.newValueLabel(Languages.safeText(MessageKey.STATUS));
    statusValue = Labels.newBlankLabel();

    JLabel addressLabel = Labels.newValueLabel(Languages.safeText(MessageKey.BITCOIN_ADDRESS));
    addressValue = Labels.newBlankLabel();

    // Create the QR code display
    displayQRCodePopoverMaV = Popovers.newDisplayQRCodePopoverMaV(getPanelName());
    showQRCode = Buttons.newQRCodeButton(getShowQRCodePopoverAction());

    JLabel qrCodeLabelLabel = Labels.newValueLabel(Languages.safeText(MessageKey.QR_CODE_LABEL_LABEL));
    qrCodeLabelValue = Labels.newBlankLabel();

    JLabel noteLabel = Labels.newValueLabel(Languages.safeText(MessageKey.PRIVATE_NOTES));
    noteValue = Labels.newBlankLabel();

    JLabel amountBTCLabel = Labels.newBlankLabel();
    amountBTCValue = Labels.newBlankLabel();
    // Bitcoin column
    LabelDecorator.applyBitcoinSymbolLabel(
            amountBTCLabel,
            Configurations.currentConfiguration.getBitcoin(),
            Languages.safeText(MessageKey.LOCAL_AMOUNT) + " ");

    JLabel amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.LOCAL_AMOUNT) + " " + Configurations.currentConfiguration.getBitcoin().getLocalCurrencySymbol());
    amountFiatValue = Labels.newBlankLabel();

    JLabel exchangeRateLabel = Labels.newValueLabel(Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL));
    exchangeRateValue = Labels.newBlankLabel();

    update();

    contentPanel.add(statusLabel);
    contentPanel.add(statusValue, "wrap");

    contentPanel.add(dateLabel);
    contentPanel.add(dateValue, "wrap");

    contentPanel.add(addressLabel);
    contentPanel.add(addressValue);
    contentPanel.add(showQRCode, "wrap");

    contentPanel.add(qrCodeLabelLabel);
    contentPanel.add(qrCodeLabelValue, "wrap");

    contentPanel.add(noteLabel);
    contentPanel.add(noteValue, "wrap");

    contentPanel.add(amountBTCLabel);
    contentPanel.add(amountBTCValue, "wrap");

    contentPanel.add(amountFiatLabel);
    contentPanel.add(amountFiatValue, "wrap");

    contentPanel.add(exchangeRateLabel);
    contentPanel.add(exchangeRateValue, "wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {
    if (getWizardModel().isShowPrevOnPaymentRequestDetailScreen()) {
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

    // Work out the payment request to show
    PaymentRequestData paymentRequestData = getWizardModel().getPaymentRequestData();

    if (paymentRequestData == null) {
      // Shouldn't happen but put a message on the UI all the same
      statusValue.setText(Languages.safeText(CoreMessageKey.NO_PAYMENT_REQUEST));
    } else {

      DateTime date = paymentRequestData.getDate();
      dateValue.setText(LocalisedDateUtils.formatFriendlyDate(date));
      addressValue.setText(paymentRequestData.getAddress());
      qrCodeLabelValue.setText(paymentRequestData.getLabel());

      statusValue.setText(Languages.safeText(paymentRequestData.getStatus().getStatusKey(), paymentRequestData.getStatus().getStatusData()));
      LabelDecorator.applyStatusIconAndColor(paymentRequestData.getStatus(), statusValue, false, MultiBitUI.SMALL_ICON_SIZE);

      noteValue.setText(paymentRequestData.getNote());

      BigInteger amountBTC = paymentRequestData.getAmountBTC();
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();

      String[] balanceArray = Formats.formatSatoshisAsSymbolic(amountBTC, languageConfiguration, bitcoinConfiguration, true);
      amountBTCValue.setText(balanceArray[0] + balanceArray[1]);

      FiatPayment amountFiat = paymentRequestData.getAmountFiat();
      amountFiatValue.setText((Formats.formatLocalAmount(amountFiat.getAmount(), languageConfiguration.getLocale(), bitcoinConfiguration, true)));

      String exchangeRateText;
      if (Strings.isNullOrEmpty(paymentRequestData.getAmountFiat().getRate().or("")) || Strings.isNullOrEmpty(paymentRequestData.getAmountFiat().getExchangeName().or(""))) {
        exchangeRateText = Languages.safeText(MessageKey.NOT_AVAILABLE);
      } else {
        exchangeRateText = paymentRequestData.getAmountFiat().getRate().or("") + " (" + paymentRequestData.getAmountFiat().getExchangeName().or("") + ")";
      }
      exchangeRateValue.setText(exchangeRateText);
    }
  }

  /**
   * @return A new action for showing the QR code popover
   */
  private Action getShowQRCodePopoverAction() {

    // Show or hide the QR code
    return new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        PaymentRequestData paymentRequestData = getWizardModel().getPaymentRequestData();

        String bitcoinAddress = paymentRequestData.getAddress();
        BigInteger satoshis = paymentRequestData.getAmountBTC();
        String label = paymentRequestData.getLabel();

        // Form a Bitcoin URI from the contents
        String bitcoinUri = BitcoinURI.convertToBitcoinURI(
          bitcoinAddress,
          satoshis,
          label,
          null
        );

        displayQRCodePopoverMaV.getModel().setValue(bitcoinUri);
        displayQRCodePopoverMaV.getModel().setLabel(label);

        // Show the QR code as a popover
        Panels.showLightBoxPopover(displayQRCodePopoverMaV.getView().newComponentPanel());
      }

    };
  }

}
