package org.multibit.hd.ui.views.wizards.buy_sell;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Coin;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.FiatPayment;
import org.multibit.hd.core.dto.MBHDPaymentRequestData;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.error_reporting.ExceptionHandler;
import org.multibit.hd.core.exceptions.PaymentsSaveException;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.BitcoinNetworkService;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.core.services.WalletService;
import org.multibit.hd.core.utils.Addresses;
import org.multibit.hd.ui.audio.Sounds;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.utils.SafeDesktop;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.wallet_detail.WalletDetail;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Currency;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Show details and buy, Sell buttons</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class BuySellSelectPanelView extends AbstractWizardPanelView<BuySellWizardModel, String> {

  private static final Logger log = LoggerFactory.getLogger(BuySellSelectPanelView.class);

  // The buy referral URI text, without a bitcoin address appended (this is added late)
  private static final String buyUriText = "https://www.glidera.io/referral?client_id=700a89b3cf4ce97d08bb58ea4e550f38&buydestinationaddress=";

  private static final URI sellUri = URI.create("https://www.glidera.io/referral?client_id=700a89b3cf4ce97d08bb58ea4e550f38");

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to allow event filtering
   */
  public BuySellSelectPanelView(AbstractWizard<BuySellWizardModel> wizard, String panelName) {

    super(wizard, panelName, AwesomeIcon.CREDIT_CARD, MessageKey.BUY_SELL_TITLE);

  }

  @Override
  public void newPanelModel() {

    setPanelModel("");

    // No wizard model
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[300][240][30]", // Column constraints
      "[][]10" // Row constraints
    ));

    contentPanel.add(Labels.newBuySellRegionNote(), "span 3, growx, wrap");
    contentPanel.add(Buttons.newLaunchBrowserButton(getBuyLaunchBrowserAction(), MessageKey.BUY_VISIT_GLIDERA, MessageKey.BUY_VISIT_GLIDERA_TOOLTIP), "align center");
    contentPanel.add(Buttons.newLaunchBrowserButton(getSellLaunchBrowserAction(),MessageKey.SELL_VISIT_GLIDERA, MessageKey.SELL_VISIT_GLIDERA_TOOLTIP), "align center, wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<BuySellWizardModel> wizard) {

    PanelDecorator.addFinish(this, wizard);

  }

  @Override
  public void afterShow() {

    getFinishButton().requestFocusInWindow();

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  /**
   * @return The "launch browser" action for the Buy button
   */
  private Action getBuyLaunchBrowserAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String glideraBuyAddress =  getGlideraBuyAddress();
        String uriText = buyUriText + glideraBuyAddress;
        log.debug("Glidera buy URI text: {}", uriText);
        URI buyUri = URI.create(uriText);

        // Add a payment request for the Glidera address
        savePaymentRequest(glideraBuyAddress);

        if (!SafeDesktop.browse(buyUri)) {
          Sounds.playBeep(Configurations.currentConfiguration.getSound());
        }

      }
    };
  }

  /**
    * @return The "launch browser" action for the Sell button
    */
   private Action getSellLaunchBrowserAction() {

     return new AbstractAction() {
       @Override
       public void actionPerformed(ActionEvent e) {

         if (!SafeDesktop.browse(sellUri)) {
           Sounds.playBeep(Configurations.currentConfiguration.getSound());
         }

       }
     };
   }

  /**
   * Get the Bitcoin address (in the current wallet) to add onto the 'buy bitcoin' URL.
   * This tells Glidera the address to use to send bitcoin to
   *
   * @return String the Bitcoin address
   */
  private String getGlideraBuyAddress() {
    // Get the next receiving address to show from the wallet service.
    // This is normally a new receiving address but if the gap limit is reached it is the current one
    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    Optional<CharSequence> passwordParameter = Optional.absent();
    CharSequence password = currentWalletSummary.get().getWalletPassword().getPassword();
    if (currentWalletSummary.isPresent()) {
      if (!(password == null) && !"".equals(password)) {
        passwordParameter = Optional.of(password);
      }
    }

    WalletService walletService;
    if (CoreServices.getCurrentWalletService().isPresent()) {
      walletService = CoreServices.getCurrentWalletService().get();
    } else {
      if (WalletManager.INSTANCE.getCurrentWalletSummary().isPresent()) {
        walletService = CoreServices.getOrCreateWalletService(WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletId());
      } else {
        throw new IllegalStateException("Cannot create WalletService to create a new address");
      }
    }

    String nextAddressToShow;

    Optional<Integer> gap = walletService.getGap();
    log.debug("current gap: {}", gap);
    boolean atGapLimit = gap.isPresent() && gap.get() >= WalletService.GAP_LIMIT;

    if (atGapLimit) {
      nextAddressToShow = walletService.getLastGeneratedReceivingAddress();
    } else {
      nextAddressToShow = walletService.generateNextReceivingAddress(passwordParameter);

      // Recreate bloom filter
      BitcoinNetworkService bitcoinNetworkService = CoreServices.getOrCreateBitcoinNetworkService();
      Preconditions.checkState(bitcoinNetworkService.isStartedOk(), "'bitcoinNetworkService' should be started OK");
      bitcoinNetworkService.recalculateFastCatchupAndFilter();
    }

    return nextAddressToShow;
  }

  /**
    * Save the displayed payment request
    */
   private void savePaymentRequest(String buyAddress) {

     log.debug("Saving payment request");
     WalletService walletService = CoreServices.getCurrentWalletService().get();

     // Fail fast
     Preconditions.checkNotNull(walletService, "'walletService' must be present");
     Preconditions.checkState(WalletManager.INSTANCE.getCurrentWalletSummary().isPresent(), "'currentWalletSummary' must be present");

     final MBHDPaymentRequestData MBHDPaymentRequestData = new MBHDPaymentRequestData();
     MBHDPaymentRequestData.setNote("");
     MBHDPaymentRequestData.setDate(DateTime.now());
     MBHDPaymentRequestData.setAddress(Addresses.parse(buyAddress).get());
     MBHDPaymentRequestData.setLabel(Languages.safeText(MessageKey.BUY_VISIT_GLIDERA));
     MBHDPaymentRequestData.setAmountCoin(Optional.<Coin>absent());

     final FiatPayment fiatPayment = new FiatPayment();
     fiatPayment.setAmount(Optional.<BigDecimal>absent());

     final ExchangeKey exchangeKey = ExchangeKey.current();
     fiatPayment.setExchangeName(Optional.of(exchangeKey.getExchangeName()));
     fiatPayment.setRate(Optional.<String>absent());
     fiatPayment.setCurrency(Optional.<Currency>absent());

     MBHDPaymentRequestData.setAmountFiat(fiatPayment);

     walletService.addMBHDPaymentRequestData(MBHDPaymentRequestData);
     try {
       log.debug("Saving payment information");
       CharSequence password = WalletManager.INSTANCE.getCurrentWalletSummary().get().getWalletPassword().getPassword();
       if (password != null) {
         walletService.writePayments(password);
       }
     } catch (PaymentsSaveException pse) {
       ExceptionHandler.handleThrowable(pse);
     }

     // Ensure the views that display payments update through a "wallet detail changed" event
     final WalletDetail walletDetail = new WalletDetail();

     final File applicationDataDirectory = InstallationManager.getOrCreateApplicationDataDirectory();
     final File walletFile = WalletManager.INSTANCE.getCurrentWalletFile(applicationDataDirectory).get();

     final WalletSummary walletSummary = WalletManager.INSTANCE.getCurrentWalletSummary().get();

     ContactService contactService = CoreServices.getOrCreateContactService(walletSummary.getWalletPassword());

     walletDetail.setApplicationDirectory(applicationDataDirectory.getAbsolutePath());
     walletDetail.setWalletDirectory(walletFile.getParentFile().getName());
     walletDetail.setNumberOfContacts(contactService.allContacts().size());
     walletDetail.setNumberOfPayments(walletService.getPaymentDataSetSize());

     log.debug("A new receiving address has been issued for Glidera. The number of external keys is now {}", walletSummary.getWallet().getActiveKeychain().getIssuedExternalKeys());

     SwingUtilities.invokeLater(new Runnable() {
       @Override
       public void run() {
         ViewEvents.fireWalletDetailChangedEvent(walletDetail);
       }
     });
   }
}
