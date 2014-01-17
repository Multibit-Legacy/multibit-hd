package org.multibit.hd.core.services;

import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.api.Contact;

import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContactServiceTest {

  private ContactService testObject;

  @Before
  public void setUp() throws Exception {

    testObject = new ContactService();

  }

  @Test
  public void testNewContact() throws Exception {

    assertThat(testObject.newContact("Fred Bloggs").getName()).isEqualTo("Fred Bloggs");

  }

  @Test
  public void testAllContacts() throws Exception {

    Set<Contact> allContacts = testObject.allContacts(1, 10);

    assertThat(allContacts.size()).isEqualTo(6);

  }

  @Test
  public void testFilterContactsByName() throws Exception {

    Set<Contact> filteredContacts = testObject.filterContactsByName(1, 10, "Alice");

    assertThat(filteredContacts.size()).isEqualTo(2);

  }
}
