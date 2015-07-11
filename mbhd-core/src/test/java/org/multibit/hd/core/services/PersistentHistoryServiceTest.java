package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import org.bitcoinj.crypto.MnemonicCode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.commons.utils.Dates;
import org.multibit.hd.brit.core.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.HistoryEntry;
import org.multibit.hd.core.dto.WalletIdTest;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

public class PersistentHistoryServiceTest {

  private PersistentHistoryService historyService;
  private WalletSummary walletSummary;

  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] entropy1 = MnemonicCode.INSTANCE.toEntropy(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));
    byte[] seed1 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));

    BackupManager.INSTANCE.initialise(applicationDirectory, Optional.<File>absent());

    long nowInSeconds = Dates.nowInSeconds();
    walletSummary = WalletManager
      .INSTANCE
      .getOrCreateMBHDSoftWalletSummaryFromEntropy(
              applicationDirectory,
              entropy1,
              seed1,
              nowInSeconds,
              WalletServiceTest.PASSWORD,
              "Example",
              "Example",
              false); // No need to sync

    File contactDbFile = new File(applicationDirectory.getAbsolutePath() + File.separator + HistoryService.HISTORY_DATABASE_NAME);

    historyService = new PersistentHistoryService(contactDbFile);
    historyService.addDemoHistory();

  }

  @After
  public void tearDown() throws Exception {

    // Order is important here
    CoreServices.shutdownNow(ShutdownEvent.ShutdownType.SOFT);

    InstallationManager.shutdownNow(ShutdownEvent.ShutdownType.SOFT);
    BackupManager.INSTANCE.shutdownNow();
    WalletManager.INSTANCE.shutdownNow(ShutdownEvent.ShutdownType.HARD);
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
    historyService.loadHistory((String)walletSummary.getWalletPassword().getPassword());

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
