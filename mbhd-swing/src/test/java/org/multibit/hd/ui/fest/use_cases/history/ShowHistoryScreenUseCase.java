package org.multibit.hd.ui.fest.use_cases.history;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.swing.timing.Timeout.timeout;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "history" sidebar screen</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ShowHistoryScreenUseCase extends AbstractFestUseCase {

  public ShowHistoryScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertThat(parameters).isNotNull();

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(HISTORY_ROW);

    // Expect the History screen to show
    window
      .button(MessageKey.SEARCH.getKey())
      .requireVisible()
      .requireEnabled(timeout(1, TimeUnit.SECONDS));


    window
      .textBox(MessageKey.SEARCH.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .comboBox(MessageKey.HISTORY.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.EDIT.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .table(MessageKey.HISTORY.getKey())
      .requireVisible()
      .requireEnabled();

  }

}
