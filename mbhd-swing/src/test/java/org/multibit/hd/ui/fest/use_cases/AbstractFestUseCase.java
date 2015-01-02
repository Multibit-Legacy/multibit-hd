package org.multibit.hd.ui.fest.use_cases;

import com.google.common.base.Optional;
import org.fest.swing.core.matcher.JButtonMatcher;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Pause;
import org.multibit.hd.core.events.ExchangeRateChangedEvent;
import org.multibit.hd.core.exchanges.ExchangeKey;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Abstract base class to provide the following to FEST use case testing:</p>
 * <ul>
 * <li>Access to common methods</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public abstract class AbstractFestUseCase {

  protected static final Logger log = LoggerFactory.getLogger(AbstractFestUseCase.class);

  protected static final int SEND_REQUEST_ROW = 0;
  protected static final int PAYMENTS_ROW = 1;
  protected static final int CONTACTS_ROW = 2;
  protected static final int HELP_ROW = 3;
  protected static final int SETTINGS_ROW = 4;
  protected static final int MANAGE_WALLET_ROW = 5;
  protected static final int TOOLS_ROW = 6;
  protected static final int EXIT_ROW = 7;

  protected final FrameFixture window;

  public AbstractFestUseCase(FrameFixture window) {
    this.window = window;

    log.info("New use case: {}", this.getClass().getSimpleName());

  }

  /**
   * Execute the use case
   *
   * @param parameters Any parameters that are useful to a particular use case (e.g. data carried between panels)
   */
  public abstract void execute(Map<String, Object> parameters);

  /**
   * @param name The label name
   *
   * @return The label if it is not showing
   */
  public JLabelMatcher newNotShowingJLabelFixture(String name) {

    return JLabelMatcher.withName(name);

  }

  /**
   * @param name The button name
   *
   * @return The button if it is not showing
   */
  public JButtonMatcher newNotShowingJButtonFixture(String name) {

    return JButtonMatcher.withName(name);

  }

  /**
   * @param name The text box name
   *
   * @return The text box if it is not showing
   */
  public JTextComponentMatcher newNotShowingJTextBoxFixture(String name) {

    return JTextComponentMatcher.withName(name);

  }

  /**
   * <p>Provides a naming convention for the verification label</p>
   *
   * @param panelName     The panel name taken from the wizard state (e.g. WelcomeWizardState.RESTORE_PASSWORD_SEED_PHRASE)
   * @param componentName The component name to avoid conflict with multiple verifiable components (e.g. "timestamp", "seedphrase", "credentials")
   *
   * @return The appropriate FEST name for the verification label
   */
  protected String getVerificationStatusName(String panelName, String componentName) {
    return panelName + "." + componentName + "." + MessageKey.VERIFICATION_STATUS.getKey();
  }

  /**
   * @return True if an exchange rate from a valid provider has been received
   */
  protected boolean isExchangePresent() {

    // If there is no exchange then return fast
    if (ExchangeKey.current().equals(ExchangeKey.NONE)) {
      return false;
    }

    // Work out the current exchange rate state
    Optional<ExchangeRateChangedEvent> event = CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent();
    return event.isPresent() && event.get().getRateProvider().isPresent();

  }

  /**
   * @return True if the Bitcoin network is running rate has been received
   */
  protected boolean isBitcoinNetworkPresent() {

    return CoreServices.getOrCreateBitcoinNetworkService().isStartedOk();

  }

  protected void removeTag(int startCount) {

    // Count the tags
    final int tagCount1 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount1).isEqualTo(startCount);

    // Click on tag to remove
    window
      .list(MessageKey.TAGS.getKey())
      .pressAndReleaseKeys(KeyEvent.VK_DELETE);

    // Count the tags
    final int tagCount2 = window
      .list(MessageKey.TAGS.getKey())
      .contents().length;

    assertThat(tagCount2).isEqualTo(startCount - 1);

  }

  /**
   * <p>Ensure that a checkbox has been selected</p>
   *
   * @param messageKey          The message key to identify the table
   * @param row                 The row
   * @param checkboxColumnIndex The checkbox column index
   */
  protected void ensureCheckboxIsSelected(MessageKey messageKey, int row, int checkboxColumnIndex) {

    String[][] contents = window
      .table(messageKey.getKey())
      .contents();

    if ("false".equals(contents[row][checkboxColumnIndex])) {

      log.debug("Checkbox [{}][{}] is false. Selecting row...", row, checkboxColumnIndex);

      // Click on the row to activate the checkbox
      window
        .table(messageKey.getKey())
        .selectRows(row);

    } else {

      log.debug("Checkbox [{}][{}] is true.", row, checkboxColumnIndex);

    }

    // Verify we're in the correct state
    contents = window
      .table(messageKey.getKey())
      .contents();

    assertThat("true".equals(contents[row][checkboxColumnIndex])).isTrue();

  }

  /**
   * <p>Asserts that a label contains the required key text in the current locale (ignores wrapping HTML if present)</p>
   *
   * @param key The message key to use
   */
  protected void assertLabelText(MessageKey key) {

    String titleHtml = window.label(key.getKey()).text();

    assertThat(titleHtml).contains(Languages.safeText(key));

  }

  /**
   * <p>Asserts that a label contains the required value text in the current locale (ignores wrapping HTML if present)</p>
   *
   * @param key The message key to use
   */
  protected void assertLabelContainsValue(MessageKey key, String value) {

    String label = window.label(key.getKey()).text();

    assertThat(label).contains(value);

  }

  /**
   * <p>Asserts that a label contains the required value text in the current locale (ignores wrapping HTML if present)</p>
   *
   * @param key The message key to use
   */
  protected void assertLabelContainsValue(String key, String value) {

    String label = window.label(key).text();

    assertThat(label).contains(value);

  }

  /**
   * <p>Asserts that a "display amount" component is showing</p>
   *
   * @param panelName      The panel name taken from the wizard state (e.g. WelcomeWizardState.RESTORE_PASSWORD_SEED_PHRASE)
   * @param componentName  The component name to avoid conflict with multiple verifiable components (e.g. "client_fee")
   * @param isVisible      True if the overall component is visible (overrides the exchange rate visibility)
   * @param isLocalVisible True if the local amount should be visible
   */
  protected void assertDisplayAmount(String panelName, String componentName, boolean isVisible, boolean isLocalVisible) {

    if (isVisible) {

      // Ensure all components are visible (allowing for exchange rate setting)

      window
        .label(panelName + "." + componentName + ".leading_balance")
        .requireVisible();
      window
        .label(panelName + "." + componentName + ".primary_balance")
        .requireVisible();
      window
        .label(panelName + "." + componentName + ".secondary_balance")
        .requireVisible();
      window
        .label(newNotShowingJLabelFixture(panelName + "." + componentName + ".leading_balance"));

      if (isLocalVisible) {

        window
          .label(panelName + "." + componentName + ".exchange")
          .requireVisible();

      } else {

        window
          .label(newNotShowingJLabelFixture(panelName + "." + componentName + ".exchange"))
          .requireText("");

      }

    } else {

      // Ensure all components are not visible (regardless of exchange rate setting)

      window
        .label(newNotShowingJLabelFixture(panelName + "." + componentName + ".leading_balance"))
        .requireNotVisible();
      window
        .label(newNotShowingJLabelFixture(panelName + "." + componentName + ".primary_balance"))
        .requireNotVisible();
      window
        .label(newNotShowingJLabelFixture(panelName + "." + componentName + ".secondary_balance"))
        .requireNotVisible();
      window
        .label(newNotShowingJLabelFixture(panelName + "." + componentName + ".leading_balance"))
        .requireNotVisible();
      window
        .label(newNotShowingJLabelFixture(panelName + "." + componentName + ".exchange"))
        .requireNotVisible();

    }
  }

  /**
   * The standard length of time for a wallet to fail to unlock
   */
  protected void pauseForFailedWalletUnlock() {
    Pause.pause(3, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a wallet to unlock
   */
  protected void pauseForWalletUnlock() {
    Pause.pause(7, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a wallet credentials to change
   */
  protected void pauseForWalletPasswordChange() {
    Pause.pause(3, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a wallet to be created (at least 15 seconds with CA certs which take at least 6 seconds on broadband)
   */
  protected void pauseForWalletCreation() {
    Pause.pause(15, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a wallet to be restored (at least 20 seconds with CA certs which take at least 6 seconds on broadband)
   */
  protected void pauseForWalletRestore() {
    Pause.pause(20, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a wallet to be switched (at least 5 seconds)
   */
  protected void pauseForWalletSwitch() {
    Pause.pause(5, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for data to persist (e.g. contacts)
   */
  protected void pauseForDataPersistence() {
    Pause.pause(1, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a view reset to occur (e.g. configuration change)
   */
  protected void pauseForViewReset() {
    Pause.pause(2, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a component reset to occur (e.g. exchange list)
   */
  protected void pauseForComponentReset() {
    Pause.pause(1, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a user to respond to something (e.g. hardware wallet button press)
   */
  protected void pauseForUserInput() {
    Pause.pause(1, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a preparation view to occur (e.g. wallet create notes)
   */
  protected void pauseForPreparationDisplay() {
    Pause.pause(4, TimeUnit.SECONDS);
  }
}
