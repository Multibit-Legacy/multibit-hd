package org.multibit.hd.ui.views.wizards;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.*;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.ui.views.wizards.about.AboutState;
import org.multibit.hd.ui.views.wizards.about.AboutWizard;
import org.multibit.hd.ui.views.wizards.about.AboutWizardModel;
import org.multibit.hd.ui.views.wizards.application_settings.ApplicationSettingsState;
import org.multibit.hd.ui.views.wizards.application_settings.ApplicationSettingsWizard;
import org.multibit.hd.ui.views.wizards.application_settings.ApplicationSettingsWizardModel;
import org.multibit.hd.ui.views.wizards.units_settings.UnitsSettingsState;
import org.multibit.hd.ui.views.wizards.units_settings.UnitsSettingsWizard;
import org.multibit.hd.ui.views.wizards.units_settings.UnitsWizardModel;
import org.multibit.hd.ui.views.wizards.change_password.ChangePasswordState;
import org.multibit.hd.ui.views.wizards.change_password.ChangePasswordWizard;
import org.multibit.hd.ui.views.wizards.change_password.ChangePasswordWizardModel;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactState;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactWizard;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactWizardModel;
import org.multibit.hd.ui.views.wizards.edit_contact.EnterContactDetailsMode;
import org.multibit.hd.ui.views.wizards.edit_history.EditHistoryState;
import org.multibit.hd.ui.views.wizards.edit_history.EditHistoryWizard;
import org.multibit.hd.ui.views.wizards.edit_history.EditHistoryWizardModel;
import org.multibit.hd.ui.views.wizards.edit_history.EnterHistoryDetailsMode;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletState;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletWizard;
import org.multibit.hd.ui.views.wizards.edit_wallet.EditWalletWizardModel;
import org.multibit.hd.ui.views.wizards.empty_wallet.EmptyWalletState;
import org.multibit.hd.ui.views.wizards.empty_wallet.EmptyWalletWizard;
import org.multibit.hd.ui.views.wizards.empty_wallet.EmptyWalletWizardModel;
import org.multibit.hd.ui.views.wizards.exchange_settings.ExchangeSettingsState;
import org.multibit.hd.ui.views.wizards.exchange_settings.ExchangeSettingsWizard;
import org.multibit.hd.ui.views.wizards.exchange_settings.ExchangeSettingsWizardModel;
import org.multibit.hd.ui.views.wizards.exit.ExitState;
import org.multibit.hd.ui.views.wizards.exit.ExitWizard;
import org.multibit.hd.ui.views.wizards.exit.ExitWizardModel;
import org.multibit.hd.ui.views.wizards.export_payments.ExportPaymentsWizard;
import org.multibit.hd.ui.views.wizards.export_payments.ExportPaymentsWizardModel;
import org.multibit.hd.ui.views.wizards.export_payments.ExportPaymentsWizardState;
import org.multibit.hd.ui.views.wizards.lab_settings.LabSettingsState;
import org.multibit.hd.ui.views.wizards.lab_settings.LabSettingsWizard;
import org.multibit.hd.ui.views.wizards.lab_settings.LabSettingsWizardModel;
import org.multibit.hd.ui.views.wizards.language_settings.LanguageSettingsState;
import org.multibit.hd.ui.views.wizards.language_settings.LanguageSettingsWizard;
import org.multibit.hd.ui.views.wizards.language_settings.LanguageSettingsWizardModel;
import org.multibit.hd.ui.views.wizards.password.PasswordState;
import org.multibit.hd.ui.views.wizards.password.PasswordWizard;
import org.multibit.hd.ui.views.wizards.password.PasswordWizardModel;
import org.multibit.hd.ui.views.wizards.payments.PaymentsState;
import org.multibit.hd.ui.views.wizards.payments.PaymentsWizard;
import org.multibit.hd.ui.views.wizards.payments.PaymentsWizardModel;
import org.multibit.hd.ui.views.wizards.receive_bitcoin.ReceiveBitcoinState;
import org.multibit.hd.ui.views.wizards.receive_bitcoin.ReceiveBitcoinWizard;
import org.multibit.hd.ui.views.wizards.receive_bitcoin.ReceiveBitcoinWizardModel;
import org.multibit.hd.ui.views.wizards.repair_wallet.RepairWalletState;
import org.multibit.hd.ui.views.wizards.repair_wallet.RepairWalletWizard;
import org.multibit.hd.ui.views.wizards.repair_wallet.RepairWalletWizardModel;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinParameter;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizard;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizardModel;
import org.multibit.hd.ui.views.wizards.sign_message.SignMessageState;
import org.multibit.hd.ui.views.wizards.sign_message.SignMessageWizard;
import org.multibit.hd.ui.views.wizards.sign_message.SignMessageWizardModel;
import org.multibit.hd.ui.views.wizards.sound_settings.SoundSettingsState;
import org.multibit.hd.ui.views.wizards.sound_settings.SoundSettingsWizard;
import org.multibit.hd.ui.views.wizards.sound_settings.SoundSettingsWizardModel;
import org.multibit.hd.ui.views.wizards.verify_message.VerifyMessageState;
import org.multibit.hd.ui.views.wizards.verify_message.VerifyMessageWizard;
import org.multibit.hd.ui.views.wizards.verify_message.VerifyMessageWizardModel;
import org.multibit.hd.ui.views.wizards.verify_network.VerifyNetworkState;
import org.multibit.hd.ui.views.wizards.verify_network.VerifyNetworkWizard;
import org.multibit.hd.ui.views.wizards.verify_network.VerifyNetworkWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizard;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>Factory to provide the following to UI:</p>
 * <ul>
 * <li>Provision of different wizards targeting various use cases</li>
 * </ul>
 *
 * <h3>Overview of the Wizard architecture</h3>
 *
 * <p>A wizard presents a series of panels enclosed in a light box. This is in contrast to the
 * standard modal dialog approach offered by Swing which is more limited and offers less customisation
 * opportunities.</p>
 *
 * <p>From a data perspective each wizard consists of one "wizard model" which has many "panel models"
 * each of which have many "component models". Components are reused across panels and so do not maintain
 * a back reference to a parent panels but instead use a <code>WizardComponentModelChangedEvent</code> to
 * inform all interested panels that their data has changed. Events are filtered by the panel name to prevent
 * collisions.</p>
 *
 * <p>A "wizard view" has a consistent layout: a title and description (top), some components (middle) and a row of
 * buttons (bottom). The top and bottom rows are handled mainly by boilerplate code leaving just the presentation
 * and management of the middle section to the developer.</p>
 *
 * <h3>Quickly assembling a wizard</h3>
 *
 * <p>The quickest way to get a wizard up and running is to take an existing one and modify it accordingly. If
 * your requirement is straightforward (no MaV components or reliance on previous panels) then the boilerplate
 * will handle all the work for you.</p>
 *
 * @since 0.0.1
 *  
 */
