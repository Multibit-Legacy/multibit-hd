package org.multibit.hd.ui.views.components.text_fields;

import org.bitcoinj.core.Address;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.core.utils.Addresses;
import org.multibit.hd.ui.views.components.select_recipient.RecipientComboBoxEditor;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Input verifier to provide the following to UI:</p>
 * <ul>
 * <li>Denies an empty field</li>
 * <li>Applies Recipient lookup rules</li>
 * <li>Applies Bitcoinj Address parsing rules if a direct address is detected</li>
 * <li>Apply theme colouring if the value is valid/invalid</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ThemeAwareRecipientInputVerifier extends InputVerifier {

  private final Color invalidColor = Themes.currentTheme.invalidDataEntryBackground();
  private final Color validColor = Themes.currentTheme.dataEntryBackground();

  private final ContactService contactService;

  /**
   * @param contactService    The contact service for the current wallet
   *
   */
  public ThemeAwareRecipientInputVerifier(ContactService contactService) {

    Preconditions.checkNotNull(contactService, "'contactService' must be present");

    this.contactService = contactService;

  }

  @Override
  public boolean verify(JComponent component) {

    if (component instanceof RecipientComboBoxEditor.RecipientComboBoxTextField) {

      boolean isValid = false;

      RecipientComboBoxEditor.RecipientComboBoxTextField textField = (RecipientComboBoxEditor.RecipientComboBoxTextField) component;

      String text = textField.getText();

      if (!Strings.isNullOrEmpty(text)) {

        // Treat as an address first
        final Optional<Address> enteredAddress = Addresses.parse(text);
        if (enteredAddress.isPresent()) {

          // Validated as a Bitcoin address
          isValid = true;

          // Create an anonymous recipient
          Recipient recipient = new Recipient(enteredAddress.get());
          textField.setRecipient(Optional.of(recipient));

        } else {

          // Try again as a recipient
          Optional<Contact> contactOptional = contactService.filterContactsForSingleMatch(text, true);
          if (contactOptional.isPresent()) {

            Contact contact = contactOptional.get();

            // Verify that the only possibility has a valid Bitcoin address
            Optional<Address> bitcoinAddress = contact.getBitcoinAddress();
            if (bitcoinAddress.isPresent()) {
              Recipient recipient = new Recipient(bitcoinAddress.get());
              recipient.setContact(contact);

              textField.setText(contact.getName());
              textField.setRecipient(Optional.of(recipient));

              // Validated as a recipient
              isValid = true;

            }
          }

        }

      }

      // Apply the appropriate color based on the result
      component.setBackground(isValid ? validColor : invalidColor);

      return isValid;

    } else {
      throw new IllegalArgumentException("'component' must be a JTextField. Actual: " + component.getClass().getCanonicalName());
    }

  }

}
