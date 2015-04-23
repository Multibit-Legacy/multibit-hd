package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.uri.BitcoinURI;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.MBHDPaymentRequestData;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.LocalisedDateUtils;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressModel;
import org.multibit.hd.ui.views.components.display_address.DisplayBitcoinAddressView;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeModel;
import org.multibit.hd.ui.views.components.display_qrcode.DisplayQRCodeView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show payment request overview</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class PaymentRequestDetailPanelView extends AbstractWizardPanelView<PaymentsWizardModel, PaymentRequestDetailPanelModel> {

  private JLabel dateValue;

  private JLabel statusValue;

  private ModelAndView<DisplayBitcoinAddressModel, DisplayBitcoinAddressView> displayBitcoinAddressMaV;

  private JLabel qrCodeLabelValue;

  private JLabel noteValue;

  private JLabel amountBTCValue;

  private JLabel amountFiatLabel;
  private JLabel amountFiatValue;

  private JLabel exchangeRateLabel;
  private JLabel exchangeRateValue;

  // Support QR code popover
  private ModelAndView<DisplayQRCodeModel, DisplayQRCodeView> displayQRCodePopoverMaV;


  /**
   * @param wizard The wizard managing the states
   */
  public PaymentRequestDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {
    super(wizard, panelName, MessageKey.DISPLAY_PAYMENT_REQUEST_TITLE, AwesomeIcon.FILE_TEXT_O);
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
    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[]20[][]", // Column constraints
        "[]10[]10[]" // Row constraints
      ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    JLabel dateLabel = Labels.newValueLabel(Languages.safeText(MessageKey.DATE));
    dateValue = Labels.newBlankLabel();

    JLabel statusLabel = Labels.newValueLabel(Languages.safeText(MessageKey.STATUS));
    statusValue = Labels.newBlankLabel();

    // Prepare the Bitcoin address
    JLabel addressLabel = Labels.newValueLabel(Languages.safeText(MessageKey.BITCOIN_ADDRESS));

    // Create the QR code display
    displayQRCodePopoverMaV = Popovers.newDisplayQRCodePopoverMaV(getPanelName());

    JLabel qrCodeLabelLabel = Labels.newValueLabel(Languages.safeText(MessageKey.QR_CODE_LABEL));
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

    amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.LOCAL_AMOUNT));
    amountFiatValue = Labels.newBlankLabel();

    exchangeRateLabel = Labels.newValueLabel(Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL));
    exchangeRateValue = Labels.newBlankLabel();

    boolean paymentOK = readMBHDPaymentRequestData();

    contentPanel.add(statusLabel);
    contentPanel.add(statusValue, "wrap");

    // Avoid NPEs if payment fails
    if (paymentOK) {
      contentPanel.add(dateLabel);
      contentPanel.add(dateValue, "wrap");

      contentPanel.add(addressLabel);
      contentPanel.add(displayBitcoinAddressMaV.getView().newComponentPanel());
      contentPanel.add(Buttons.newQRCodeButton(getShowQRCodePopoverAction()), "wrap");
      // Not working yet
      //contentPanel.add(Buttons.newSmallShowSignMessageWizardButton(getShowSignMessageWizardAction()), "wrap");

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

      // Register components
      registerComponents(displayBitcoinAddressMaV, displayQRCodePopoverMaV);
    }
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
    getFinishButton().requestFocusInWindow();
    getFinishButton().setEnabled(true);
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  /**
   * @return True if the payment request data was populated
   */
  private boolean readMBHDPaymentRequestData() {
    // Work out the payment request to show
    MBHDPaymentRequestData MBHDPaymentRequestData = getWizardModel().getMBHDPaymentRequestData();

    if (MBHDPaymentRequestData == null) {
      // Shouldn't happen but put a message on the UI all the same
      statusValue.setText(Languages.safeText(CoreMessageKey.NO_PAYMENT_REQUEST));
      return false;
    } else {

      DateTime date = MBHDPaymentRequestData.getDate();
      // Display in the system timezone
      dateValue.setText(LocalisedDateUtils.formatFriendlyDateLocal(date));

      displayBitcoinAddressMaV = Components.newDisplayBitcoinAddressMaV(MBHDPaymentRequestData.getAddress().toString());

      qrCodeLabelValue.setText(MBHDPaymentRequestData.getLabel());

      statusValue.setText(Languages.safeText(MBHDPaymentRequestData.getStatus().getStatusKey(), MBHDPaymentRequestData.getStatus().getStatusData()));
      LabelDecorator.applyPaymentStatusIconAndColor(MBHDPaymentRequestData.getStatus(), statusValue, false, MultiBitUI.SMALL_ICON_SIZE);

      noteValue.setText(MBHDPaymentRequestData.getNote());

      Coin amountBTC = MBHDPaymentRequestData.getAmountCoin().or(Coin.ZERO);
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();

      String[] balanceArray = Formats.formatCoinAsSymbolic(amountBTC, languageConfiguration, bitcoinConfiguration, true);
      amountBTCValue.setText(balanceArray[0] + balanceArray[1]);

      FiatPayment amountFiat = MBHDPaymentRequestData.getAmountFiat();
      if (amountFiat.getAmount().isPresent()) {
        amountFiatValue.setText((Formats.formatLocalAmount(amountFiat.getAmount().get(), languageConfiguration.getLocale(), bitcoinConfiguration, true)));
      } else {
        amountFiatValue.setText("");
      }

      if (amountFiat.getCurrency().isPresent()) {
        amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.LOCAL_AMOUNT) + " " + amountFiat.getCurrency().get().getCurrencyCode());
        // Add Bitcoin unit to exchange rate label
        LabelDecorator.applyBitcoinSymbolLabel(
          exchangeRateLabel,
          Configurations.currentConfiguration.getBitcoin(),
          Languages.safeText(
            MessageKey.EXCHANGE_RATE_LABEL)
            + " "
            + amountFiat.getCurrency().get().getCurrencyCode()
            + " / ");
      } else {
        amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.LOCAL_AMOUNT));
        exchangeRateLabel.setText(Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL));
      }

      String exchangeRateText;
      if (Strings.isNullOrEmpty(MBHDPaymentRequestData.getAmountFiat().getRate().or("")) || Strings.isNullOrEmpty(MBHDPaymentRequestData.getAmountFiat().getExchangeName().or(""))) {
        exchangeRateText = Languages.safeText(MessageKey.NOT_AVAILABLE);
      } else {
        // Convert the exchange rate (which is always stored as fiat currency per bitcoin) to match the unit of bitcoin being used
        String convertedExchangeRateText = Formats.formatExchangeRate(MBHDPaymentRequestData.getAmountFiat().getRate(), languageConfiguration, bitcoinConfiguration);
        exchangeRateText = convertedExchangeRateText + " (" + MBHDPaymentRequestData.getAmountFiat().getExchangeName().or("") + ")";
      }
      exchangeRateValue.setText(exchangeRateText);
    }

    // Must be OK to be here
    return true;
  }

  /**
   * @return A new action for showing or hiding the QR code popover
   */
  private Action getShowQRCodePopoverAction() {

    // Show or hide the QR code
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (Panels.isLightBoxPopoverShowing()) {
          // Hide the popover being shown
          Panels.hideLightBoxPopoverIfPresent();
        } else {
          // Show the QR code popover
          MBHDPaymentRequestData MBHDPaymentRequestData = getWizardModel().getMBHDPaymentRequestData();

          Address bitcoinAddress = MBHDPaymentRequestData.getAddress();
          Coin coin = MBHDPaymentRequestData.getAmountCoin().isPresent() ? MBHDPaymentRequestData.getAmountCoin().get() : null;
          String label = MBHDPaymentRequestData.getLabel();

          // Form a Bitcoin URI from the contents
          String bitcoinUri = BitcoinURI.convertToBitcoinURI(
                  bitcoinAddress,
                  coin,
                  label,
                  null
          );

          displayQRCodePopoverMaV.getModel().setValue(bitcoinUri);
          displayQRCodePopoverMaV.getModel().setTransactionLabel(label);

          // Show the QR code as a popover
          Panels.showLightBoxPopover(displayQRCodePopoverMaV.getView().newComponentPanel());
        }
      }
    };
  }
}
