package org.multibit.hd.core.services;

import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.events.HistoryEvent;
import org.multibit.hd.core.exceptions.HistoryLoadException;
import org.multibit.hd.core.exceptions.HistorySaveException;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 *  <p>Class to provide the following to HistoryService consumers:</p>
 *  <ul>
 *  <li>An empty contact service i.e no backing store</li>
 *  </ul>
 */
public class EmptyHistoryService implements HistoryService {

  private List<HistoryEntry> history = Lists.newArrayList();

  @Override
  public HistoryEntry newHistoryEntry(String description) {

    UUID id = UUID.randomUUID();
    HistoryEntry entry = new HistoryEntry(id, description);

    history.add(entry);

    return entry;
  }

  @Override
  public List<HistoryEntry> allHistory() {
    return history;
  }

  @Override
  public List<HistoryEntry> filterHistoryByContent(String query) {
    return null;
  }

  @Override
  public void addAll(Collection<HistoryEntry> selectedEntries) {
    history.addAll(selectedEntries);
  }

  @Override
  public void loadHistory() throws HistoryLoadException {

  }

  @Override
  public void removeAll(Collection<HistoryEntry> selectedEntries) {
    history.removeAll(selectedEntries);
  }

  @Override
  public void updateHistory(Collection<HistoryEntry> editedEntries) {

  }

  @Override
  public void writeHistory() throws HistorySaveException {

  }

  @Override
  public void onHistoryEvent(HistoryEvent event) {

    newHistoryEntry(event.getDescription());

  }

  @Override
  public void addDemoHistory() {

  }

}
