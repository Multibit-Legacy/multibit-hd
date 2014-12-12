package org.multibit.hd.ui.views.wizards.payments;

import org.bitcoinj.core.Coin;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.store.TransactionInfo;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
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
 * <li>Show transaction overview</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class TransactionAmountPanelView extends AbstractWizardPanelView<PaymentsWizardModel, TransactionOverviewPanelModel> {

  private JLabel amountBTCLabel;
  private JLabel amountBTCValue;
  private JLabel amountFiatLabel;
  private JLabel amountFiatValue;
  private JLabel miningFeePaidLabel;
  private JLabel miningFeePaidValue;
  private JLabel clientFeePaidLabel;
  private JLabel clientFeePaidValue;
  private JLabel exchangeRateLabel;
  private JLabel exchangeRateValue;

  /**
   * @param wizard The wizard managing the states
   */
  public TransactionAmountPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TRANSACTION_AMOUNT, AwesomeIcon.FILE_TEXT_O);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    TransactionOverviewPanelModel panelModel = new TransactionOverviewPanelModel(
            getPanelName()
    );
    setPanelModel(panelModel);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
            Panels.migXYLayout(),
            "[]10[][][]", // Column constraints
            "[]10[]10[]10[]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    // Create amount BTC label, text value is added in updateAmountCoin()
    amountBTCLabel = Labels.newValueLabel("");

    amountBTCValue = Labels.newValueLabel("");

    amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.LOCAL_AMOUNT));
    amountFiatValue = Labels.newValueLabel("");

    miningFeePaidLabel = Labels.newValueLabel("");
    // Add bitcoin unit to mining fee label
    LabelDecorator.applyBitcoinSymbolLabel(
            miningFeePaidLabel,
            Configurations.currentConfiguration.getBitcoin(),
            Languages.safeText(MessageKey.TRANSACTION_FEE) + " ");
    miningFeePaidValue = Labels.newValueLabel("");

    clientFeePaidLabel = Labels.newValueLabel(Languages.safeText(MessageKey.CLIENT_FEE));
    // Add bitcoin unit to client fee label
    LabelDecorator.applyBitcoinSymbolLabel(
            clientFeePaidLabel,
            Configurations.currentConfiguration.getBitcoin(),
            Languages.safeText(MessageKey.CLIENT_FEE) + " ");
    clientFeePaidValue = Labels.newValueLabel("");

    exchangeRateLabel = Labels.newValueLabel(Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL));
    exchangeRateValue = Labels.newValueLabel("");

    update();

    contentPanel.add(amountBTCLabel);
    contentPanel.add(amountBTCValue, "wrap");

    contentPanel.add(amountFiatLabel);
    contentPanel.add(amountFiatValue, "wrap");

    contentPanel.add(exchangeRateLabel);
    contentPanel.add(exchangeRateValue, "wrap");

    contentPanel.add(miningFeePaidLabel);
    contentPanel.add(miningFeePaidValue, "wrap");

    contentPanel.add(clientFeePaidLabel);
    contentPanel.add(clientFeePaidValue, "wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {
    PanelDecorator.addExitCancelPreviousNext(this, wizard);
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getNextButton().requestFocusInWindow();
        getNextButton().setEnabled(true);
      }
    });

    update();
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing
  }

  public void update() {

    PaymentData paymentData = getWizardModel().getPaymentData();

    if (paymentData != null) {
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();

      updateAmountCoin(paymentData, languageConfiguration, bitcoinConfiguration);

      updateAmountFiat(paymentData, languageConfiguration, bitcoinConfiguration);

      if (paymentData instanceof TransactionData) {
        TransactionData transactionData = (TransactionData) paymentData;

        // Get the transaction info again directly from the WalletService to make sure the minging fee is up to date
        // Otherwise use sentBySelf on corresponding transactionInfo
        Optional<Coin> miningFee = Optional.absent();

        Optional<WalletService> walletService = CoreServices.getCurrentWalletService();
        if (walletService.isPresent()) {
          TransactionInfo transactionInfo = walletService.get().getTransactionInfoByHash(transactionData.getTransactionId());

          if (transactionInfo != null) {
            miningFee = transactionInfo.getMinerFee();
          }
        }
        // Miner's fee
        updateMiningFee(languageConfiguration, bitcoinConfiguration, miningFee);

        // Client fee
        updateClientFee(languageConfiguration, bitcoinConfiguration, transactionData);

        if (transactionData.getAmountCoin().compareTo(Coin.ZERO) >= 0) {

          // Received bitcoin
          // Client and mining fee is not applicable
          clientFeePaidValue.setText(Languages.safeText(MessageKey.NOT_AVAILABLE));
          clientFeePaidLabel.setVisible(false);
          clientFeePaidValue.setVisible(false);
          miningFeePaidLabel.setVisible(false);
          miningFeePaidValue.setVisible(false);
        } else {

          // Sent bitcoin
          clientFeePaidLabel.setVisible(true);
          clientFeePaidValue.setVisible(true);
          miningFeePaidLabel.setVisible(true);
          miningFeePaidValue.setVisible(true);
        }
      }

      if (paymentData.getAmountFiat() != null && paymentData.getAmountFiat().getCurrency().isPresent()) {
        // Add bitcoin unit to exchange rate label
        LabelDecorator.applyBitcoinSymbolLabel(
                exchangeRateLabel,
                Configurations.currentConfiguration.getBitcoin(),
                Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL) + " " + paymentData.getAmountFiat().getCurrency().get().getCurrencyCode()
                        + " / ");
      } else {
        exchangeRateLabel.setText(Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL));
      }

      String exchangeRateText = "";
      if (paymentData.getAmountFiat() != null) {
        if (Strings.isNullOrEmpty(paymentData.getAmountFiat().getRate().or("")) || Strings.isNullOrEmpty(paymentData.getAmountFiat().getExchangeName().or(""))) {
          exchangeRateText = Languages.safeText(MessageKey.NOT_AVAILABLE);
        } else {
          // Convert the exchange rate (which is always stored as fiat currency per bitcoin) to match the unit of bitcoin being used
          String convertedExchangeRateText = Formats.formatExchangeRate(paymentData.getAmountFiat().getRate(), languageConfiguration, bitcoinConfiguration);
          exchangeRateText = convertedExchangeRateText + " (" + paymentData.getAmountFiat().getExchangeName().or("") + ")";
        }
      }
      exchangeRateValue.setText(exchangeRateText);
    }
  }

  private void updateClientFee(LanguageConfiguration languageConfiguration, BitcoinConfiguration bitcoinConfiguration, TransactionData transactionData) {
    Optional<Coin> clientFee = transactionData.getClientFee();
    if (clientFee.isPresent()) {
      String[] clientFeePaidArray = Formats.formatCoinAsSymbolic(clientFee.get().negate(), languageConfiguration, bitcoinConfiguration, true);
      clientFeePaidValue.setText(clientFeePaidArray[0] + clientFeePaidArray[1]);
    } else {
      clientFeePaidValue.setText(Languages.safeText(MessageKey.NO_CLIENT_FEE_WAS_ADDED));
    }
  }

  private void updateMiningFee(LanguageConfiguration languageConfiguration, BitcoinConfiguration bitcoinConfiguration,  Optional<Coin> miningFee) {
    if (miningFee.isPresent()) {
      String[] minerFeePaidArray = Formats.formatCoinAsSymbolic(miningFee.get().negate(), languageConfiguration, bitcoinConfiguration, true);
      miningFeePaidValue.setText(minerFeePaidArray[0] + minerFeePaidArray[1]);
    } else {
      miningFeePaidValue.setText(Languages.safeText(MessageKey.NOT_AVAILABLE));
    }
  }

  private void updateAmountCoin(PaymentData paymentData, LanguageConfiguration languageConfiguration, BitcoinConfiguration bitcoinConfiguration) {
    Coin amountCoin = paymentData.getAmountCoin();

    MessageKey messageKey = getMessageKeyForAmount(paymentData);

    // Add bitcoin unit to amount label
    LabelDecorator.applyBitcoinSymbolLabel(
            amountBTCLabel,
            Configurations.currentConfiguration.getBitcoin(),
            Languages.safeText(messageKey) + " ");

    String[] balanceArray = Formats.formatCoinAsSymbolic(amountCoin, languageConfiguration, bitcoinConfiguration, true);
    amountBTCValue.setText(balanceArray[0] + balanceArray[1]);
  }

  private void updateAmountFiat(PaymentData paymentData, LanguageConfiguration languageConfiguration, BitcoinConfiguration bitcoinConfiguration) {
    FiatPayment amountFiat = paymentData.getAmountFiat();
    if (amountFiat != null && amountFiat.getAmount().isPresent()) {
      amountFiatValue.setText((Formats.formatLocalAmount(amountFiat.getAmount().get(), languageConfiguration.getLocale(), bitcoinConfiguration, true)));
    } else {
      amountFiatValue.setText("");
    }

    MessageKey messageKey = getMessageKeyForAmount(paymentData);

    if (amountFiat != null && amountFiat.getCurrency().isPresent()) {
      amountFiatLabel = Labels.newValueLabel(Languages.safeText(messageKey) + " " + amountFiat.getCurrency().get().getCurrencyCode());
    } else {
      amountFiatLabel = Labels.newValueLabel(Languages.safeText(messageKey));
    }
  }

  private MessageKey getMessageKeyForAmount(PaymentData paymentData) {
    Coin amountCoin = paymentData.getAmountCoin();

    if (amountCoin.compareTo(Coin.ZERO) >= 0) {
      // Receive
      return MessageKey.LOCAL_AMOUNT;
    } else {
      // Send
      return MessageKey.LOCAL_AMOUNT_INCLUDING_FEES;
    }
  }
}
