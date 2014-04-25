package org.multibit.hd.ui.fest.use_cases.history;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.tables.HistoryTableModel;

import java.util.Map;
import java.util.regex.Pattern;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "history" screen edit Opened and Password entry history</li>
 * </ul>
 * <p>Requires the "history" screen to be showing</p>
 * <p>Requires "Opened" and "Password Verified" to be created</p>
 *
 * @since 0.0.1
 *  
 */
public class EditOpenedAndPasswordEntryUseCase extends AbstractFestUseCase {

  public EditOpenedAndPasswordEntryUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.HISTORY.getKey())
      .rowCount();

    // Find the Opened row
    int openedRow = window
      .table(MessageKey.HISTORY.getKey())
      .cell(Pattern.compile("^Opened*.*"))
      .row;

    // Find the Password verified row
    int pvRow = window
      .table(MessageKey.HISTORY.getKey())
      .cell(Languages.safeText(MessageKey.PASSWORD_VERIFIED))
      .row;

    // Click on Password verified table row
    String[][] history = window
      .table(MessageKey.HISTORY.getKey())
      .contents();

    if ("false".equals(history[openedRow][HistoryTableModel.CHECKBOX_COLUMN_INDEX])) {

      // Click on the row to activate the checkbox
      window
        .table(MessageKey.HISTORY.getKey())
        .selectRows(openedRow);
    }

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

    // Verify the multiple history edit wizard appears
    window
      .label(MessageKey.EDIT_HISTORY_ENTRIES_TITLE.getKey());

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Private notes
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("Preparing Scrooge's accounts");

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

    // Update the contents
    history = window
      .table(MessageKey.HISTORY.getKey())
      .contents();

    assertThat(history[openedRow][HistoryTableModel.NOTES_COLUMN_INDEX]).contains("Scrooge");
    assertThat(history[pvRow][HistoryTableModel.NOTES_COLUMN_INDEX]).contains("Scrooge");

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

    // Click No
    window
      .button(MessageKey.NO.getKey())
      .requireVisible()
      .requireEnabled()
      .click();
  }

}
