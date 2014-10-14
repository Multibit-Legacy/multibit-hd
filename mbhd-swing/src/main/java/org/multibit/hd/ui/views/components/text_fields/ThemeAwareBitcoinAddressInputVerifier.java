package org.multibit.hd.ui.views.components.text_fields;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.NetworkParameters;
import com.google.common.base.Preconditions;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * <p>Input verifier to provide the following to UI:</p>
 * <ul>
 * <li>Permits an empty field</li>
 * <li>Applies Bitcoinj Address parsing rules</li>
 * <li>Apply theme colouring if the value is valid/invalid</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ThemeAwareBitcoinAddressInputVerifier extends InputVerifier {

  private final Color invalidColor = Themes.currentTheme.invalidDataEntryBackground();
  private final Color validColor = Themes.currentTheme.dataEntryBackground();

  private final NetworkParameters networkParameters;

  /**
   * @param networkParameters The Bitcoin network parameters
   */
  public ThemeAwareBitcoinAddressInputVerifier(NetworkParameters networkParameters) {

    Preconditions.checkNotNull(networkParameters,"'networkParameters' must be present");

    this.networkParameters = networkParameters;
  }

  public boolean verify(JComponent component) {

    String text = ((JTextComponent) component).getText();

    // Permit empty values
    if (text == null || text.trim().length() == 0) {
      return true;
    }

    // Parse the text as a Bitcoin address
    try {
      new Address(networkParameters, text);
    } catch (AddressFormatException e) {
      component.setBackground(invalidColor);
      return false;
    }

    // Must be OK to be here
    component.setBackground(validColor);

    return true;
  }

}
