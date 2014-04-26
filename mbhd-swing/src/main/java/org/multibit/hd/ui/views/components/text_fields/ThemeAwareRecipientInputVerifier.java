package org.multibit.hd.ui.views.components.text_fields;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.params.MainNetParams;
import org.multibit.hd.core.dto.Recipient;
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

  @Override
  public boolean verify(JComponent component) {

    if (component instanceof JComboBox) {

      JComboBox comboBox = ((JComboBox) component);

      Object item = comboBox.getEditor().getItem();

      if (item == null) {
        component.setBackground(invalidColor);
        return false;
      }

      // String is a direct Bitcoin address
      if (item instanceof String) {
        return verifyBitcoinAddress(comboBox, (String) item);
      }

      // Recipient relies on a valid Bitcoin address being present (filter should ensure this)
      if (item instanceof Recipient) {

        Recipient recipient = (Recipient) item;
        return verifyBitcoinAddress(comboBox, recipient.getBitcoinAddress());

      }

      // Not a String or Recipient so throw it out
      throw new IllegalArgumentException("'item' must be a String or Recipient");

    } else {
      throw new IllegalArgumentException("'component' must be a JComboBox");
    }

  }

  /**
   * @param comboBox       The combo box
   * @param bitcoinAddress The Bitcoin address
   *
   * @return True if the verification was successful
   */
  private boolean verifyBitcoinAddress(JComboBox comboBox, String bitcoinAddress) {

    // Deny empty values
    if (bitcoinAddress.trim().length() == 0) {
      comboBox.setBackground(invalidColor);
      return false;
    }

    // Parse the text as a Bitcoin address
    try {
      new Address(MainNetParams.get(), bitcoinAddress);

      // It's a valid Bitcoin address so stop now
      comboBox.setBackground(validColor);
      return true;

    } catch (AddressFormatException e) {
      comboBox.setBackground(invalidColor);
    }

    // Parse the text as a Recipient
    return false;

  }

}
