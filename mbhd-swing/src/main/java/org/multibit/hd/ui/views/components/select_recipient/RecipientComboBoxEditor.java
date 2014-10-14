package org.multibit.hd.ui.views.components.select_recipient;

import org.bitcoinj.core.NetworkParameters;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.services.ContactService;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.components.text_fields.ThemeAwareRecipientInputVerifier;
import org.multibit.hd.ui.views.themes.Themes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
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

  private static final Logger log = LoggerFactory.getLogger(RecipientComboBoxEditor.class);

  private static final int MINIMUM_BITCOIN_ADDRESS_LENGTH = 26; // Source: https://en.bitcoin.it/wiki/Address

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
    final ThemeAwareRecipientInputVerifier verifier = new ThemeAwareRecipientInputVerifier(contactService);
    editor.setInputVerifier(verifier);

    editor.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent e) {
        handleEvent(e);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        handleEvent(e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        handleEvent(e);
      }

      private void handleEvent(DocumentEvent e) {
        // User has either typed or pasted - check the update text on every keystroke for early validation
        try {
          String text = e.getDocument().getText(0, e.getDocument().getLength());
          log.debug("Saw a handleEvent with item = '" + text + "'");

          int currentLength = text.length();
          // If longer than the length of a minimum bitcoin address then verify.
          // This will give some false positives if the user types in a long recipient address manually but works reasonably well
          if (currentLength >= MINIMUM_BITCOIN_ADDRESS_LENGTH) {
            verifier.verify(editor);
          }
        } catch (BadLocationException e1) {
          e1.printStackTrace();
        }
      }
    });
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

      log.debug("Saw a setItem with item = '" + item + "'");

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
