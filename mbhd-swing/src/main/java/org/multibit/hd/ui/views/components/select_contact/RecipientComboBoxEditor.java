package org.multibit.hd.ui.views.components.select_contact;

import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.components.text_fields.ThemeAwareRecipientInputVerifier;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * <p>ComboBox editor to provide the following to combo boxes:</p>
 * <ul>
 * <li>Conversion between a string and a Recipient</li>
 * </ul>
 * <p>Uses "name" of Recipient.Contact as the string lookup</p>
 * <p>Deliberately ignores the Bitcoin address component to avoid false near addresses</p>
 *
 * @since 0.0.1
 *  
 */
public class RecipientComboBoxEditor implements ComboBoxEditor {

  protected JTextField editor;
  private Recipient recipient;

  public RecipientComboBoxEditor() {

    // Use a modified text field with a workaround
    editor = new ComboBoxTextField("", 0);
    editor.setName(MessageKey.RECIPIENT.getKey());

    // Apply theme
    editor.setBackground(Themes.currentTheme.dataEntryBackground());

    // Apply rounded corners for consistent LaF
    editor.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));

    // Validate as a Contact with Bitcoin address, or a direct Bitcoin address
    editor.setInputVerifier(new ThemeAwareRecipientInputVerifier());

  }

  public JTextField getEditorComponent() {
    return editor;
  }

  /**
   * Sets the item that should be edited.
   *
   * @param anObject the displayed value of the editor
   */
  public void setItem(Object anObject) {

    String editorText;

    if (anObject instanceof String) {

      // User is typing or has pasted in the editor
      editorText = (String) anObject;
      recipient = null;

    } else {

      // User has selected from the list
      Recipient recipient = (Recipient) anObject;

      if (recipient != null && recipient.getBitcoinAddress() != null) {
        // Choose either the contact name or a Bitcoin address
        if (recipient.getContact().isPresent()) {
          editorText = recipient.getContact().get().getName();
        } else {
          editorText = recipient.getBitcoinAddress();
        }
        this.recipient = recipient;
      } else {
        // Recipient does not have a Bitcoin address so clear the editor
        this.recipient = null;
        editorText = "";
      }
    }

    // Workaround for Java Bug #4530952
    if (!editorText.equals(editor.getText())) {
      editor.setText(editorText);
    }

  }

  public Object getItem() {

    if (recipient != null && recipient.getContact().isPresent()) {
      // There is a hit on a contact
      return recipient;
    } else {
      // Return the editor contents, which may be a pasted address
      return editor.getText();
    }

  }

  public void selectAll() {

    editor.selectAll();
    editor.requestFocus();

  }

  public void addActionListener(ActionListener l) {
    editor.addActionListener(l);
  }

  public void removeActionListener(ActionListener l) {
    editor.removeActionListener(l);
  }

  static class ComboBoxTextField extends JTextField {

    public ComboBoxTextField(String value, int n) {
      super(value, n);
    }

    // Workaround for Java Bug #4530952
    public void setText(String s) {
      if (getText().equals(s)) {
        return;
      }
      super.setText(s);
    }
  }

}
