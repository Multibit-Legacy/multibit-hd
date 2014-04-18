package org.multibit.hd.core.services;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.managers.WalletManagerTest;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

public class PersistentHistoryServiceTest {

  private PersistentHistoryService historyService;

  @Before
  public void setUp() throws Exception {

    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryApplicationDirectory();
    File contactDbFile = new File(temporaryDirectory.getAbsolutePath() + File.separator + HistoryService.HISTORY_DATABASE_NAME);

    historyService = new PersistentHistoryService(contactDbFile);
    historyService.addDemoHistory();

  }

  @Test
  public void testNewHistoryEntry() throws Exception {

    assertThat(historyService.newHistoryEntry("Test description").getDescription()).isEqualTo("Test description");

  }

  @Test
  public void testAllHistoryEntries() throws Exception {

    List<HistoryEntry> allHistoryEntries = historyService.allHistory();

    assertThat(allHistoryEntries.size()).isEqualTo(6);

  }

  @Test
  public void testClearHistoryEntries() throws Exception {

    historyService.clear();
    List<HistoryEntry> allHistoryEntries = historyService.allHistory();

    assertThat(allHistoryEntries.size()).isEqualTo(0);

  }

  @Test
  public void testFilterHistoryEntriesByName() throws Exception {

    List<HistoryEntry> filteredHistoryEntries = historyService.filterHistoryByContent("1");

    assertThat(filteredHistoryEntries.size()).isEqualTo(2);

  }

  @Test
  public void testLoadAndStore() throws Exception {

    // Add a new contact to the history db and save it
    String newHistoryEntryDescription = (UUID.randomUUID()).toString();
    HistoryEntry newHistoryEntry = historyService.newHistoryEntry(newHistoryEntryDescription);
    newHistoryEntry.setNotes("dippy");

    int numberOfHistoryEntries = historyService.allHistory().size();

    // Store the history to the backing writeHistory
    historyService.writeHistory();

    // Clear the cached history and check it is empty
    historyService.clear();
    List<HistoryEntry> allHistoryEntries = historyService.allHistory();
    assertThat(allHistoryEntries.size()).isEqualTo(0);

    // Reload it - there should be the same number of history entries and the new history entry should be available
    historyService.loadHistory();

    allHistoryEntries = historyService.allHistory();

    assertThat(allHistoryEntries.size()).isEqualTo(numberOfHistoryEntries);

    List<HistoryEntry> reloadedHistoryEntries = historyService.filterHistoryByContent(newHistoryEntryDescription);
    HistoryEntry reloadedHistoryEntry = reloadedHistoryEntries.iterator().next();

    // Check everything round-tripped OK
    assertThat(reloadedHistoryEntry.getDescription()).isEqualTo(newHistoryEntryDescription);
    assertThat(reloadedHistoryEntry.getId()).isEqualTo(newHistoryEntry.getId());
    assertThat(reloadedHistoryEntry.getNotes()).isEqualTo(newHistoryEntry.getNotes());

  }
}
