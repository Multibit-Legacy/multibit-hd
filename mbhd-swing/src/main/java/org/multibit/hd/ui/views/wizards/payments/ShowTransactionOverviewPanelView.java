package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.PaymentData;
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
 * <li>Show transaction overview</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ShowTransactionOverviewPanelView extends AbstractWizardPanelView<PaymentsWizardModel, ShowTransactionOverviewPanelModel> {

  private static final Logger log = LoggerFactory.getLogger(ShowTransactionOverviewPanelView.class);

  private JLabel dateValue;
  private JLabel statusValue;
  private JLabel typeValue;
  private JLabel descriptionValue;
  private JLabel amountBTCValue;
  private JLabel amountFiatValue;
  private JLabel exchangeRateValue;

  /**
   * @param wizard The wizard managing the states
   */
  public ShowTransactionOverviewPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TRANSACTION_OVERVIEW, AwesomeIcon.FILE_TEXT_ALT);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    ShowTransactionOverviewPanelModel panelModel = new ShowTransactionOverviewPanelModel(
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

    JLabel typeLabel = Labels.newValueLabel(Languages.safeText(MessageKey.TYPE));
    typeValue = Labels.newValueLabel("");

    JLabel descriptionLabel = Labels.newValueLabel(Languages.safeText(MessageKey.DESCRIPTION));
    descriptionValue = Labels.newValueLabel("");

    JLabel amountBTCLabel = Labels.newValueLabel("");
    amountBTCValue = Labels.newValueLabel("");
    // Bitcoin column
    LabelDecorator.applyBitcoinSymbolLabel(
            amountBTCLabel,
            Configurations.currentConfiguration.getBitcoinConfiguration(),
            Languages.safeText(MessageKey.AMOUNT) + " ");

    JLabel amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.AMOUNT) + " " + Configurations.currentConfiguration.getBitcoinConfiguration().getLocalCurrencySymbol());
    amountFiatValue = Labels.newValueLabel("");

    JLabel exchangeRateLabel = Labels.newValueLabel(Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL));
    exchangeRateValue = Labels.newValueLabel("");

    update();

    contentPanel.add(statusLabel);
    contentPanel.add(statusValue, "wrap");
    contentPanel.add(dateLabel);
    contentPanel.add(dateValue, "wrap");
    contentPanel.add(typeLabel);
    contentPanel.add(typeValue, "wrap");
    contentPanel.add(descriptionLabel);
    contentPanel.add(descriptionValue, "wrap");
    contentPanel.add(amountBTCLabel);
    contentPanel.add(amountBTCValue, "wrap");
    contentPanel.add(amountFiatLabel);
    contentPanel.add(amountFiatValue, "wrap");
    contentPanel.add(exchangeRateLabel);
    contentPanel.add(exchangeRateValue, "wrap");
  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {
    PanelDecorator.addExitCancelNext(this, wizard);
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
      DateTime date = paymentData.getDate();
      dateValue.setText(LocalisedDateUtils.formatFriendlyDate(date));

      descriptionValue.setText(paymentData.getDescription());

      statusValue.setText(Languages.safeText(paymentData.getStatus().getStatusKey(), paymentData.getStatus().getStatusData()));
      LabelDecorator.applyStatusIconAndColor(paymentData, statusValue, MultiBitUI.SMALL_ICON_SIZE);

      typeValue.setText(Languages.safeText(paymentData.getType().getLocalisationKey()));

      BigInteger amountBTC = paymentData.getAmountBTC();
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguageConfiguration();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();

      String[] balanceArray = Formats.formatSatoshisAsSymbolic(amountBTC, languageConfiguration, bitcoinConfiguration);
      amountBTCValue.setText(balanceArray[0] + balanceArray[1]);

      FiatPayment amountFiat = paymentData.getAmountFiat();
      amountFiatValue.setText((Formats.formatLocalAmount(amountFiat.getAmount(), languageConfiguration.getLocale(), bitcoinConfiguration)));

      String exchangeRateText;
      if (Strings.isNullOrEmpty(paymentData.getAmountFiat().getRate()) || Strings.isNullOrEmpty(paymentData.getAmountFiat().getExchange() )) {
        exchangeRateText = Languages.safeText(MessageKey.NOT_AVAILABLE);
      } else {
        exchangeRateText = paymentData.getAmountFiat().getRate() + " (" + paymentData.getAmountFiat().getExchange() + ")";
      }
      exchangeRateValue.setText(exchangeRateText);
      if (paymentData instanceof TransactionData) {
        // It should be as payment requests are routed to their own screen but check all the same
        TransactionData transactionData = (TransactionData) paymentData;
        Optional<BigInteger> feeOnSendBTC = transactionData.getFeeOnSendBTC(); // TODO not currently being remembered when tx is sent hence not available
      }
    }
  }
}
