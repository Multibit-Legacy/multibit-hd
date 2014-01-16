package org.multibit.hd.ui.views.components.select_contact;

import org.multibit.hd.core.api.Contact;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *  
 */
public class ContactComboBoxEditor implements ComboBoxEditor {

  protected JTextField editor;
  private Contact contact;

  public ContactComboBoxEditor() {

    // Use a modified text field with a workaround
    editor = new ComboBoxTextField("", 0);
    editor.setBackground(Themes.currentTheme.dataEntryBackground());

  }

  public Component getEditorComponent() {
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
      editorText = (String) anObject;
    } else {
      Contact contact = (Contact) anObject;

      if (contact != null) {
        editorText = contact.getName();
        this.contact = contact;
      } else {
        editorText = "";
      }
    }

    // workaround for 4530952
    if (!editorText.equals(editor.getText())) {
      editor.setText(editorText);
    }
  }

  public Object getItem() {

    if (contact != null) {
      return contact;
    } else {
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

    // workaround for 4530952
    public void setText(String s) {
      if (getText().equals(s)) {
        return;
      }
      super.setText(s);
    }
  }

}
