package org.multibit.hd.ui.views.screens.contacts;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.ui.views.screens.AbstractScreenModel;
import org.multibit.hd.ui.views.screens.Screen;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the contacts screen</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class ContactsScreenModel extends AbstractScreenModel {

  private final ContactService contactService;

  private final Stack<Collection<Contact>> undoStack = new Stack<>();

  public ContactsScreenModel(ContactService contactService, Screen screen) {
    super(screen);

    Preconditions.checkNotNull(contactService,"'contactService' must be present");
    this.contactService = contactService;

  }

  public List<Contact> getContacts() {

    return contactService.allContacts();
  }

  public List<Contact> filterContactsByContent(String query) {

    // Include all contacts
    return contactService.filterContactsByContent(query, false);
  }

  /**
   * <p>Provide access to the contact service for the "edit contact" wizard</p>
   *
   * @return The contact service
   */
  public ContactService getContactService() {
    return contactService;
  }

  /**
   * <p>Remove all the selected contacts from the cache and persistent store</p>
   *
   * @param selectedContacts The selected contacts
   */
  public void removeAll(Collection<Contact> selectedContacts) {

    undoStack.push(selectedContacts);

    contactService.removeAll(selectedContacts);

    contactService.writeContacts();

  }

  /**
   * @return The complete list of contacts (no filtering) with the most recent deletes re-instated
   */
  public Collection<Contact> undo() {

    if (canUndo()) {
      Collection<Contact> contacts = undoStack.pop();

      contactService.addAll(contacts);
      contactService.writeContacts();

    }

    return contactService.allContacts();

  }

  /**
   * @return true if there is something to undo
   */
  public boolean canUndo() {
    return !undoStack.empty();
  }
}
