package org.multibit.hd.core.services;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.api.Contact;
import org.multibit.hd.core.managers.WalletManagerTest;

import java.io.File;
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
    contactService.newContact(newContactName);

    int numberOfContacts = contactService.allContacts(1, 10).size();

    // Clear the contacts db and check it is empty
    contactService.clear();
    Set<Contact> allContacts = contactService.allContacts(1, 10);
    assertThat(allContacts.size()).isEqualTo(0);

    // Reload it - there should be the same number of contacts and the new contact should be available
    contactService.load();

    allContacts = contactService.allContacts(1, 10);

    assertThat(allContacts.size()).isEqualTo(numberOfContacts);

    Set<Contact> reloadedContacts = contactService.filterContactsByName(1, 10, newContactName);
    assertThat(reloadedContacts.iterator().next().getName()).isEqualTo(newContactName);
  }
}
