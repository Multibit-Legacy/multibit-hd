package org.multibit.hd.core.services;

import com.google.common.collect.Sets;
import org.multibit.hd.core.api.Contact;

import java.util.Set;
import java.util.UUID;

/**
 * <p>Service to provide the following to application:</p>
 * <ul>
 * <li>CRUD operations on Contacts</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactService {

  private final Set<Contact> contacts = Sets.newHashSet(
    newContact("Alice Capital"),
    newContact("Bob Capital"),
    newContact("Charles Capital"),
    newContact("Derek Capital"),
    newContact("alice Lower"),
    newContact("alicia Lower")
  );

  /**
   * Reduced visibility constructor to prevent accidental instance creation outside of CoreServices
   */
  ContactService() {
  }

  /**
   * @param name A name (normally first name and last name)
   *
   * @return A new contact with a fresh ID
   */
  public Contact newContact(String name) {
    return new Contact(UUID.randomUUID(), name);
  }

  /**
   * @param page            The page number
   * @param contactsPerPage The number of contacts per page
   *
   * @return A set of all Contacts for the given page
   */
  public Set<Contact> allContacts(int page, int contactsPerPage) {
    return contacts;
  }

  /**
   * @param page            The page number
   * @param contactsPerPage The number of contacts per page
   * @param query           The text fragment to match (case-insensitive, anywhere in the name)
   *
   * @return A filtered set of Contacts for the given page and query
   */
  public Set<Contact> filterContactsByName(int page, int contactsPerPage, String query) {

    String lowerQuery = query.toLowerCase();

    Set<Contact> filteredContacts = Sets.newHashSet();

    for (Contact contact : contacts) {
      if (contact.getName().toLowerCase().contains(lowerQuery)) {
        filteredContacts.add(contact);
      }
    }

    return filteredContacts;
  }

}
