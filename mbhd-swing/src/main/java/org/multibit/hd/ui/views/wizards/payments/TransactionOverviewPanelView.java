package org.multibit.hd.ui.views.wizards.payments;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.Coin;
import com.google.bitcoin.core.NetworkParameters;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.miginfocom.swing.MigLayout;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.config.BitcoinNetwork;
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
import org.multibit.hd.ui.utils.HtmlUtils;
import org.multibit.hd.ui.utils.LocalisedDateUtils;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;
import java.awt.image.BufferedImage;
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
public class TransactionOverviewPanelView extends AbstractWizardPanelView<PaymentsWizardModel, TransactionOverviewPanelModel> {

  private JLabel dateValue;
  private JLabel statusValue;
  private JLabel typeValue;
  private JLabel descriptionValue;
  private JLabel recipientValue;
  private JLabel recipientImageLabel;
  private JLabel amountBTCValue;
  private JLabel amountFiatLabel;
  private JLabel amountFiatValue;
  private JLabel miningFeePaidLabel;
  private JLabel miningFeePaidValue;
  private JLabel clientFeePaidLabel;
  private JLabel clientFeePaidValue;
  private JLabel exchangeRateValue;

  // TODO Inject this
  private final NetworkParameters networkParameters = BitcoinNetwork.current().get();

