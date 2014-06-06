package org.multibit.hd.ui.fest.use_cases.settings.exchange;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "exchange OER" behaviour</li>
 * </ul>
 * <p>Requires the "settings" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class VerifyExchangeOERUseCase extends AbstractFestUseCase {

  public VerifyExchangeOERUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Click on "exchange"
    window
      .button(MessageKey.SHOW_EXCHANGE_WIZARD.getKey())
      .click();

    // Allow time for component to change
    pauseForComponentReset();

    // Verify the "exchange" wizard appears
    window
      .label(MessageKey.EXCHANGE_SETTINGS_TITLE.getKey());

    // Verify cancel is present
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .comboBox(MessageKey.EXCHANGE_RATE_PROVIDER.getKey())
      .selectItem("Open Exchange Rates");

    // Verify that various components are available
    window
      .button(MessageKey.BROWSE.getKey())
      .requireEnabled();

    window
      .textBox(MessageKey.ENTER_ACCESS_CODE.getKey())
      .requireVisible()
      .enterText("1");

    window
      .comboBox(MessageKey.SELECT_LOCAL_CURRENCY.getKey())
      .requireVisible();

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .requireEnabled()
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.SHOW_LANGUAGE_WIZARD.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