public class Wizards {

  private static final Logger log = LoggerFactory.getLogger(Wizards.class);

  /**
   * @return A new "exit" wizard
   */
  public static ExitWizard newExitWizard() {

    log.debug("New 'Exit wizard'");
    return new ExitWizard(new ExitWizardModel(ExitState.EXIT_CONFIRM), true);
  }

  /**
   * @return A new "about" wizard
   */
  public static AboutWizard newAboutWizard() {

    log.debug("New 'About wizard'");
    return new AboutWizard(new AboutWizardModel(AboutState.ABOUT_DETAILS), true);
  }

  /**
   * @param parameter Providing information about how the send should be performed
   *
   * @return A new "send bitcoin" wizard
   */
  public static SendBitcoinWizard newSendBitcoinWizard(SendBitcoinParameter parameter) {

    log.debug("New 'Send bitcoin wizard'");

    return new SendBitcoinWizard(new SendBitcoinWizardModel(SendBitcoinState.SEND_ENTER_AMOUNT, parameter));

  }

  /**
   * @return A new "request bitcoin" wizard
   */
  public static ReceiveBitcoinWizard newRequestBitcoinWizard() {

    log.debug("New 'Request bitcoin wizard'");
    return new ReceiveBitcoinWizard(new ReceiveBitcoinWizardModel(ReceiveBitcoinState.RECEIVE_ENTER_AMOUNT), false);

  }

