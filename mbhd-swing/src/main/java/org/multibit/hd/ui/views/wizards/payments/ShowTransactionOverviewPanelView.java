package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.config.LanguageConfiguration;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.gravatar.Gravatars;
import org.multibit.hd.ui.languages.Formats;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.LocalisedDateUtils;
import org.multibit.hd.ui.views.components.ImageDecorator;
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
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

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
  private JLabel recipientValue;
  private JLabel recipientImageLabel;
  private JLabel amountBTCValue;
  private JLabel amountFiatValue;
  private JLabel minerFeePaidValue;
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

    JLabel recipientLabel = Labels.newValueLabel(Languages.safeText(MessageKey.RECIPIENT));
    recipientValue = Labels.newValueLabel("");

    // Start with an invisible label
    recipientImageLabel = Labels.newImageLabel(Optional.<BufferedImage>absent());
    recipientImageLabel.setVisible(false);

    JLabel amountBTCLabel = Labels.newValueLabel("");

    amountBTCValue = Labels.newValueLabel("");
    // Bitcoin column
    LabelDecorator.applyBitcoinSymbolLabel(
            amountBTCLabel,
            Configurations.currentConfiguration.getBitcoinConfiguration(),
            Languages.safeText(MessageKey.AMOUNT) + " ");

    JLabel amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.AMOUNT) + " " + Configurations.currentConfiguration.getBitcoinConfiguration().getLocalCurrencySymbol());
    amountFiatValue = Labels.newValueLabel("");

    JLabel minerFeePaidLabel = Labels.newValueLabel(Languages.safeText(MessageKey.TRANSACTION_FEE));
    minerFeePaidValue = Labels.newValueLabel("");

    JLabel exchangeRateLabel = Labels.newValueLabel(Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL));
    exchangeRateValue = Labels.newValueLabel("");

    update();

    contentPanel.add(statusLabel);
    contentPanel.add(statusValue, "span 2, wrap");
    contentPanel.add(dateLabel);
    contentPanel.add(dateValue, "span 2, wrap");
    contentPanel.add(typeLabel);
    contentPanel.add(typeValue, "span 2, wrap");
    contentPanel.add(descriptionLabel);
    contentPanel.add(descriptionValue, "span 2, wrap");
    contentPanel.add(recipientLabel);
    contentPanel.add(recipientValue);
    contentPanel.add(recipientImageLabel, "span 2, wrap");
    contentPanel.add(amountBTCLabel);
    contentPanel.add(amountBTCValue, "span 2, wrap");
    contentPanel.add(amountFiatLabel);
    contentPanel.add(amountFiatValue, "span 2, wrap");
    contentPanel.add(minerFeePaidLabel);
    contentPanel.add(minerFeePaidValue, "span 2, wrap");
    contentPanel.add(exchangeRateLabel);
    contentPanel.add(exchangeRateValue, "span 2, wrap");
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
      LabelDecorator.applyStatusIconAndColor(paymentData.getStatus(), statusValue, paymentData.isCoinBase(), MultiBitUI.SMALL_ICON_SIZE);

      typeValue.setText(Languages.safeText(paymentData.getType().getLocalisationKey()));

      BigInteger amountBTC = paymentData.getAmountBTC();
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguageConfiguration();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoinConfiguration();

      String[] balanceArray = Formats.formatSatoshisAsSymbolic(amountBTC, languageConfiguration, bitcoinConfiguration, true);
      amountBTCValue.setText(balanceArray[0] + balanceArray[1]);

      FiatPayment amountFiat = paymentData.getAmountFiat();
      amountFiatValue.setText((Formats.formatLocalAmount(amountFiat.getAmount(), languageConfiguration.getLocale(), bitcoinConfiguration, true)));

      if (paymentData instanceof TransactionData) {
        TransactionData transactionData = (TransactionData) paymentData;
        // Miner's fee
        Optional<BigInteger> feeOnSend = transactionData.getFeeOnSendBTC();
        if (feeOnSend.isPresent()) {
          String[] minerFeePaidArray = Formats.formatSatoshisAsSymbolic(feeOnSend.get(), languageConfiguration, bitcoinConfiguration, true);
          minerFeePaidValue.setText(minerFeePaidArray[0] + minerFeePaidArray[1]);
        } else {
          minerFeePaidValue.setText(Languages.safeText(MessageKey.NOT_AVAILABLE));
        }

        // Contact may be one of the output addresses
        Collection<String> addressList = transactionData.getOutputAddresses();
        // This is a bit inefficient - could have a hashmap of Contacts, keyed by address
        // Or store the address sent to
        ContactService contactService = CoreServices.getOrCreateContactService(Optional.of(WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId()));
        List<Contact> allContacts = contactService.allContacts();
        Contact matchedContact = null;

        if (allContacts != null) {
          for (Contact contact : allContacts) {
            if (addressList != null) {
              for (String address : addressList) {
                if (contact.getBitcoinAddress().isPresent() && contact.getBitcoinAddress().get().equals(address)) {
                  // This is a contact for this address
                  // Keep show the first
                  recipientValue.setText(contact.getName());
                  Recipient matchedRecipient = new Recipient(address);
                  matchedRecipient.setContact(contact);
                  matchedContact = contact;

                  displayGravatar(contact, recipientImageLabel);
                  break;
                }
              }
            }
          }
        }
        if (matchedContact == null) {
          recipientValue.setText(Languages.safeText(MessageKey.NOT_AVAILABLE));
        }
      }

      String exchangeRateText;
      if (Strings.isNullOrEmpty(paymentData.getAmountFiat().getRate()) || Strings.isNullOrEmpty(paymentData.getAmountFiat().getExchange())) {
        exchangeRateText = Languages.safeText(MessageKey.NOT_AVAILABLE);
      } else {
        exchangeRateText = paymentData.getAmountFiat().getRate() + " (" + paymentData.getAmountFiat().getExchange() + ")";
      }
      exchangeRateValue.setText(exchangeRateText);
    }
  }

  // Display a gravatar if we have a contact
  private void displayGravatar(Contact contact, final JLabel recipientImageLabel) {
    if (contact.getEmail().isPresent() && !Strings.isNullOrEmpty(contact.getEmail().get())) {

      // We have an email address
      String emailAddress = contact.getEmail().get();

      final ListenableFuture<Optional<BufferedImage>> imageFuture = Gravatars.retrieveGravatar(emailAddress);
      Futures.addCallback(imageFuture, new FutureCallback<Optional<BufferedImage>>() {
        public void onSuccess(Optional<BufferedImage> image) {
          if (image.isPresent()) {

            // Apply the rounded corners
            ImageIcon imageIcon = new ImageIcon(ImageDecorator.applyRoundedCorners(image.get(), MultiBitUI.IMAGE_CORNER_RADIUS));

            recipientImageLabel.setIcon(imageIcon);
            recipientImageLabel.setVisible(true);
          }
        }

        public void onFailure(Throwable thrown) {
          recipientImageLabel.setVisible(false);
        }
      });
    } else {
      recipientImageLabel.setVisible(false);
    }
  }
}
