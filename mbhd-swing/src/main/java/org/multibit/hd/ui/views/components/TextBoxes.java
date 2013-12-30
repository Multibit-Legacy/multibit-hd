package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised text boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class TextBoxes {

  /**
   * Utilities have no public constructor
   */
  private TextBoxes() {
  }

  /**
   * @return A new "Recipient" auto-completer
   */
  public static JTextField newRecipient() {

    JTextField textField = new JTextField(40);
    textField.setBackground(Themes.currentTheme.dataEntryBackground());

    return textField;
  }

  /**
   * @return A new "Password" text field
   */
  public static JPasswordField newPassword() {

    JPasswordField passwordField = new JPasswordField(40);
    passwordField.setBackground(Themes.currentTheme.dataEntryBackground());

    return passwordField;
  }

  /**
   * @return A new "Notes" text area
   */
  public static JTextArea newNotes() {

    JTextArea textArea = new JTextArea(5, 40);
    textArea.setBackground(Themes.currentTheme.dataEntryBackground());

    return textArea;
  }

  /**
   * @return A new "seed phrase" text area
   */
  public static JTextArea newSeedPhrase() {

    // Allow for 256 characters
    JTextArea textArea = new JTextArea(5, 40);
    textArea.setBackground(Themes.currentTheme.readOnlyBackground());
    textArea.setFont(new Font("Courier New", Font.PLAIN, 14));

    // Ensure line and word wrapping occur as required
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    // Prevent copy/paste operations
    textArea.setTransferHandler(null);
    textArea.setEditable(false);

    return textArea;

  }

  /**
   * @return A new "Amount" text field for currency entry
   */
  public static JTextField newCurrency(String amount) {

    JTextField textField = new JTextField(amount, 20);
    textField.setBackground(Themes.currentTheme.dataEntryBackground());

    return textField;
  }
}
