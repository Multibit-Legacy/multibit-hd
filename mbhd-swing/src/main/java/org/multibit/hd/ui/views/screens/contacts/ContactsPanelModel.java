package org.multibit.hd.ui.views.screens.contacts;

import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.views.screens.AbstractScreenModel;
import org.multibit.hd.ui.views.screens.Screen;

import java.util.List;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the contacts screen</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactsPanelModel extends AbstractScreenModel {

  private final ContactService contactService;

  public ContactsPanelModel(Screen screen) {
    super(screen);

    // Provide an initial population of contacts
    this.contactService = CoreServices.getOrCreateContactService(getCurrentWalletId());
  }

  public List<Contact> getContacts() {

    return contactService.allContacts();
  }

  public void removeAll(List<Contact> selectedContacts) {

    contactService.removeAll(selectedContacts);

  }

  // TODO Move this into a wallet service
  private WalletId getCurrentWalletId() {
    if (WalletManager.INSTANCE.getCurrentWalletData().isPresent()) {
      return WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId();
    }

    // TODO this need removing
    return new WalletId("66666666-77777777-88888888-99999999-aaaaaaaa");
  }
}
