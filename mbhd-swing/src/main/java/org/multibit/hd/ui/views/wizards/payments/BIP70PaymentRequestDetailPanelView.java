package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.core.Coin;
import org.joda.time.DateTime;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.BitcoinNetworkSummary;
import org.multibit.hd.core.dto.CoreMessageKey;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.events.BitcoinNetworkChangedEvent;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.view.ViewEvents;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.*;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountModel;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountStyle;
import org.multibit.hd.ui.views.components.display_amount.DisplayAmountView;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.WizardButton;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinParameter;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Send bitcoin: Display payment request</li>
 * </ul>
 *
 * @since 0.0.8
 */

public class BIP70PaymentRequestDetailPanelView extends AbstractWizardPanelView<PaymentsWizardModel, BIP70PaymentRequestDetailPanelModel> {

  // Panel specific components
  private ModelAndView<DisplayAmountModel, DisplayAmountView> paymentRequestAmountMaV;


  private JLabel statusValue;

  private JLabel trustStatusLabel;
  private JTextArea memo;
  private JLabel displayName;
  private JLabel date;
  private JLabel expires;

  private JButton payThisPaymentRequestButton;

  private static final Logger log = LoggerFactory.getLogger(BIP70PaymentRequestDetailPanelView.class);

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public BIP70PaymentRequestDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {
    super(wizard, panelName, AwesomeIcon.MONEY, MessageKey.DISPLAY_PAYMENT_REQUEST_TITLE);
  }


