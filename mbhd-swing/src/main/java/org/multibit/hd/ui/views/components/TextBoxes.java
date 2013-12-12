package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised buttons</li>
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

    JTextField textField = new JTextField();
    textField.setBackground(Themes.currentTheme.dataEntryBackground());

    return textField;
  }

  /**
   * @return A new "Password" text field
   */
  public static JPasswordField newPassword() {

    JPasswordField passwordField = new JPasswordField();
    passwordField.setBackground(Themes.currentTheme.dataEntryBackground());

    return passwordField;
  }

  /**
   * @return A new "Notes" text area
   */
  public static JTextArea newNotes() {

    JTextArea textArea = new JTextArea();
    textArea.setBackground(Themes.currentTheme.dataEntryBackground());

    return textArea;
  }
}
