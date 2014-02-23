package org.multibit.hd.ui.views.components.auto_complete;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.dto.WalletId;
import org.multibit.hd.core.managers.WalletManager;
import org.multibit.hd.core.services.CoreServices;

import java.util.List;

/**
 * <p>Factory to provide the following to views:</p>
 * <ul>
 * <li>Creation of filters for auto-complete combo boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class AutoCompleteFilters {

  /**
   * Utilities have private constructors
   */
  private AutoCompleteFilters() {
  }

  /**
   * @return An auto-complete filter linked to the Contact API
   */
  public static AutoCompleteFilter<Recipient> newRecipientFilter() {

    return new AutoCompleteFilter<Recipient>() {

      @Override
      public Recipient[] create() {

        List<Contact> contacts = CoreServices.getOrCreateContactService(Optional.of(getCurrentWalletId())).allContacts();

        return populateRecipients(contacts);

      }

      @Override
      public Recipient[] update(String fragment) {

        if (Strings.isNullOrEmpty(fragment)) {
          return new Recipient[]{};
        }

        List<Contact> contacts = CoreServices.getOrCreateContactService(Optional.of(getCurrentWalletId())).filterContactsByContent(fragment);

        return populateRecipients(contacts);
      }

      /**
       *
       * @param contacts The contacts to add to the recipients
       * @return The recipients
       */
      private Recipient[] populateRecipients(List<Contact> contacts) {

        Recipient[] recipients = new Recipient[contacts.size()];

        int i = 0;
        for (Contact contact : contacts) {
          Recipient recipient = new Recipient(contact.getBitcoinAddress().or(""));
          recipient.setContact(contact);
          recipients[i] = recipient;
          i++;
        }

        return recipients;
      }

      private WalletId getCurrentWalletId() {
        return WalletManager.INSTANCE.getCurrentWalletData().get().getWalletId();
      }
    };

  }

}
