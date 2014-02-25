package org.multibit.hd.core.services;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.events.HistoryEvent;
import org.multibit.hd.core.exceptions.HistoryLoadException;
import org.multibit.hd.core.exceptions.HistorySaveException;

import java.util.Collection;
import java.util.List;

/**
 *  <p>Interface to provide the following to History API:<br>
 *  <ul>
 *  <li>Common methods for history data access</li>
 *  </ul>
 *  </p>
 *  
 */
public interface HistoryService {

  /**
   * The name of the directory (within the wallets directory) that contains the history database
   */
  String HISTORY_DIRECTORY_NAME = "history";

  /**
   * The name of the history database
   */
  String HISTORY_DATABASE_NAME = "history.db";

  /**
   * @param description The MultiBit description of what happened
   *
   * @return A new history entry
   */
  HistoryEntry newHistoryEntry(String description);

  /**
   * @return All the history entries
   */
  List<HistoryEntry> allHistory();

  /**
   * @param query The text to match across all fields (description, notes etc)
   *
   * @return Any matching history entries
   */
  List<HistoryEntry> filterHistoryByContent(String query);

  /**
   * @param selectedEntries The selected entries to add to the store
   */
  void addAll(Collection<HistoryEntry> selectedEntries);

  /**
   * <p>Load the history from the store</p>
   *
   * @throws HistoryLoadException If something goes wrong
   */
  void loadHistory() throws HistoryLoadException;

  /**
   * @param selectedEntries The selected entries to remove from the store
   */
  void removeAll(Collection<HistoryEntry> selectedEntries);

  /**
   * <p>Update the store with the edited history entries</p>
   *
   * @param editedEntries The edited history entries
   */
  void updateHistory(Collection<HistoryEntry> editedEntries);

  /**
   * <p>Write the history to the store</p>
   *
   * @throws HistorySaveException If something goes wrong
   */
  void writeHistory() throws HistorySaveException;

  /**
   * <p>Respond to a history event</p>
   *
   * @param event The history event
   */
  @Subscribe
  void onHistoryEvent(HistoryEvent event);

  /**
   * <p>Create some demonstration entries for testing purposes</p>
   */
  void addDemoHistory();
}
