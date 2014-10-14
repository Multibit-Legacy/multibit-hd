package org.multibit.hd.ui.views.wizards.edit_history;

import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.ui.views.wizards.AbstractWizardModel;

import java.util.List;

/**
 * <p>Model object to provide the following to "edit history" wizard:</p>
 * <ul>
 * <li>Storage of panel data</li>
 * <li>State transition management</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class EditHistoryWizardModel extends AbstractWizardModel<EditHistoryState> {

  private final List<HistoryEntry> historyEntries;

  /**
   * @param state    The state object
   * @param historyEntries The history entries to edit
   */
  public EditHistoryWizardModel(EditHistoryState state, List<HistoryEntry> historyEntries) {
    super(state);

    this.historyEntries = historyEntries;
  }

  /**
   * @return The edited history entries
   */
  public List<HistoryEntry> getHistoryEntries() {
    return historyEntries;
  }

}
