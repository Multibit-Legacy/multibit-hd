package org.multibit.hd.ui.views.components.select_recipient;

import com.google.bitcoin.core.NetworkParameters;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.services.ContactService;
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

  protected RecipientComboBoxTextField editor;

  /**
   * @param contactService The contact service for the current wallet
   */
  public RecipientComboBoxEditor(ContactService contactService, NetworkParameters networkParameters) {

    Preconditions.checkNotNull(contactService, "'contactService' must be present");
    Preconditions.checkNotNull(networkParameters, "'networkParameters' must be present");

    // Use a modified text field to store the recipient
    editor = new RecipientComboBoxTextField("", 0);
    editor.setName(MessageKey.RECIPIENT.getKey());

    // Apply theme
    editor.setBackground(Themes.currentTheme.dataEntryBackground());

    // Apply rounded corners for consistent LaF
    editor.setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));

    // Validate as a Contact with Bitcoin address, or a direct Bitcoin address
    editor.setInputVerifier(new ThemeAwareRecipientInputVerifier(contactService, networkParameters));

  }

  public JTextField getEditorComponent() {
    return editor;
  }

  /**
   * Sets the item that should be edited.
   *
   * @param item The displayed value of the editor
   */
  @SuppressWarnings("unchecked")
  public void setItem(Object item) {

    // User is typing or has pasted in the editor
    if (item instanceof String) {

      editor.setText((String) item);
      editor.setRecipient(Optional.<Recipient>absent());

      return;

    }

    // User has selected from drop down list or input verifier has succeeded
    if (item instanceof Recipient) {

      final Optional<Recipient> recipient = Optional.fromNullable((Recipient) item);

      selectNameOrBitcoinAddress(recipient);

      editor.setRecipient(recipient);

      return;
    }

    // Just in case an Optional<Recipient> is passed in
    if (item instanceof Optional) {

      final Optional<Recipient> recipient = (Optional<Recipient>) item;

      if (recipient.isPresent()) {
        selectNameOrBitcoinAddress(recipient);
      } else {
        editor.setText("");
      }

      editor.setRecipient(recipient);

    }

  }


  /**
   * @return The current recipient if present, otherwise the text contents of the editor
   */
  public Object getItem() {

    if (editor.getRecipient().isPresent()) {
      return editor.getRecipient().get();
    } else {
      // Return the editor contents, which may be invalid content
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

  private void selectNameOrBitcoinAddress(Optional<Recipient> recipient) {

    if (recipient.get().getContact().isPresent()) {
      editor.setText(recipient.get().getContact().get().getName());
    } else {
      editor.setText(recipient.get().getBitcoinAddress().toString());
    }
  }

  /**
   * Specialised text field for tracking text and Recipient
   */
  public static class RecipientComboBoxTextField extends JTextField {

    private Optional<Recipient> recipient = Optional.absent();

    public RecipientComboBoxTextField(String value, int n) {
      super(value, n);
    }

    // Workaround for Java Bug #4530952
    public void setText(String s) {
      if (getText().equals(s)) {
        return;
      }
      super.setText(s);
    }

    /**
     * @param recipient The recipient to set
     */
    public void setRecipient(Optional<Recipient> recipient) {

      Preconditions.checkNotNull(recipient, "'recipient' must be present");
      this.recipient = recipient;
    }

    /**
     * @return The recipient
     */
    public Optional<Recipient> getRecipient() {
      return recipient;
    }
  }
}
