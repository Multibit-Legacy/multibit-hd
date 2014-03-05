package org.multibit.hd.ui.views.components;

import com.google.common.base.Preconditions;
import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.dto.BackupSummary;
import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.i18n.LanguageKey;
import org.multibit.hd.ui.i18n.Languages;
import org.multibit.hd.ui.i18n.MessageKey;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteDecorator;
import org.multibit.hd.ui.views.components.auto_complete.AutoCompleteFilter;
import org.multibit.hd.ui.views.components.display_amount.BitcoinSymbolListCellRenderer;
import org.multibit.hd.ui.views.components.renderers.BackupSummaryListCellRenderer;
import org.multibit.hd.ui.views.components.renderers.LanguageListCellRenderer;
import org.multibit.hd.ui.views.components.select_contact.RecipientComboBoxEditor;
import org.multibit.hd.ui.views.components.select_contact.RecipientListCellRenderer;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

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
   * The "languages" combo box action command
   */
  public static final String LANGUAGES_COMMAND = "languages";
  /**
   * The "placement" combo box action command
   */
  public static final String PLACEMENT_COMMAND = "placement";
  /**
   * The "decimal separator" combo box action command
   */
  public static final String DECIMAL_COMMAND = "decimal";
  /**
   * The "grouping separator" combo box action command
   */
  public static final String GROUPING_COMMAND = "grouping";
  /**
   * The "local symbol" combo box action command
   */
  public static final String LOCAL_SYMBOL_COMMAND = "localSymbol";
  /**
   * The "Bitcoin symbol" combo box action command
   */
  public static final String BITCOIN_SYMBOL_COMMAND = "bitcoinSymbol";

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
    comboBox.setMinimumSize(new Dimension(25, MultiBitUI.NORMAL_ICON_SIZE + 14));

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
   * @return A new "history checkbox" combo box (all, none) - kept separate from contacts
   */
  public static JComboBox<String> newHistoryCheckboxComboBox(ActionListener listener) {

    return newContactsCheckboxComboBox(listener);

  }

  /**
   * @param listener The action listener to alert when the selection is made
   * @param locale   The locale to use for initial selection
   *
   * @return A new "language" combo box containing all supported languages and variants
   */
  public static JComboBox<String> newLanguagesComboBox(ActionListener listener, Locale locale) {

    // Get the language names in the order they are declared
    String[] languageNames = new String[LanguageKey.values().length];
    int i = 0;
    for (LanguageKey languageKey : LanguageKey.values()) {
      languageNames[i] = languageKey.getLanguageName();
      i++;
    }

    // Populate the combo box and declare a suitable renderer
    JComboBox<String> comboBox = newReadOnlyComboBox(languageNames);
    comboBox.setRenderer(new LanguageListCellRenderer());
    comboBox.setMaximumRowCount(12);

    // Can use the ordinal due to the declaration ordering
    comboBox.setSelectedIndex(LanguageKey.fromLocale(locale).ordinal());

    // Add the listener at the end to avoid false events
    comboBox.setActionCommand(LANGUAGES_COMMAND);
    comboBox.addActionListener(listener);

    return comboBox;

  }

  /**
   * @param listener             The action listener to alert when the selection is made
   * @param bitcoinConfiguration The Bitcoin configuration to use
   *
   * @return A new "decimal" combo box
   */
  public static JComboBox<String> newDecimalComboBox(ActionListener listener, BitcoinConfiguration bitcoinConfiguration) {

    String[] decimalSeparators = Languages.getAmountSeparators();
    JComboBox<String> comboBox = newReadOnlyComboBox(decimalSeparators);

    // Determine the first matching separator
    Character decimal = bitcoinConfiguration.getDecimalSeparator();
    for (int i = 0; i < decimalSeparators.length; i++) {
      if (decimal.equals(decimalSeparators[i].charAt(0))) {
        comboBox.setSelectedIndex(i);
        break;
      }
    }

    // Add the listener at the end to avoid false events
    comboBox.setActionCommand(DECIMAL_COMMAND);
    comboBox.addActionListener(listener);

    return comboBox;

  }

  /**
   * @param listener             The action listener to alert when the selection is made
   * @param bitcoinConfiguration The Bitcoin configuration to use
   *
   * @return A new "decimal" combo box
   */
  public static JComboBox<String> newGroupingComboBox(ActionListener listener, BitcoinConfiguration bitcoinConfiguration) {

    String[] groupingSeparators = Languages.getAmountSeparators();
    JComboBox<String> comboBox = newReadOnlyComboBox(groupingSeparators);

    // Determine the first matching separator
    Character grouping = bitcoinConfiguration.getGroupingSeparator();
    for (int i = 0; i < groupingSeparators.length; i++) {
      if (grouping.equals(groupingSeparators[i].charAt(0))) {
        comboBox.setSelectedIndex(i);
        break;
      }
    }

    // Add the listener at the end to avoid false events
    comboBox.setActionCommand(GROUPING_COMMAND);
    comboBox.addActionListener(listener);

    return comboBox;

  }

  /**
   * <p>Provide a choice between the local currency symbol, or its 3-letter code</p>
   *
   * @param listener             The action listener to alert when the selection is made
   * @param bitcoinConfiguration The Bitcoin configuration to use
   *
   * @return A new "local symbol" combo box (e.g. ["$", "USD"] or ["£","GBP"] etc)
   */
  public static JComboBox<String> newLocalSymbolComboBox(ActionListener listener, BitcoinConfiguration bitcoinConfiguration) {

    String[] localSymbols = new String[]{
      bitcoinConfiguration.getLocalCurrencySymbol(),
      bitcoinConfiguration.getLocalCurrencyUnit().getCurrencyCode(),
    };
    JComboBox<String> comboBox = newReadOnlyComboBox(localSymbols);

    // Ensure we have no ugly scrollbar
    comboBox.setMaximumRowCount(localSymbols.length);

    // Add the listener at the end to avoid false events
    comboBox.setActionCommand(LOCAL_SYMBOL_COMMAND);
    comboBox.addActionListener(listener);

    return comboBox;
  }

  /**
   * @param listener             The action listener to alert when the selection is made
   * @param bitcoinConfiguration The Bitcoin configuration to use
   *
   * @return A new "Bitcoin symbol" combo box (e.g. "mB", "XBT" etc)
   */
  public static JComboBox<BitcoinSymbol> newBitcoinSymbolComboBox(ActionListener listener, BitcoinConfiguration bitcoinConfiguration) {

    // Order of insertion is important here
    JComboBox<BitcoinSymbol> comboBox = newReadOnlyComboBox(BitcoinSymbol.values());

    comboBox.setEditable(false);

    // Ensure we have no ugly scrollbar
    comboBox.setMaximumRowCount(BitcoinSymbol.values().length);

    // Use a list cell renderer to ensure Bitcoin symbols are correctly presented
    ListCellRenderer<BitcoinSymbol> renderer = new BitcoinSymbolListCellRenderer();
    comboBox.setRenderer(renderer);

    // Ensure we start with the given symbol selected
    BitcoinSymbol bitcoinSymbol = BitcoinSymbol.of(bitcoinConfiguration.getBitcoinSymbol());
    comboBox.setSelectedIndex(bitcoinSymbol.ordinal());

    // Add the listener at the end to avoid false events
    comboBox.setActionCommand(BITCOIN_SYMBOL_COMMAND);
    comboBox.addActionListener(listener);

    return comboBox;
  }

  /**
   * @param listener The action listener to alert when the selection is made
   *
   * @return A new "placement" combo box (e.g. "Leading", "Trailing" etc)
   */
  public static JComboBox<String> newPlacementComboBox(ActionListener listener) {

    // Order of insertion is important here
    String[] positions = new String[]{
      Languages.safeText(MessageKey.LEADING),
      Languages.safeText(MessageKey.TRAILING),
    };
    JComboBox<String> comboBox = newReadOnlyComboBox(positions);

    // Add the listener at the end to avoid false events
    comboBox.setActionCommand(PLACEMENT_COMMAND);
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