package org.multibit.hd.ui.views.screens.contacts;

import com.google.common.base.Optional;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
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

  // TODO Have this injected through a WalletServices.getOrCreateContact() method
  private Optional<ContactService> contactService = Optional.absent();

  private final Stack<Collection<Contact>> undoStack = new Stack<>();

  public ContactsScreenModel(Screen screen) {
    super(screen);

  }


  public List<Contact> getContacts() {

    // TODO This construct is to disappear
    if (!contactService.isPresent()) {
      initialiseContact();
    }

    return contactService.get().allContacts();
  }

  public List<Contact> filterContactsByContent(String query) {

    // TODO This construct is to disappear
    if (!contactService.isPresent()) {
      initialiseContact();
    }
    return contactService.get().filterContactsByContent(query);
  }

  /**
   * <p>Provide access to the contact service for the "edit contact" wizard</p>
   *
   * @return The contact service
   */
  public ContactService getContactService() {
    return contactService.get();
  }

  /**
   * <p>Defer the initialisation of the contact service until a wallet ID is available</p>
   */
  private void initialiseContact() {

    if (!WalletManager.INSTANCE.getCurrentWalletData().isPresent()) {
      throw new IllegalStateException("Contacts should not be accessible without a wallet ID");
    }

    this.contactService = Optional.of(CoreServices.getOrCreateContactService(WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId()));
  }

  /**
   * <p>Remove all the selected contacts from the cache and persistent store</p>
   *
   * @param selectedContacts The selected contacts
   */
  public void removeAll(Collection<Contact> selectedContacts) {

    // TODO This construct is to disappear
    if (!contactService.isPresent()) {
      initialiseContact();
    }

    undoStack.push(selectedContacts);

    contactService.get().removeAll(selectedContacts);

    contactService.get().writeContacts();

  }

  /**
   * @return The complete list of contacts (no filtering) with the most recent deletes re-instated
   */
  public Collection<Contact> undo() {

    // TODO This construct is to disappear
    if (!contactService.isPresent()) {
      initialiseContact();
    }

    if (!undoStack.empty()) {
      Collection<Contact> contacts = undoStack.pop();

      contactService.get().addAll(contacts);

      contactService.get().writeContacts();

    }

    return contactService.get().allContacts();

  }

}