  /**
   * @param wizard The wizard managing the states
   */
  public TransactionOverviewPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TRANSACTION_OVERVIEW, AwesomeIcon.FILE_TEXT_O);

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
      "[][][][]", // Column constraints
      "[]10" // Row constraints
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
      Configurations.currentConfiguration.getBitcoin(),
      Languages.safeText(MessageKey.LOCAL_AMOUNT) + " ");

    amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.LOCAL_AMOUNT));
    amountFiatValue = Labels.newValueLabel("");

    miningFeePaidLabel = Labels.newValueLabel(Languages.safeText(MessageKey.TRANSACTION_FEE));
    miningFeePaidValue = Labels.newValueLabel("");

    clientFeePaidLabel = Labels.newValueLabel(Languages.safeText(MessageKey.CLIENT_FEE));
    clientFeePaidValue = Labels.newValueLabel("");

    JLabel exchangeRateLabel = Labels.newValueLabel(Languages.safeText(MessageKey.EXCHANGE_RATE_LABEL));
    exchangeRateValue = Labels.newValueLabel("");

    update();

    contentPanel.add(recipientLabel,"growx,push");
    contentPanel.add(recipientValue, "growx,span 2,push");
    contentPanel.add(recipientImageLabel, "shrink,align right,wrap");

    contentPanel.add(statusLabel);
    contentPanel.add(statusValue, "span 3,wrap");

    contentPanel.add(typeLabel);
    contentPanel.add(typeValue, "growx,push");
    contentPanel.add(dateLabel);
    contentPanel.add(dateValue, "wrap");

    contentPanel.add(amountBTCLabel);
    contentPanel.add(amountBTCValue);
    contentPanel.add(amountFiatLabel);
    contentPanel.add(amountFiatValue, "wrap");

    contentPanel.add(miningFeePaidLabel);
    contentPanel.add(miningFeePaidValue);
    contentPanel.add(clientFeePaidLabel);
    contentPanel.add(clientFeePaidValue, "wrap");

    contentPanel.add(exchangeRateLabel);
    contentPanel.add(exchangeRateValue, "span 3,wrap");

    contentPanel.add(descriptionLabel);
    contentPanel.add(descriptionValue, "grow,push,span 3,wrap");

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
      LanguageConfiguration languageConfiguration = Configurations.currentConfiguration.getLanguage();
      BitcoinConfiguration bitcoinConfiguration = Configurations.currentConfiguration.getBitcoin();

      updateMetadata(paymentData, date);

      updateAmountCoin(paymentData, languageConfiguration, bitcoinConfiguration);

      updateAmountFiat(paymentData, languageConfiguration, bitcoinConfiguration);

      if (paymentData instanceof TransactionData) {
        TransactionData transactionData = (TransactionData) paymentData;

        // Miner's fee
        updateMiningFee(languageConfiguration, bitcoinConfiguration, transactionData);

        // Client fee
        updateClientFee(languageConfiguration, bitcoinConfiguration, transactionData);

        if (transactionData.getAmountCoin().compareTo(Coin.ZERO) >= 0) {

          // Received bitcoin
          recipientValue.setText(Languages.safeText(MessageKey.THIS_BITCOIN_WAS_SENT_TO_YOU));

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

          // Contact may be one of the output addresses
          Collection<String> addressList = transactionData.getOutputAddresses();

          Contact matchedContact = matchContact(addressList);
          if (matchedContact == null) {
            recipientValue.setText(Languages.safeText(MessageKey.NOT_AVAILABLE));
          }
        }
      }

      String exchangeRateText;
      if (Strings.isNullOrEmpty(paymentData.getAmountFiat().getRate().or("")) || Strings.isNullOrEmpty(paymentData.getAmountFiat().getExchangeName().or(""))) {
        exchangeRateText = Languages.safeText(MessageKey.NOT_AVAILABLE);
      } else {
        exchangeRateText = paymentData.getAmountFiat().getRate().or("") + " (" + paymentData.getAmountFiat().getExchangeName().or("") + ")";
      }
      exchangeRateValue.setText(exchangeRateText);
    }
  }

  private Contact matchContact(Collection<String> addressList) {

    // This is a bit inefficient - could have a hashmap of Contacts, keyed by address
    // Or store the address sent to
    ContactService contactService = CoreServices.getOrCreateContactService(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletId());
    List<Contact> allContacts = contactService.allContacts();

    Contact matchedContact = null;

    for (Contact contact : allContacts) {

      if (addressList != null) {
        for (String address : addressList) {
          if (contact.getBitcoinAddress().isPresent() && contact.getBitcoinAddress().get().equals(address)) {

            // This is a contact for this address
            final Address bitcoinAddress;
            try {
              bitcoinAddress = new Address(networkParameters, address);
            } catch (AddressFormatException e) {
              // If this occurs we really want to know
              throw new IllegalArgumentException("Contact has an incorrect Bitcoin address: " + contact, e);
            }

            // Only show the first match
            recipientValue.setText(contact.getName());
            Recipient matchedRecipient = new Recipient(bitcoinAddress);
            matchedRecipient.setContact(contact);
            matchedContact = contact;

            displayGravatar(contact, recipientImageLabel);
            break;
          }
        }
      }

    }

    return matchedContact;
  }

  private void updateClientFee(LanguageConfiguration languageConfiguration, BitcoinConfiguration bitcoinConfiguration, TransactionData transactionData) {

    Optional<Coin> clientFee = transactionData.getClientFee();
    if (clientFee.isPresent()) {
      String[] clientFeePaidArray = Formats.formatCoinAsSymbolic(clientFee.get(), languageConfiguration, bitcoinConfiguration, true);
      clientFeePaidValue.setText(clientFeePaidArray[0] + clientFeePaidArray[1]);
    } else {
      clientFeePaidValue.setText(Languages.safeText(MessageKey.NO_CLIENT_FEE_WAS_ADDED));
    }

  }

  private void updateMiningFee(LanguageConfiguration languageConfiguration, BitcoinConfiguration bitcoinConfiguration, TransactionData transactionData) {

    Optional<Coin> miningFee = transactionData.getMiningFee();
    if (miningFee.isPresent()) {
      String[] minerFeePaidArray = Formats.formatCoinAsSymbolic(miningFee.get(), languageConfiguration, bitcoinConfiguration, true);
      miningFeePaidValue.setText(minerFeePaidArray[0] + minerFeePaidArray[1]);
    } else {
      miningFeePaidValue.setText(Languages.safeText(MessageKey.NOT_AVAILABLE));
    }

  }

  private void updateMetadata(PaymentData paymentData, DateTime date) {

    // Display in the system timezone
    dateValue.setText(LocalisedDateUtils.formatFriendlyDateLocal(date));

    // Description may be long so ensure we wrap the label
    String descriptionHtml = HtmlUtils.localiseWithLineBreaks(new String[]{paymentData.getDescription()});
    descriptionValue.setText(descriptionHtml);

    statusValue.setText(Languages.safeText(paymentData.getStatus().getStatusKey(), paymentData.getStatus().getStatusData()));
    LabelDecorator.applyPaymentStatusIconAndColor(paymentData.getStatus(), statusValue, paymentData.isCoinBase(), MultiBitUI.SMALL_ICON_SIZE);

    typeValue.setText(Languages.safeText(paymentData.getType().getLocalisationKey()));

  }

  private void updateAmountCoin(PaymentData paymentData, LanguageConfiguration languageConfiguration, BitcoinConfiguration bitcoinConfiguration) {

    Coin amountCoin = paymentData.getAmountCoin();

    String[] balanceArray = Formats.formatCoinAsSymbolic(amountCoin, languageConfiguration, bitcoinConfiguration, true);
    amountBTCValue.setText(balanceArray[0] + balanceArray[1]);

  }

  private void updateAmountFiat(PaymentData paymentData, LanguageConfiguration languageConfiguration, BitcoinConfiguration bitcoinConfiguration) {

    FiatPayment amountFiat = paymentData.getAmountFiat();
    if (amountFiat.getAmount().isPresent()) {
      amountFiatValue.setText((Formats.formatLocalAmount(amountFiat.getAmount().get(), languageConfiguration.getLocale(), bitcoinConfiguration, true)));
    } else {
      amountFiatValue.setText("");
    }

    if (amountFiat.getCurrency().isPresent()) {
      amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.LOCAL_AMOUNT) + " " + amountFiat.getCurrency().get().getCurrencyCode());
    } else {
      amountFiatLabel = Labels.newValueLabel(Languages.safeText(MessageKey.LOCAL_AMOUNT));
    }

  }

  // Display a gravatar if we have a contact
  private void displayGravatar(Contact contact, final JLabel recipientImageLabel) {

    // Attempt to find an email address
    String emailAddress = contact.getEmail().or("nobody@example.org");

    final ListenableFuture<Optional<BufferedImage>> imageFuture = Gravatars.retrieveGravatar(emailAddress);
    Futures.addCallback(imageFuture, new FutureCallback<Optional<BufferedImage>>() {
      public void onSuccess(Optional<BufferedImage> image) {
        if (image.isPresent()) {

          // Apply the rounded corners
          ImageIcon imageIcon = new ImageIcon(ImageDecorator.applyRoundedCorners(image.get(), MultiBitUI.IMAGE_CORNER_RADIUS));

          recipientImageLabel.setIcon(imageIcon);
        } else {
          // Update the UI to use the "no network" icon
          recipientImageLabel.setIcon(Images.newNoNetworkContactImageIcon());
        }

        recipientImageLabel.setVisible(true);

      }

      public void onFailure(Throwable thrown) {
        // Update the UI to use the "no network" icon
        recipientImageLabel.setIcon(Images.newNoNetworkContactImageIcon());
        recipientImageLabel.setVisible(true);
      }
    });

  }
}
