package org.multibit.hd.ui.views.screens.contacts;

import com.google.common.eventbus.Subscribe;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.events.view.WizardHideEvent;
import org.multibit.hd.ui.views.screens.AbstractScreenModel;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactState;
import org.multibit.hd.ui.views.wizards.edit_contact.EditContactWizardModel;

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

  private final Stack<List<Contact>> undoStack = new Stack<>();

  public ContactsScreenModel(Screen screen) {
    super(screen);

    // Provide an initial population of contacts
    this.contactService = CoreServices.getOrCreateContactService(getCurrentWalletId());
  }

  public List<Contact> getContacts() {

    return contactService.allContacts();
  }

  public void removeAll(List<Contact> selectedContacts) {

    undoStack.push(selectedContacts);

    contactService.removeAll(selectedContacts);

  }

  public List<Contact> filterContactsByContent(String query) {

    return contactService.filterContactsByContent(query);
  }

  // TODO Move this into a wallet service
  private WalletId getCurrentWalletId() {

    if (WalletManager.INSTANCE.getCurrentWalletData().isPresent()) {
      return WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId();
    }

    // TODO this need removing
    return new WalletId("66666666-77777777-88888888-99999999-aaaaaaaa");
  }

  /**
   * @return The complete list of contacts (no filtering) with the most recent deletes re-instated
   */
  public List<Contact> undo() {

    if (!undoStack.empty()) {
      List<Contact> contacts = undoStack.pop();

      contactService.addAll(contacts);
    }

    return contactService.allContacts();

  }

  /**
   * <p>Handle the transfer of data from the "edit contact" wizard</p>
   *
   * @param event The "wizard hide" event
   */
  @Subscribe
  public void onWizardHideEvent(WizardHideEvent event) {

    // Filter other events
    if (!event.getPanelName().equals(EditContactState.ENTER_DETAILS.name())) {
      return;
    }

    // Transfer the data from the wizard back into the table model
    List<Contact> contacts = ((EditContactWizardModel) event.getWizardModel()).getContacts();

    contactService.updateContacts(contacts);
    contactService.writeContacts();

  }

  public ContactService getContactService() {
    return contactService;
  }
}
