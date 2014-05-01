package org.multibit.hd.ui.views.components.text_fields;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.ui.views.components.select_recipient.RecipientComboBoxEditor;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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

  private static final Logger log = LoggerFactory.getLogger(ThemeAwareRecipientInputVerifier.class);

  private final Color invalidColor = Themes.currentTheme.invalidDataEntryBackground();
  private final Color validColor = Themes.currentTheme.dataEntryBackground();

  private final ContactService contactService;
  private final NetworkParameters networkParameters;

  /**
   * @param contactService    The contact service for the current wallet
   * @param networkParameters The network parameters
   */
  public ThemeAwareRecipientInputVerifier(ContactService contactService, NetworkParameters networkParameters) {

    Preconditions.checkNotNull(contactService, "'contactService' must be present");

    this.contactService = contactService;
    this.networkParameters = networkParameters;

  }

  @Override
  public boolean verify(JComponent component) {

    if (component instanceof RecipientComboBoxEditor.RecipientComboBoxTextField) {

      boolean isValid = false;

      RecipientComboBoxEditor.RecipientComboBoxTextField textField = (RecipientComboBoxEditor.RecipientComboBoxTextField) component;

      String text = textField.getText();

      if (text != null) {

        log.debug("Verify {} as address", text);

        // Treat as an address first
        final Optional<Address> enteredAddress = verifyBitcoinAddress(text);
        if (enteredAddress.isPresent()) {

          // Validated as a Bitcoin address
          isValid = true;

          // Create an anonymous recipient
          Recipient recipient = new Recipient(enteredAddress.get());
          textField.setRecipient(Optional.of(recipient));

          log.debug("Set anonymous recipient on text field");

        } else {

          log.debug("Verify {} as recipient", text);

          // Try again as a recipient
          List<Contact> contacts = contactService.filterContactsByContent(text, true);
          if (contacts.size() == 1) {

            Contact contact = contacts.get(0);
            // Verify that the only possibility has a valid Bitcoin address
            Optional<String> bitcoinAddress = contact.getBitcoinAddress();
            if (bitcoinAddress.isPresent()) {
              Optional<Address> contactAddress = verifyBitcoinAddress(bitcoinAddress.get());
              if (contactAddress.isPresent()) {

                isValid = true;

                Recipient recipient = new Recipient(contactAddress.get());
                recipient.setContact(contact);

                textField.setText(contact.getName());
                textField.setRecipient(Optional.of(recipient));

                log.debug("Set contact recipient on text field");

              }
            }
          }

        }

      }

      log.debug("Is valid: {}", isValid);

      // Apply the appropriate color based on the result
      component.setBackground(isValid ? validColor : invalidColor);

      return isValid;

    } else {
      throw new IllegalArgumentException("'component' must be a JTextField. Actual: " + component.getClass().getCanonicalName());
    }

  }

  /**
   * TODO (GR) Consider adding this to an Addresses factory
   *
   * @param bitcoinAddress The Bitcoin address
   *
   * @return A Bitcoin address if the text is valid
   */
  private Optional<Address> verifyBitcoinAddress(String bitcoinAddress) {

    // Deny empty values
    if (bitcoinAddress.trim().length() == 0) {
      return Optional.absent();
    }

    // Parse the text as a Bitcoin address
    try {
      return Optional.of(new Address(networkParameters, bitcoinAddress));
    } catch (AddressFormatException e) {
      // Do nothing
    }

    // Must have failed to be here
    return Optional.absent();

  }

}
