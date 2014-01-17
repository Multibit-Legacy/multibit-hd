package org.multibit.hd.ui.views.components;

import org.multibit.hd.core.api.Contact;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteDecorator;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteFilter;
import org.multibit.hd.ui.views.components.select_contact.ContactComboBoxEditor;
import org.multibit.hd.ui.views.components.select_contact.ContactListCellRenderer;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.event.ActionListener;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised combo boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ComboBoxes {

  /**
   * Utilities have no public constructor
   */
  private ComboBoxes() {
  }

  /**
   * @return A new combo box with default styling (no listener since it will cause early event triggers during set up)
   */
  @SuppressWarnings("unchecked")
  public static <T> JComboBox<T> newComboBox(T[] items) {

    JComboBox<T> comboBox = new JComboBox<>(items);

    // Use a basic renderer to avoid confusing visual artifacts like tick marks
    comboBox.setRenderer(new BasicComboBoxRenderer());

    // Ensure we use the correct component orientation
    comboBox.applyComponentOrientation(Languages.currentComponentOrientation());

    return comboBox;

  }

  /**
   * @param listener The action listener to alert when the selectin is made
   *
   * @return A new "language" combo box
   */
  @SuppressWarnings("unchecked")
  public static JComboBox<String> newLanguagesComboBox(ActionListener listener) {

    JComboBox<String> comboBox = newComboBox(Languages.getLanguageNames(true));
    comboBox.setSelectedIndex(Languages.getIndexFromLocale(Languages.currentLocale()));
    comboBox.setEditable(false);

    // Add the listener at the end to avoid false events
    comboBox.addActionListener(listener);

    return comboBox;

  }

  /**
   * @return A new "recipient" combo box
   */
  @SuppressWarnings("unchecked")
  public static JComboBox<Contact> newRecipientComboBox(AutoCompleteFilter<Contact> filter) {

    JComboBox<Contact> comboBox = new JComboBox<>(filter.create());
    comboBox.setBackground(Themes.currentTheme.dataEntryBackground());

    // Use a contact editor to force use of the name field
    comboBox.setEditor(new ContactComboBoxEditor());

    // Use a contact list cell renderer to ensure thumbnails are maintained
    ListCellRenderer<Contact> renderer = new ContactListCellRenderer();
    comboBox.setRenderer(renderer);

    // Ensure we start with nothing selected
    comboBox.setSelectedIndex(-1);

    // Ensure we use the correct component orientation
    comboBox.applyComponentOrientation(Languages.currentComponentOrientation());

    AutoCompleteDecorator.apply(comboBox, filter);

    return comboBox;

  }

  /**
   * @param listener The action listener
   *
   * @return A new "seed size" combo box
   */
  @SuppressWarnings("unchecked")
  public static JComboBox<String> newSeedSizeComboBox(ActionListener listener) {

    JComboBox<String> comboBox = newComboBox(new String[]{
      "12",
      "18",
      "24"
    });
    comboBox.setSelectedIndex(0);
    comboBox.setEditable(false);

    // Add the listener at the end to avoid false events
    comboBox.addActionListener(listener);

    return comboBox;
  }

}
