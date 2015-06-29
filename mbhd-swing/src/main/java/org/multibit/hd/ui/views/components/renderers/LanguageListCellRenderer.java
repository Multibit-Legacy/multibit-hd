package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.ui.languages.LanguageKey;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;

/**
 * <p>List cell renderer to provide the following to combo boxes:</p>
 * <ul>
 * <li>Rendering of language+region+variant based on code</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class LanguageListCellRenderer extends JLabel implements ListCellRenderer<String> {

  public LanguageListCellRenderer() {

    setOpaque(true);
    setVerticalAlignment(CENTER);

  }

  public Component getListCellRendererComponent(
    JList list,
    String value,
    int index,
    boolean isSelected,
    boolean cellHasFocus
  ) {

    if (isSelected) {
      setBackground(Themes.currentTheme.tableRowSelectedBackground());
      setForeground(Themes.currentTheme.inverseText());
    } else {
      setBackground(Themes.currentTheme.dataEntryBackground());
      setForeground(Themes.currentTheme.dataEntryText());
    }

    if (value != null) {

      final LanguageKey languageKey;
      if (index >= 0) {
        // The languages are presented in the order they are declared
        languageKey = LanguageKey.values()[index];
      } else {
        // Need to work out the key from the value
        languageKey = LanguageKey.fromLanguageName(value);
      }

      // Need the language key to locate the icon
      setIcon(languageKey.getIcon());
      setText(languageKey.getLanguageName());

      // Ensure we can access the contents
      getAccessibleContext().setAccessibleName(languageKey.getLanguageName());

    } else {
      // No value means no text or icon
      setIcon(null);
      setText("");
    }

    return this;
  }

}