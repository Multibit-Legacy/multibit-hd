package org.multibit.hd.core.services;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.brit.seed_phrase.Bip39SeedPhraseGenerator;
import org.multibit.hd.brit.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.config.Configurations;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.WalletIdTest;
import org.multibit.hd.core.events.ShutdownEvent;
import org.multibit.hd.core.managers.BackupManager;
import org.multibit.hd.core.managers.InstallationManager;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.utils.Addresses;
import org.multibit.hd.core.utils.Dates;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

public class PersistentContactServiceTest {

  private PersistentContactService contactService;

  @Before
  public void setUp() throws Exception {

    InstallationManager.unrestricted = true;

    Configurations.currentConfiguration = Configurations.newDefaultConfiguration();

    File applicationDirectory = InstallationManager.getOrCreateApplicationDataDirectory();

    // Create a wallet from a seed
    SeedPhraseGenerator seedGenerator = new Bip39SeedPhraseGenerator();
    byte[] seed1 = seedGenerator.convertToSeed(Bip39SeedPhraseGenerator.split(WalletIdTest.SEED_PHRASE_1));

    BackupManager.INSTANCE.initialise(applicationDirectory, Optional.<File>absent());

    long nowInSeconds = Dates.nowInSeconds();
    WalletManager
      .INSTANCE
      .getOrCreateMBHDSoftWalletSummaryFromSeed(
              applicationDirectory,
              seed1,
              nowInSeconds,
              WalletServiceTest.PASSWORD,
              "Example",
              "Example"
      );

    File contactDbFile = new File(applicationDirectory.getAbsolutePath() + File.separator + ContactService.CONTACTS_DATABASE_NAME);

    contactService = new PersistentContactService(contactDbFile);
    contactService.addDemoContacts();

  }

  @After
  public void tearDown() throws Exception {

    // Order is important here
    CoreServices.shutdownNow(ShutdownEvent.ShutdownType.SOFT);

    InstallationManager.shutdownNow();
    BackupManager.INSTANCE.shutdownNow();
    WalletManager.INSTANCE.shutdownNow(ShutdownEvent.ShutdownType.HARD);
  }

  @Test
  public void testNewContact() throws Exception {

    assertThat(contactService.newContact("Fred Bloggs").getName()).isEqualTo("Fred Bloggs");

  }

  @Test
  public void testAllContacts() throws Exception {

    List<Contact> allContacts = contactService.allContacts();

    assertThat(allContacts.size()).isEqualTo(6);

  }

  @Test
   public void testClearContacts() throws Exception {

     contactService.clear();
     List<Contact> allContacts = contactService.allContacts();

     assertThat(allContacts.size()).isEqualTo(0);

   }
  @Test
  public void testFilterContactsByName() throws Exception {

    List<Contact> filteredContacts = contactService.filterContactsByContent("Alice",false);

    assertThat(filteredContacts.size()).isEqualTo(2);

  }

  @Test
  public void testLoadAndStore() throws Exception {

    // Add a new contact to the contacts db and save it
    String newContactName = (UUID.randomUUID()).toString();
    Contact newContact = contactService.newContact(newContactName);
    contactService.addAll(Lists.newArrayList(newContact));

    newContact.setBitcoinAddress(Addresses.parse("1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty").get());
    newContact.setEmail("boppy");
    newContact.setExtendedPublicKey("soppy");
    newContact.setImagePath("sippy");
    newContact.setNotes("dippy");

    List<String> tags = Lists.newArrayList();
    tags.add("dappy");
    tags.add("frippy");
    tags.add("froppy");
    newContact.setTags(tags);

    int numberOfContacts = contactService.allContacts().size();

    // Store the contacts to the backing writeContacts
    contactService.writeContacts();

    // Clear the cached contacts and check it is empty
    contactService.clear();
    List<Contact> allContacts = contactService.allContacts();
    assertThat(allContacts.size()).isEqualTo(0);

    // Reload it - there should be the same number of contacts and the new contact should be available
    contactService.loadContacts();

    allContacts = contactService.allContacts();

    assertThat(allContacts.size()).isEqualTo(numberOfContacts);

    List<Contact> reloadedContacts = contactService.filterContactsByContent(newContactName,false);
    Contact reloadedContact = reloadedContacts.iterator().next();

    // Check everything round-tripped OK
    assertThat(reloadedContact.getName()).isEqualTo(newContactName);
    assertThat(reloadedContact.getBitcoinAddress()).isEqualTo(newContact.getBitcoinAddress());
    assertThat(reloadedContact.getEmail()).isEqualTo(newContact.getEmail());
    assertThat(reloadedContact.getExtendedPublicKey()).isEqualTo(newContact.getExtendedPublicKey());
    assertThat(reloadedContact.getImagePath()).isEqualTo(newContact.getImagePath());
    assertThat(reloadedContact.getId()).isEqualTo(newContact.getId());
    assertThat(reloadedContact.getNotes()).isEqualTo(newContact.getNotes());
    assertThat(reloadedContact.getTags()).isEqualTo(newContact.getTags());
  }
}
