package org.multibit.hd.ui.fest.use_cases.standard.sidebar.buy_sell;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "buy/sell" screen about wizard shows</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ShowThenCancelBuySellUseCase extends AbstractFestUseCase {

  public ShowThenCancelBuySellUseCase(FrameFixture window) {
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

    // Verify the "buy/sell" wizard appears
    assertLabelText(MessageKey.BUY_SELL_TITLE);

    // Verify the notes appear
    assertLabelText(MessageKey.BUY_SELL_NOTE_1);

    // Verify "buy bitcoin" is present
    window
      .button(MessageKey.BUY_VISIT_GLIDERA.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify "sell bitcoin" is present
    window
      .button(MessageKey.SELL_VISIT_GLIDERA.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify Finish is present
    window
      .button(MessageKey.FINISH.getKey())
      .requireVisible()
      .requireEnabled();

    // Click Finish
    window
      .button(MessageKey.FINISH.getKey())
      .click();

    // Expect the sidebar to grab focus
    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireFocused();

  }

}
