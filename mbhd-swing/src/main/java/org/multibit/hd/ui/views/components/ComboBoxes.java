package org.multibit.hd.ui.views.components;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.dto.BackupSummary;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteDecorator;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteFilter;
import org.multibit.hd.ui.views.components.select_backup_summary.BackupSummaryListCellRenderer;
import org.multibit.hd.ui.views.components.select_contact.RecipientComboBoxEditor;
import org.multibit.hd.ui.views.components.select_contact.RecipientListCellRenderer;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

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
   * @param items The items for the combo box model
   *
   * @return A new editable combo box with default styling (no listener since it will cause early event triggers during set up)
   */
  public static <T> JComboBox<T> newComboBox(T[] items) {

    JComboBox<T> comboBox = new JComboBox<>(items);

    // Required to match icon button heights
    comboBox.setMinimumSize(new Dimension(100, MultiBitUI.NORMAL_ICON_SIZE + 14));

    // Required to blend in with panel
    comboBox.setBackground(Themes.currentTheme.detailPanelBackground());

    // Ensure we use the correct component orientation
    comboBox.applyComponentOrientation(Languages.currentComponentOrientation());

    // Ensure that keyboard navigation does not trigger action events
    comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);

    return comboBox;

  }

  /**
   * @return A new read only combo box (no listeners attached)
   */
  public static <T> JComboBox<T> newReadOnlyComboBox(T[] items) {

    JComboBox<T> comboBox = newComboBox(items);

    comboBox.setEditable(false);

    // Apply theme
    comboBox.setBackground(Themes.currentTheme.readOnlyComboBox());

    return comboBox;

  }

  /**
   * @param listener The action listener to alert when the selection is made
   *
   * @return A new "contact checkbox" combo box (all, none)
   */
  public static JComboBox<String> newContactsCheckboxComboBox(ActionListener listener) {

    String[] items = new String[]{
      Languages.safeText(MessageKey.ALL),
      Languages.safeText(MessageKey.NONE),
    };

    JComboBox<String> comboBox = newReadOnlyComboBox(items);

    // Add the listener at the end to avoid false events
    comboBox.addActionListener(listener);

    return comboBox;

  }

  /**
   * @param listener The action listener to alert when the selection is made
   *
   * @return A new "language" combo box
   */
  public static JComboBox<String> newLanguagesComboBox(ActionListener listener) {

    JComboBox<String> comboBox = newReadOnlyComboBox(Languages.getLanguageNames(true));

    comboBox.setSelectedIndex(Languages.getIndexFromLocale(Languages.currentLocale()));

    // Add the listener at the end to avoid false events
    comboBox.addActionListener(listener);

    return comboBox;

  }

  /**
   * @param filter The contact auto-complete filter
   *
   * @return A new "recipient" combo box with auto-complete functionality
   */
  public static JComboBox<Recipient> newRecipientComboBox(AutoCompleteFilter<Recipient> filter) {

    JComboBox<Recipient> comboBox = newComboBox(filter.create());

    comboBox.setEditable(true);

    // Use a contact editor to force use of the name field
    comboBox.setEditor(new RecipientComboBoxEditor());

    // Use a contact list cell renderer to ensure thumbnails are maintained
    ListCellRenderer<Recipient> renderer = new RecipientListCellRenderer((JTextField) comboBox.getEditor().getEditorComponent());
    comboBox.setRenderer(renderer);

    // Ensure we start with nothing selected
    comboBox.setSelectedIndex(-1);

    AutoCompleteDecorator.apply(comboBox, filter);

    return comboBox;

  }

  /**
   * @param listener        The action listener
   * @param backupSummaries The backup summary entries
   *
   * @return A new "recipient" combo box with auto-complete functionality
   */
  public static JComboBox<BackupSummary> newBackupSummaryComboBox(ActionListener listener, List<BackupSummary> backupSummaries) {

    Preconditions.checkNotNull(listener, "'listener' must be present");
    Preconditions.checkNotNull(listener, "'backupSummaries' must be present");

    // Convert the backup summaries to an array
    BackupSummary[] backupSummaryArray = new BackupSummary[backupSummaries.size()];

    JComboBox<BackupSummary> comboBox = newReadOnlyComboBox(backupSummaryArray);

    // Use a backup summary list cell renderer to ensure the correct fields are displayed
    ListCellRenderer<BackupSummary> renderer = new BackupSummaryListCellRenderer();
    comboBox.setRenderer(renderer);

    // Ensure we start with nothing selected
    comboBox.setSelectedIndex(-1);

    return comboBox;

  }

  /**
   * @param listener The action listener
   *
   * @return A new "seed size" combo box
   */
  public static JComboBox<String> newSeedSizeComboBox(ActionListener listener) {

    JComboBox<String> comboBox = newReadOnlyComboBox(new String[]{
      "12",
      "18",
      "24"
    });
    comboBox.setSelectedIndex(0);

    // Add the listener at the end to avoid false events
    comboBox.addActionListener(listener);

    return comboBox;
  }

}