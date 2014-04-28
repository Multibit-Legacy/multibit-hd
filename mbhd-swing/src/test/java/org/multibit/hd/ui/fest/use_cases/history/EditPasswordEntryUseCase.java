package org.multibit.hd.ui.fest.use_cases.history;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.tables.HistoryTableModel;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "history" screen edit Bob history</li>
 * </ul>
 * <p>Requires the "history" screen to be showing</p>
 * <p>Requires the "create wallet" and "password verified" entries to be present</p>
 *
 * @since 0.0.1
 *  
 */
public class EditPasswordEntryUseCase extends AbstractFestUseCase {

  public EditPasswordEntryUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.HISTORY.getKey())
      .rowCount();

    // Find "Password verified" row
    int pvRow = window
      .table(MessageKey.HISTORY.getKey())
      .cell(Languages.safeText(MessageKey.PASSWORD_VERIFIED))
      .row;

    // Get the history
    String[][] history = window
      .table(MessageKey.HISTORY.getKey())
      .contents();

    if ("false".equals(history[pvRow][HistoryTableModel.CHECKBOX_COLUMN_INDEX])) {

      // Click on the row to activate the checkbox
      window
        .table(MessageKey.HISTORY.getKey())
        .selectRows(pvRow);
    }

    // Click on Edit
    window
      .button(MessageKey.EDIT.getKey())
      .click();

    // Verify the single history edit wizard appears
    window
      .label(MessageKey.EDIT_HISTORY_ENTRY_TITLE.getKey());

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Update password entry private notes
    window
      .textBox(MessageKey.DESCRIPTION_READ_ONLY.getKey())
      .requireText(Languages.safeText(MessageKey.PASSWORD_VERIFIED))
      .requireNotEditable();

    // Private notes
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("First login to wallet");

    verifyCancel();

    // Click Apply
    window
      .button(MessageKey.APPLY.getKey())
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.EDIT.getKey())
      .requireVisible()
      .requireEnabled();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.HISTORY.getKey())
      .rowCount();

    // Verify that no new row has been added
    assertThat(rowCount2).isEqualTo(rowCount1);

    // Verify that the private notes are visible
    window
      .table(MessageKey.HISTORY.getKey())
      .cell("First login to wallet");
  }

  /**
   * Verifies that clicking cancel with data present gives a Yes/No popover
   */
  private void verifyCancel() {

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Expect Yes/No popup)
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.CLOSE.getKey())
      .requireVisible()
      .requireEnabled();

    // Click Cancel
    window
      .button(MessageKey.NO.getKey())
      .requireVisible()
      .requireEnabled()
      .click();
  }

}
