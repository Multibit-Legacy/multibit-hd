package org.multibit.hd.ui.views.components.text_fields;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.params.MainNetParams;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.dto.Contact;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.ui.views.themes.Themes;

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

  private final Color invalidColor = Themes.currentTheme.invalidDataEntryBackground();
  private final Color validColor = Themes.currentTheme.dataEntryBackground();

  private final ContactService contactService;

  /**
   * @param contactService The contact service for the current wallet
   */
  public ThemeAwareRecipientInputVerifier(ContactService contactService) {

    Preconditions.checkNotNull(contactService, "'contactService' must be present");

    this.contactService = contactService;

  }

  @Override
  public boolean verify(JComponent component) {


    if (component instanceof JTextField) {

      boolean isValid = false;

      JTextField textField = ((JTextField) component);

      String text = textField.getText();

      if (text != null) {

        // Guess the content type
        if (text.startsWith("1") || text.startsWith("3")) {

          // String is a direct Bitcoin address
          isValid = verifyBitcoinAddress(text);

        } else {

          // Treat as a recipient
          List<Contact> contacts = contactService.filterContactsByContent(text, true);
          if (contacts.size() == 1) {
            // Verify that the only possibility has a valid Bitcoin address
            Optional<String> bitcoinAddress = contacts.get(0).getBitcoinAddress();
            if (bitcoinAddress.isPresent()) {
              isValid = verifyBitcoinAddress(bitcoinAddress.get());
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

  /**
   * @param bitcoinAddress The Bitcoin address
   *
   * @return True if the verification was successful
   */
  private boolean verifyBitcoinAddress(String bitcoinAddress) {

    // Deny empty values
    if (bitcoinAddress.trim().length() == 0) {
      return false;
    }

    // Parse the text as a Bitcoin address
    try {
      new Address(MainNetParams.get(), bitcoinAddress);
      return true;

    } catch (AddressFormatException e) {
      // Do nothing
    }

    // Must have failed to be here
    return false;

  }

}
