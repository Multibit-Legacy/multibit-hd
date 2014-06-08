package org.multibit.hd.ui.fest.use_cases;

import org.fest.swing.core.matcher.JButtonMatcher;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JTextComponentMatcher;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Pause;
import org.multibit.hd.core.services.CoreServices;
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
  protected static final int HISTORY_ROW = 4;
  protected static final int SETTINGS_ROW = 5;
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
   * @return True if an exchange rate has been received
   */
  protected boolean isExchangePresent() {
    return CoreServices.getApplicationEventService().getLatestExchangeRateChangedEvent().isPresent();
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
   * The standard length of time for a wallet to fail to unlock
   */
  protected void pauseForFailedWalletUnlock() {
    Pause.pause(3, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a wallet to unlock
   */
  protected void pauseForWalletUnlock() {
    Pause.pause(5, TimeUnit.SECONDS);
  }

  /**
   * The standard length of time for a wallet to be created (at least 10 seconds with CA certs)
   */
  protected void pauseForWalletCreation() {
    Pause.pause(10, TimeUnit.SECONDS);
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

}
