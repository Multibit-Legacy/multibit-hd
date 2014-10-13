package org.multibit.hd.ui.fest.use_cases.sidebar.history;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.tables.ContactTableModel;
import org.multibit.hd.ui.views.components.tables.HistoryTableModel;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "history" screen edit Bob contact then cancel changes</li>
 * </ul>
 * <p>Requires the "history" screen to be showing</p>
 * <p>Requires the "credentials verified" entry to be present</p>
 * <p>Requires the "wallet opened" entry to be present</p>
 *
 * @since 0.0.1
 *  
 */
public class EditThenCancelPasswordEntryUseCase extends AbstractFestUseCase {

  public EditThenCancelPasswordEntryUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.HISTORY.getKey())
      .rowCount();

    // Find the credentials verified row
    int pvRow = window
      .table(MessageKey.HISTORY.getKey())
      .cell(Languages.safeText(MessageKey.PASSWORD_VERIFIED))
      .row;

    // Get table contents
    String[][] history =  window
      .table(MessageKey.HISTORY.getKey())
      .contents();

    ensureCheckboxIsSelected(MessageKey.HISTORY, pvRow, HistoryTableModel.CHECKBOX_COLUMN_INDEX);

    // Click on Edit
    window
      .button(MessageKey.EDIT.getKey())
      .click();

    // Verify the single entry edit wizard appears
    assertLabelText(MessageKey.EDIT_HISTORY_ENTRY_TITLE);

    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    // Update credentials entry details
    window
      .textBox(MessageKey.PRIVATE_NOTES.getKey())
      .setText("Updated information");

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .click();

    // Expect Yes/No popover
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button("popover_"+MessageKey.CLOSE.getKey())
      .requireVisible()
      .requireEnabled();

    // Click Yes
    window
      .button(MessageKey.YES.getKey())
      .requireVisible()
      .requireEnabled()
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

    // Get updated table contents
    history =  window
      .table(MessageKey.HISTORY.getKey())
      .contents();

    assertThat(history[pvRow][ContactTableModel.NAME_COLUMN_INDEX].contains("Updated")).isFalse();

  }

}
