package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;

/**
 * <p>List cell renderer to provide the following to combo boxes:</p>
 * <ul>
 * <li>Consistent rendering of list cells</li>
 * </ul>
 *
 * @since 0.1.1
 *
 */
public class ThemeAwareListCellRenderer<T> extends JLabel implements ListCellRenderer<T> {

  public ThemeAwareListCellRenderer() {
    super();
    setOpaque(true);
  }

  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    if (isSelected) {
      setBackground(Themes.currentTheme.tableRowSelectedBackground());
      setForeground(Themes.currentTheme.inverseText());
    } else {
      setBackground(Themes.currentTheme.dataEntryBackground());
      setForeground(Themes.currentTheme.dataEntryText());
    }

    if (value != null) {

      // Need the language key to locate the icon
      setText(value.toString());

    } else {
      // No value means no text or icon
      setIcon(null);
      setText("");
    }

    return this;
  }
}
