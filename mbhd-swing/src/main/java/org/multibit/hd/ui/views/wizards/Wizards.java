package org.multibit.hd.ui.views.wizards;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.config.Configuration;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.PaymentRequestData;
import org.multibit.hd.ui.views.wizards.about.exit.AboutState;
import org.multibit.hd.ui.views.wizards.about.exit.AboutWizard;
import org.multibit.hd.ui.views.wizards.about.exit.AboutWizardModel;
import org.multibit.hd.ui.views.wizards.application_settings.ApplicationSettingsState;
import org.multibit.hd.ui.views.wizards.application_settings.ApplicationSettingsWizard;
import org.multibit.hd.ui.views.wizards.application_settings.ApplicationSettingsWizardModel;
import org.multibit.hd.ui.views.wizards.bitcoin_settings.BitcoinSettingsState;
import org.multibit.hd.ui.views.wizards.bitcoin_settings.BitcoinSettingsWizard;
import org.multibit.hd.ui.views.wizards.bitcoin_settings.BitcoinSettingsWizardModel;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactState;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactWizard;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactWizardModel;
import org.multibit.hd.ui.views.wizards.edit_contact.EnterContactDetailsMode;
import org.multibit.hd.ui.views.wizards.edit_history.EditHistoryState;
import org.multibit.hd.ui.views.wizards.edit_history.EditHistoryWizard;
import org.multibit.hd.ui.views.wizards.edit_history.EditHistoryWizardModel;
import org.multibit.hd.ui.views.wizards.edit_history.EnterHistoryDetailsMode;
import org.multibit.hd.ui.views.wizards.exchange_settings.ExchangeSettingsState;
import org.multibit.hd.ui.views.wizards.exchange_settings.ExchangeSettingsWizard;
import org.multibit.hd.ui.views.wizards.exchange_settings.ExchangeSettingsWizardModel;
import org.multibit.hd.ui.views.wizards.exit.ExitState;
import org.multibit.hd.ui.views.wizards.exit.ExitWizard;
import org.multibit.hd.ui.views.wizards.exit.ExitWizardModel;
import org.multibit.hd.ui.views.wizards.export_payments.ExportPaymentsWizard;
import org.multibit.hd.ui.views.wizards.export_payments.ExportPaymentsWizardModel;
import org.multibit.hd.ui.views.wizards.export_payments.ExportPaymentsWizardState;
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
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinState;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizard;
import org.multibit.hd.ui.views.wizards.send_bitcoin.SendBitcoinWizardModel;
import org.multibit.hd.ui.views.wizards.sound_settings.SoundSettingsState;
import org.multibit.hd.ui.views.wizards.sound_settings.SoundSettingsWizard;
import org.multibit.hd.ui.views.wizards.sound_settings.SoundSettingsWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizard;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

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

  /**
   * @return A new "exit" wizard
   */
  public static ExitWizard newExitWizard() {

    return new ExitWizard(new ExitWizardModel(ExitState.EXIT_CONFIRM), true);
  }

  /**
   * @return A new "about" wizard
   */
  public static AboutWizard newAboutWizard() {

    return new AboutWizard(new AboutWizardModel(AboutState.ABOUT_DETAILS), true);
  }

  /**
   * @param bitcoinURI The optional Bitcoin URI containing information for the send
   *
   * @return A new "send bitcoin" wizard
   */
  public static SendBitcoinWizard newSendBitcoinWizard(Optional<BitcoinURI> bitcoinURI) {

    return new SendBitcoinWizard(new SendBitcoinWizardModel(SendBitcoinState.SEND_ENTER_AMOUNT), false, bitcoinURI);

  }

  /**
   * @return A new "receive bitcoin" wizard
   */
  public static ReceiveBitcoinWizard newRequestBitcoinWizard() {

    return new ReceiveBitcoinWizard(new ReceiveBitcoinWizardModel(ReceiveBitcoinState.RECEIVE_ENTER_AMOUNT), false);

  }

  /**
   * @param contacts The list of contacts to edit
   * @param mode     The editing mode
   *
   * @return A new "edit contact" wizard for contacts
   */
  public static EditContactWizard newEditContactWizard(List<Contact> contacts, EnterContactDetailsMode mode) {

    Preconditions.checkState(!contacts.isEmpty(), "'contacts' cannot be empty");

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

    Preconditions.checkState(!historyEntries.isEmpty(), "'historyEntries' cannot be empty");

    return new EditHistoryWizard(
      new EditHistoryWizardModel(EditHistoryState.HISTORY_ENTER_DETAILS, historyEntries),
      mode
    );

  }

  /**
   * @return A new "welcome" wizard for the initial set up
   */
  public static WelcomeWizard newExitingWelcomeWizard(WelcomeWizardState initialState) {

    return new WelcomeWizard(new WelcomeWizardModel(initialState), true);
  }

  /**
   * @return A new "welcome" wizard for wallet recovery set up
   */
  public static WelcomeWizard newClosingWelcomeWizard(WelcomeWizardState initialState) {

    return new WelcomeWizard(new WelcomeWizardModel(initialState), false);
  }

  /**
   * @return A new "password" wizard for a warm start
   */
  public static PasswordWizard newExitingPasswordWizard() {

    return new PasswordWizard(new PasswordWizardModel(PasswordState.PASSWORD_ENTER_PASSWORD), true);

  }

  /**
   * @return A new "password" wizard for password recovery set up
   */
  public static PasswordWizard newClosingPasswordWizard() {

    return new PasswordWizard(new PasswordWizardModel(PasswordState.PASSWORD_ENTER_PASSWORD), false);

  }

  /**
   * @return A new "language settings" wizard for language selection
   */
  public static LanguageSettingsWizard newLanguageSettingsWizard() {

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new LanguageSettingsWizard(new LanguageSettingsWizardModel(LanguageSettingsState.LANGUAGE_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "application settings" wizard for language selection
   */
  public static ApplicationSettingsWizard newApplicationSettingsWizard() {

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new ApplicationSettingsWizard(new ApplicationSettingsWizardModel(ApplicationSettingsState.APPLICATION_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "sound settings" wizard for language selection
   */
  public static SoundSettingsWizard newSoundSettingsWizard() {

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new SoundSettingsWizard(new SoundSettingsWizardModel(SoundSettingsState.SOUND_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "Bitcoin settings" wizard for currency selection
   */
  public static BitcoinSettingsWizard newBitcoinSettingsWizard() {

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new BitcoinSettingsWizard(new BitcoinSettingsWizardModel(BitcoinSettingsState.BITCOIN_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "Exchange settings" wizard for exchange rate provider selection
   */
  public static ExchangeSettingsWizard newExchangeSettingsWizard() {

    // Ensure we work with a copy of the current configuration in case of cancellation
    Configuration configuration = Configurations.currentConfiguration.deepCopy();

    return new ExchangeSettingsWizard(new ExchangeSettingsWizardModel(ExchangeSettingsState.EXCHANGE_ENTER_DETAILS, configuration));
  }

  /**
   * @return A new "payments" wizard
   */
  public static PaymentsWizard newPaymentsWizard(PaymentData paymentData) {
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
    return new ExportPaymentsWizard(new ExportPaymentsWizardModel(initialState), false);
  }

}
