package org.multibit.hd.ui.fest.use_cases.standard.sidebar.buy_sell;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "buy/sell" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ShowBuySellScreenUseCase extends AbstractFestUseCase {

  public ShowBuySellScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(BUY_SELL_ROW);

    // Expect the buy/sell wizard to show
    window
      .button(MessageKey.BUY_OR_SELL.getKey())
      .requireVisible()
      .requireEnabled();

    // Click cancel to escape
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Expect the sidebar to grab focus
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireFocused();
  }

}