  /**
   * @param contacts The list of contacts to edit
   * @param mode     The editing mode
   *
   * @return A new "edit contact" wizard for contacts
   */
  public static EditContactWizard newEditContactWizard(List<Contact> contacts, EnterContactDetailsMode mode) {

    log.debug("New 'Edit contact wizard'");

    Preconditions.checkState(!contacts.isEmpty(), "'contacts' cannot be empty");
    Preconditions.checkNotNull(mode, "'mode' must be present");

    return new EditContactWizard(
      new EditContactWizardModel(EditContactState.EDIT_CONTACT_ENTER_DETAILS, contacts),
      mode
    );

  }

  /**
   * @param historyEntries The list of history entries to edit
   * @param mode           The editing mode
   *
   * @return A new "edit history" wizard for history entries
   */
  public static EditHistoryWizard newEditHistoryWizard(List<HistoryEntry> historyEntries, EnterHistoryDetailsMode mode) {

    log.debug("New 'Edit history wizard'");

    Preconditions.checkState(!historyEntries.isEmpty(), "'historyEntries' cannot be empty");
    Preconditions.checkNotNull(mode, "'mode' must be present");

    return new EditHistoryWizard(
      new EditHistoryWizardModel(EditHistoryState.HISTORY_ENTER_DETAILS, historyEntries),
      mode
    );

  }

  /**
   * @return A new "welcome" wizard for the initial set up
   */
  public static WelcomeWizard newExitingWelcomeWizard(WelcomeWizardState initialState) {

    log.debug("New 'Exiting welcome wizard'");

    Preconditions.checkNotNull(initialState, "'initialState' must be present");

    return new WelcomeWizard(new WelcomeWizardModel(initialState), true);
  }

  /**
   * @return A new "welcome" wizard for wallet recovery set up
   */
  public static WelcomeWizard newClosingWelcomeWizard(WelcomeWizardState initialState) {

    log.debug("New 'Closing welcome wizard'");

    return new WelcomeWizard(new WelcomeWizardModel(initialState), false);
  }

  /**
   * @return A new "sign message" wizard for a warm start
   */
  public static SignMessageWizard newSignMessageWizard() {

    log.debug("New 'Sign message wizard'");

    return new SignMessageWizard(new SignMessageWizardModel(SignMessageState.EDIT_MESSAGE), false);

  }

  /**
   * @return A new "verify message" wizard for a warm start
   */
  public static VerifyMessageWizard newVerifyMessageWizard() {

    log.debug("New 'Verify message wizard'");

    return new VerifyMessageWizard(new VerifyMessageWizardModel(VerifyMessageState.EDIT_MESSAGE), false);

  }

  /**
   * @return A new "password" wizard for a warm start
   */
  public static PasswordWizard newExitingPasswordWizard() {

    log.debug("New 'Password wizard'");

    return new PasswordWizard(new PasswordWizardModel(PasswordState.PASSWORD_ENTER_PASSWORD), true);

  }

  /**
   * @return A new "change password" wizard
   */
  public static ChangePasswordWizard newChangePasswordWizard() {

    log.debug("New 'Change password wizard'");

    return new ChangePasswordWizard(new ChangePasswordWizardModel(ChangePasswordState.CHANGE_PASSWORD_ENTER_PASSWORD), false);

  }

  /**
   * @return A new "verify network" wizard
   */
  public static VerifyNetworkWizard newVerifyNetworkWizard() {

    log.debug("New 'Verify network wizard'");

    return new VerifyNetworkWizard(new VerifyNetworkWizardModel(VerifyNetworkState.VERIFY_NETWORK_SHOW_REPORT), false);

  }

  /**
   * @return A new "repair wallet" wizard
   */
  public static RepairWalletWizard newRepairWalletWizard() {

    log.debug("New 'Repair wallet wizard'");

    return new RepairWalletWizard(new RepairWalletWizardModel(RepairWalletState.REPAIR_WALLET), false);

  }

