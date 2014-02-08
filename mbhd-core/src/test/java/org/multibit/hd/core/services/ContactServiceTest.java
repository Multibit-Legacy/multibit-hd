package org.multibit.hd.core.services;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.api.Contact;
import org.multibit.hd.core.api.StarStyle;
import org.multibit.hd.core.managers.WalletManagerTest;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContactServiceTest {

  private ContactService contactService;
  private File contactDbFile;

  @Before
  public void setUp() throws Exception {

    File temporaryDirectory = WalletManagerTest.makeRandomTemporaryDirectory();
    contactDbFile = new File(temporaryDirectory.getAbsolutePath() + File.separator + ContactService.CONTACTS_DATABASE_NAME);

    contactService = new ContactService(contactDbFile);
    contactService.addDemoContacts();

  }

  @Test
  public void testNewContact() throws Exception {

    assertThat(contactService.newContact("Fred Bloggs").getName()).isEqualTo("Fred Bloggs");

  }

  @Test
  public void testAllContacts() throws Exception {

    Set<Contact> allContacts = contactService.allContacts(1, 10);

    assertThat(allContacts.size()).isEqualTo(6);

  }

  @Test
   public void testClearContacts() throws Exception {

     contactService.clear();
     Set<Contact> allContacts = contactService.allContacts(1, 10);

     assertThat(allContacts.size()).isEqualTo(0);

   }
  @Test
  public void testFilterContactsByName() throws Exception {

    Set<Contact> filteredContacts = contactService.filterContactsByName(1, 10, "Alice");

    assertThat(filteredContacts.size()).isEqualTo(2);

  }

  @Test
  public void testLoadAndStore() throws Exception {
    // Add a new contact to the contacts db and save it
    String newContactName = (UUID.randomUUID()).toString();
    Contact newContact = contactService.newContact(newContactName);

    newContact.setBitcoinAddress("bippy");
    newContact.setEmail("boppy");
    newContact.setExtendedPublicKey("soppy");
    newContact.setImagePath("sippy");
    newContact.setNotes("dippy");

    List<String> tags = Lists.newArrayList();
    tags.add("dappy");
    tags.add("frippy");
    tags.add("froppy");
    newContact.setTags(tags);

    Random random = new Random();
    int which = random.nextInt(5);
    StarStyle starStyle;
    switch(which) {
      case 0: starStyle = StarStyle.EMPTY; break;
      case 1: starStyle = StarStyle.FILL_1; break;
      case 2: starStyle = StarStyle.FILL_2; break;
      case 3: starStyle = StarStyle.FILL_3; break;
      case 4: starStyle = StarStyle.UNKNOWN; break;
      default: starStyle = StarStyle.UNKNOWN; break;
    }
    newContact.setStarStyle(starStyle);
    int numberOfContacts = contactService.allContacts(1, 10).size();

    // Store the contacts to the backing store
    contactService.store();

    // Clear the cached contacts and check it is empty
    contactService.clear();
    Set<Contact> allContacts = contactService.allContacts(1, 10);
    assertThat(allContacts.size()).isEqualTo(0);

    // Reload it - there should be the same number of contacts and the new contact should be available
    contactService.load();

    allContacts = contactService.allContacts(1, 10);

    assertThat(allContacts.size()).isEqualTo(numberOfContacts);

    Set<Contact> reloadedContacts = contactService.filterContactsByName(1, 10, newContactName);
    Contact reloadedContact = reloadedContacts.iterator().next();

    // Check everything roundtripped ok
    assertThat(reloadedContact.getName()).isEqualTo(newContactName);
    assertThat(reloadedContact.getBitcoinAddress()).isEqualTo(newContact.getBitcoinAddress());
    assertThat(reloadedContact.getEmail()).isEqualTo(newContact.getEmail());
    assertThat(reloadedContact.getExtendedPublicKey()).isEqualTo(newContact.getExtendedPublicKey());
    assertThat(reloadedContact.getImagePath()).isEqualTo(newContact.getImagePath());
    assertThat(reloadedContact.getId()).isEqualTo(newContact.getId());
    assertThat(reloadedContact.getNotes()).isEqualTo(newContact.getNotes());
    assertThat(reloadedContact.getStarStyle()).isEqualTo(newContact.getStarStyle());
    assertThat(reloadedContact.getTags()).isEqualTo(newContact.getTags());
  }
}