  @Override
  public void newPanelModel() {
    // Configure the panel model
    final BIP70PaymentRequestDetailPanelModel panelModel = new BIP70PaymentRequestDetailPanelModel(getPanelName());
    setPanelModel(panelModel);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {
    contentPanel.setLayout(
      new MigLayout(
        Panels.migXYLayout(),
        "[][]", // Column constraints
        "[][][][][][][30]" // Row constraints
      ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    JLabel statusLabel = Labels.newValueLabel(Languages.safeText(MessageKey.STATUS));
    statusValue = Labels.newBlankLabel();

    // Payment request amount
    paymentRequestAmountMaV = Components.newDisplayAmountMaV(
      DisplayAmountStyle.TRANSACTION_DETAIL_AMOUNT,
      true,
      SendBitcoinState.SEND_DISPLAY_PAYMENT_REQUEST.name() + ".amount"
    );
    // Show the local amount as we have it
    paymentRequestAmountMaV.getModel().setLocalAmountVisible(true);
    paymentRequestAmountMaV.getView().setVisible(true);

    // Populate value labels
    memo = TextBoxes.newReadOnlyTextArea(4,50);
    memo.setBorder(null);
    AccessibilityDecorator.apply(memo, MessageKey.NOTES);
    // Memo requires its own scroll pane
    JScrollPane scrollPane = ScrollPanes.newReadOnlyScrollPane(memo);


    date = Labels.newValueLabel(Languages.safeText(MessageKey.NOT_AVAILABLE));
    date.setName(MessageKey.DATE.getKey() + ".value");

    expires = Labels.newValueLabel(Languages.safeText(MessageKey.NOT_AVAILABLE));
    expires.setName(MessageKey.EXPIRES.getKey() + ".value");

    displayName = Labels.newValueLabel(Languages.safeText(MessageKey.NOT_AVAILABLE));
    displayName.setName(MessageKey.NAME.getKey() + ".value");

    trustStatusLabel = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    trustStatusLabel.setName("trust_status");
    contentPanel.add(trustStatusLabel, "span 2,aligny top,wrap");

    contentPanel.add(statusLabel);
    contentPanel.add(statusValue, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newMemoLabel(), "shrink");
    contentPanel.add(scrollPane, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newDisplayNameLabel(), "shrink");
    contentPanel.add(displayName, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newDateLabel(), "shrink");
    contentPanel.add(date, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newExpiresLabel(), "shrink");
    contentPanel.add(expires, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newAmount(), "baseline");
    contentPanel.add(paymentRequestAmountMaV.getView().newComponentPanel(), "wrap");

    contentPanel.add(Labels.newBlankLabel(), "");
    payThisPaymentRequestButton = Buttons.newPayThisPaymentRequestButton(createPayThisPaymentRequestAction(getWizardModel().getPaymentRequestData()));
    // Initially inivisible
    payThisPaymentRequestButton.setVisible(false);
    contentPanel.add(payThisPaymentRequestButton, "wrap");

    // Register components
    registerComponents(paymentRequestAmountMaV);
  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {
    if (getWizardModel().isShowPrevOnPaymentRequestDetailScreen()) {
      // In the Payments wizard - show a Cancel, Previous and Next
      PanelDecorator.addExitCancelPreviousNext(this, wizard);
    } else {
      PanelDecorator.addFinish(this, wizard);
    }
  }

  @Override
  public boolean beforeShow() {
    PaymentRequestData paymentRequestData = getWizardModel().getPaymentRequestData();
    Preconditions.checkNotNull(paymentRequestData);

    if (getWizardModel().isShowPrevOnPaymentRequestDetailScreen()) {
      getNextButton().requestFocusInWindow();
      ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);
    } else {
      getFinishButton().requestFocusInWindow();
      ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.FINISH, true);
    }

    // Show the 'pay this payment' button only if it is not already paid
    payThisPaymentRequestButton.setVisible(paymentRequestData.getStatus().getStatusKey() != CoreMessageKey.PAYMENT_PAID);

    // Ensure the pay request button is kept up to date
    Optional<BitcoinNetworkChangedEvent> changedEvent = CoreServices.getApplicationEventService().getLatestBitcoinNetworkChangedEvent();
    if (changedEvent.isPresent()) {
      updatePayRequestButton(changedEvent.get());
    }
    return true;
  }

  @Override
  public void afterShow() {
    PaymentRequestData paymentRequestData = getWizardModel().getPaymentRequestData();
    Preconditions.checkNotNull(paymentRequestData);

    statusValue.setText(Languages.safeText(paymentRequestData.getStatus().getStatusKey(), paymentRequestData.getStatus().getStatusData()));
    LabelDecorator.applyPaymentStatusIconAndColor(paymentRequestData.getStatus(), statusValue, false, MultiBitUI.SMALL_ICON_SIZE);

    memo.setText(paymentRequestData.getNote());
    DateTime paymentRequestDate = new DateTime(paymentRequestData.getDate());
    date.setText(Dates.formatTransactionDateLocal(paymentRequestDate));

    // Update the model and view for the amount
    Configuration configuration = Configurations.currentConfiguration;
    paymentRequestAmountMaV.getModel().setCoinAmount(paymentRequestData.getAmountCoin().or(Coin.ZERO));
    if (paymentRequestData.getAmountFiat().getAmount().isPresent()) {
      paymentRequestAmountMaV.getModel().setLocalAmount(paymentRequestData.getAmountFiat().getAmount().get().abs());
      paymentRequestAmountMaV.getModel().setLocalAmountVisible(true);
    } else {
      paymentRequestAmountMaV.getModel().setLocalAmount(null);
      paymentRequestAmountMaV.getModel().setLocalAmountVisible(false);
    }
    paymentRequestAmountMaV.getView().updateView(configuration);

    displayName.setText(paymentRequestData.getIdentityDisplayName());

    if (paymentRequestData.getExpirationDate() == null) {
      expires.setText(Languages.safeText(MessageKey.NOT_AVAILABLE));
    } else {
      expires.setText(Dates.formatTransactionDateLocal(paymentRequestData.getExpirationDate()));
      // TODO Handle display of expiry and button control
      //            if (expiresDate.isBeforeNow()) {
      //              // This payment request has expired
      //            } else {
      //            }
    }

    switch (paymentRequestData.getTrustStatus()) {
      case TRUSTED:
        LabelDecorator.applyPaymentSessionStatusIcon(
          paymentRequestData.getTrustStatus(),
          trustStatusLabel,
          MessageKey.PAYMENT_PROTOCOL_TRUSTED_NOTE,
          MultiBitUI.NORMAL_ICON_SIZE);
        break;
      case UNTRUSTED:
        LabelDecorator.applyPaymentSessionStatusIcon(
          paymentRequestData.getTrustStatus(),
          trustStatusLabel,
          MessageKey.PAYMENT_PROTOCOL_UNTRUSTED_NOTE,
          MultiBitUI.NORMAL_ICON_SIZE);
        break;
      case DOWN:
      case ERROR:
        // Provide more details on the failure
        LabelDecorator.applyPaymentSessionStatusIcon(
          paymentRequestData.getTrustStatus(),
          trustStatusLabel,
          MessageKey.PAYMENT_PROTOCOL_ERROR_NOTE,
          MultiBitUI.NORMAL_ICON_SIZE);
        memo.setText(paymentRequestData.getTrustErrorMessage());
        return;
      default:
        throw new IllegalStateException("Unknown payment session summary status: " + paymentRequestData.getTrustStatus());
    }

    // Set next/finish button to be the default
    if (getWizardModel().isShowPrevOnPaymentRequestDetailScreen()) {
      getNextButton().requestFocusInWindow();
    } else {
      getFinishButton().requestFocusInWindow();
    }
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  /**
   * @return Action to process the 'Pay this payment request' button
   */
  private Action createPayThisPaymentRequestAction(final PaymentRequestData paymentRequestData) {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Panels.hideLightBoxIfPresent();

        SendBitcoinParameter sendBitcoinParameter = new SendBitcoinParameter(null, Optional.fromNullable(paymentRequestData));
        SendBitcoinWizard sendBitcoinWizard = Wizards.newSendBitcoinWizard(sendBitcoinParameter);
        sendBitcoinWizard.getWizardModel().setLocalAmount(paymentRequestData.getAmountFiat().getAmount());
        sendBitcoinWizard.getWizardModel().prepareWhenBIP70();
        sendBitcoinWizard.show(sendBitcoinWizard.getWizardModel().getPanelName());
        Panels.showLightBox(sendBitcoinWizard.getWizardScreenHolder());
      }
    };
  }

  /**
   * @param event The "Bitcoin network changed" event - one per block downloaded during synchronization
   */
  @Subscribe
  public void onBitcoinNetworkChangeEvent(final BitcoinNetworkChangedEvent event) {

    if (!isInitialised()) {
      return;
    }

    log.trace("Received 'Bitcoin network changed' event: {}", event.getSummary());

    Preconditions.checkNotNull(event, "'event' must be present");
    Preconditions.checkNotNull(event.getSummary(), "'summary' must be present");

    BitcoinNetworkSummary summary = event.getSummary();

    Preconditions.checkNotNull(summary.getSeverity(), "'severity' must be present");

    // Keep the UI response to a minimum due to the volume of these events
    updatePayRequestButton(event);

  }

  private void updatePayRequestButton(BitcoinNetworkChangedEvent event) {
    boolean newEnabled;
    boolean canChange = true;

    // Cannot pay a request until synced as you don't know how much is in the wallet
    switch (event.getSummary().getSeverity()) {
      case RED:
        // Enable on RED only if unrestricted (allows FEST tests without a network)
        newEnabled = InstallationManager.unrestricted;
        break;
      case AMBER:
        // Enable on AMBER only if unrestricted
        newEnabled = InstallationManager.unrestricted;
        break;
      case GREEN:
        // Enable on GREEN
        newEnabled = true;
        break;
      case PINK:
      case EMPTY:
        // Maintain the status quo
        newEnabled = payThisPaymentRequestButton.isEnabled();
        canChange = false;
        break;
      default:
        // Unknown status
        throw new IllegalStateException("Unknown event severity " + event.getSummary().getSeverity());
    }

    if (canChange) {
      final boolean finalNewEnabled = newEnabled;

      // If button is not enabled and the newEnabled is false don't do anything
      if (payThisPaymentRequestButton.isEnabled() || newEnabled) {
        SwingUtilities.invokeLater(
          new Runnable() {
            @Override
            public void run() {
              payThisPaymentRequestButton.setEnabled(finalNewEnabled);
            }
          });
      }
    }
  }
}