  /**
   * @return A new "empty wallet" wizard
   */
  public static EmptyWalletWizard newEmptyWalletWizard() {

    log.debug("New 'Empty wallet wizard'");

    return new EmptyWalletWizard(new EmptyWalletWizardModel(EmptyWalletState.EMPTY_WALLET_ENTER_DETAILS), false);

  }

  /**
   * @return A new "language settings" wizard for language selection
   */
  public static LanguageSettingsWizard newLanguageSettingsWizard() {

    log.debug("New 'Language settings wizard'");

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new LanguageSettingsWizard(new LanguageSettingsWizardModel(LanguageSettingsState.LANGUAGE_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "application settings" wizard for theme selection
   */
  public static ApplicationSettingsWizard newApplicationSettingsWizard() {

    log.debug("New 'Application settings wizard'");

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new ApplicationSettingsWizard(new ApplicationSettingsWizardModel(ApplicationSettingsState.APPLICATION_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "sound settings" wizard for sound selection
   */
  public static SoundSettingsWizard newSoundSettingsWizard() {

    log.debug("New 'Sound settings wizard'");

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new SoundSettingsWizard(new SoundSettingsWizardModel(SoundSettingsState.SOUND_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "lab settings" wizard for experimental features selection
   */
  public static LabSettingsWizard newLabSettingsWizard() {

    log.debug("New 'Lab settings wizard'");

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new LabSettingsWizard(new LabSettingsWizardModel(LabSettingsState.LAB_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "Units settings" wizard for currency unit selection
   */
  public static UnitsSettingsWizard newUnitsSettingsWizard() {

    log.debug("New 'Units settings wizard'");

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new UnitsSettingsWizard(new UnitsWizardModel(UnitsSettingsState.UNITS_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "Exchange settings" wizard for exchange rate provider selection
   */
  public static ExchangeSettingsWizard newExchangeSettingsWizard() {

    log.debug("New 'Exchange settings wizard'");

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new ExchangeSettingsWizard(new ExchangeSettingsWizardModel(ExchangeSettingsState.EXCHANGE_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "payments" wizard
   */
  public static PaymentsWizard newPaymentsWizard(PaymentData paymentData) {

    log.debug("New 'Payments wizard'");

    Preconditions.checkNotNull(paymentData, "'paymentData' must be present");

    PaymentsWizardModel paymentsWizardModel;
    if (paymentData instanceof PaymentRequestData) {
      paymentsWizardModel = new PaymentsWizardModel(PaymentsState.PAYMENT_REQUEST_DETAILS, paymentData);
      paymentsWizardModel.setPaymentRequestData((PaymentRequestData) paymentData);
      paymentsWizardModel.setShowPrevOnPaymentRequestDetailScreen(false);
    } else {
      paymentsWizardModel = new PaymentsWizardModel(PaymentsState.TRANSACTION_OVERVIEW, paymentData);
      paymentsWizardModel.setShowPrevOnPaymentRequestDetailScreen(true);
    }

    return new PaymentsWizard(paymentsWizardModel, false);
  }

  /**
   * @return A new "export payments" wizard
   */
  public static ExportPaymentsWizard newExportPaymentsWizard(ExportPaymentsWizardState initialState) {

    log.debug("New 'Export payments wizard'");

    return new ExportPaymentsWizard(new ExportPaymentsWizardModel(initialState), false);
  }

  /**
   * @return A new "edit wallet" wizard for adjusted wallet details
   */
  public static EditWalletWizard newEditWalletWizard() {

    log.debug("New 'Edit wallet wizard'");

    Optional<WalletSummary> currentWalletSummary = WalletManager.INSTANCE.getCurrentWalletSummary();
    Preconditions.checkState(currentWalletSummary.isPresent(), "'currentWalletSummary' must be present");

    return new EditWalletWizard(new EditWalletWizardModel(EditWalletState.EDIT_WALLET, currentWalletSummary.get()), false);
  }

}
