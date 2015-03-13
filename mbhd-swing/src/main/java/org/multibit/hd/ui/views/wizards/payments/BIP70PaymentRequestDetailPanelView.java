package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;
import org.joda.time.DateTime;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.core.dto.PaymentSessionSummary;
import org.multibit.hd.core.utils.Dates;
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
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

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

  private JLabel trustStatusLabel;
  private JLabel memo;
  private JLabel displayName;
  private JLabel date;
  private JLabel expires;

  /**
   * The payment protocol session - condtructed from the PaymentRequestData
   */
  private Optional<PaymentSession> paymentSessionOptional = Optional.absent();

  private static final Logger log = LoggerFactory.getLogger(BIP70PaymentRequestDetailPanelView.class);

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name
   */
  public BIP70PaymentRequestDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {
    super(wizard, panelName, MessageKey.DISPLAY_PAYMENT_REQUEST_TITLE, AwesomeIcon.MONEY);
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
                    "[][][][][][]" // Row constraints
            ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

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
    memo = Labels.newValueLabel(Languages.safeText(MessageKey.NOT_AVAILABLE));
    memo.setName(MessageKey.NOTES.getKey() + ".value");

    date = Labels.newValueLabel(Languages.safeText(MessageKey.NOT_AVAILABLE));
    date.setName(MessageKey.DATE.getKey() + ".value");

    expires = Labels.newValueLabel(Languages.safeText(MessageKey.NOT_AVAILABLE));
    expires.setName(MessageKey.EXPIRES.getKey() + ".value");

    displayName = Labels.newValueLabel(Languages.safeText(MessageKey.NOT_AVAILABLE));
    displayName.setName(MessageKey.NAME.getKey() + ".value");

    trustStatusLabel = Labels.newStatusLabel(Optional.<MessageKey>absent(), null, Optional.<Boolean>absent());
    trustStatusLabel.setName("trust_status");
    contentPanel.add(trustStatusLabel, "span 2,aligny top,wrap");

    contentPanel.add(Labels.newMemoLabel(), "shrink");
    contentPanel.add(memo, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newDisplayNameLabel(), "shrink");
    contentPanel.add(displayName, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newDateLabel(), "shrink");
    contentPanel.add(date, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newExpiresLabel(), "shrink");
    contentPanel.add(expires, "shrink," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

    contentPanel.add(Labels.newAmount(), "baseline");
    contentPanel.add(paymentRequestAmountMaV.getView().newComponentPanel(), "span 4,wrap");

    // Register components
    registerComponents(paymentRequestAmountMaV);
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
    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {

                if (!paymentSessionOptional.isPresent()) {
                  // Try to create it
                  PaymentRequestData paymentRequestData = getWizardModel().getPaymentRequestData();
                  if (paymentRequestData == null) {
                    return;
                  }

                  try {
                    PaymentSession paymentSession = PaymentProtocol.parsePaymentRequest(paymentRequestData.getPaymentRequest());
                    paymentSessionOptional = Optional.of(paymentSession);

                    // TODO update for whether paymentSession is trusted/ paid etc
                    PaymentSessionSummary paymentSessionSummary = PaymentSessionSummary.newPaymentSessionOK(paymentSessionOptional.get());

                    switch (paymentSessionSummary.getStatus()) {
                      case TRUSTED:
                        LabelDecorator.applyPaymentSessionStatusIcon(
                                paymentSessionSummary.getStatus(),
                                trustStatusLabel,
                                MessageKey.PAYMENT_PROTOCOL_TRUSTED_NOTE,
                                MultiBitUI.NORMAL_ICON_SIZE);
                        break;
                      case UNTRUSTED:
                        LabelDecorator.applyPaymentSessionStatusIcon(
                                paymentSessionSummary.getStatus(),
                                trustStatusLabel,
                                MessageKey.PAYMENT_PROTOCOL_UNTRUSTED_NOTE,
                                MultiBitUI.NORMAL_ICON_SIZE);
                        // TODO Consider adding to cacerts and how subsequent Repair Wallet will be managed
                        break;
                      case DOWN:
                      case ERROR:
                        // Provide more details on the failure
                        LabelDecorator.applyPaymentSessionStatusIcon(
                                paymentSessionSummary.getStatus(),
                                trustStatusLabel,
                                MessageKey.PAYMENT_PROTOCOL_ERROR_NOTE,
                                MultiBitUI.NORMAL_ICON_SIZE);
                        memo.setText(Languages.safeText(paymentSessionSummary.getMessageKey().get(), paymentSessionSummary.getMessageData().get()));
                        displayName.setVisible(false);
                        date.setVisible(false);
                        expires.setVisible(false);
                        paymentRequestAmountMaV.getView().setVisible(false);
                        return;
                      default:
                        throw new IllegalStateException("Unknown payment session summary status: " + paymentSessionSummary.getStatus());
                    }

                    // Must have a valid payment session to be here

                    memo.setText(paymentSession.getMemo());

                    DateTime paymentRequestDate = new DateTime(paymentSession.getDate());
                    date.setText(Dates.formatTransactionDateLocal(paymentRequestDate));

                    if (paymentSession.getExpires() == null) {
                      expires.setText(Languages.safeText(MessageKey.NOT_AVAILABLE));
                    } else {
                      DateTime expiresDate = new DateTime(paymentSession.getExpires());
                      expires.setText(Dates.formatTransactionDateLocal(expiresDate));
                      // TODO Handle display of expiry and button control
                      //            if (expiresDate.isBeforeNow()) {
                      //              // This payment request has expired
                      //            } else {
                      //            }
                    }

                    // Update the model and view for the amount
                    // (no local in case of different exchange rates causing confusion)
                    Configuration configuration = Configurations.currentConfiguration;
                    paymentRequestAmountMaV.getModel().setCoinAmount(paymentSession.getValue());
                    if (paymentRequestData.getAmountFiat().getAmount().isPresent()) {
                      paymentRequestAmountMaV.getModel().setLocalAmount(paymentRequestData.getAmountFiat().getAmount().get());
                      paymentRequestAmountMaV.getModel().setLocalAmountVisible(true);
                    } else {
                      paymentRequestAmountMaV.getModel().setLocalAmount(null);
                      paymentRequestAmountMaV.getModel().setLocalAmountVisible(false);
                    }
                    paymentRequestAmountMaV.getView().updateView(configuration);

                    PaymentProtocol.PkiVerificationData identity = paymentSession.verifyPki();
                    if (identity != null && identity.displayName != null) {
                      displayName.setText(identity.displayName);
                    } else {
                      displayName.setText(Languages.safeText(MessageKey.NOT_AVAILABLE));
                    }
                    //getWizardModel().setPkiVerificationData(identity);

                    // Ensure the next button is enabled
                    ViewEvents.fireWizardButtonEnabledEvent(getPanelName(), WizardButton.NEXT, true);
                  } catch (PaymentProtocolException ppe) {
                    log.error("Cannot parse PaymentRequest to PaymentSession", ppe);
                  }
                }
              }
            });
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }
}

