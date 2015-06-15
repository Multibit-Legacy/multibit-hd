package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.core.dto.BackupSummary;
import org.multibit.hd.core.utils.Dates;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import java.awt.*;

/**
 * <p>List cell renderer to provide the following to combo boxes:</p>
 * <ul>
 * <li>Rendering of a backup summary</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class BackupSummaryListCellRenderer extends JLabel implements ListCellRenderer<BackupSummary> {

  public BackupSummaryListCellRenderer() {

    setOpaque(true);
    setVerticalAlignment(CENTER);

  }

  public Component getListCellRendererComponent(
    JList list,
    BackupSummary value,
    int index,
    boolean isSelected,
    boolean cellHasFocus
  ) {

    if (isSelected) {
      setBackground(Themes.currentTheme.tableRowSelectedBackground());
      setForeground(Themes.currentTheme.inverseText());
    } else {
      setBackground(Themes.currentTheme.dataEntryBackground());
      setForeground(Themes.currentTheme.text());
    }

    if (value != null) {
      // If the date is available use that, otherwise use the name
      if (value.getCreated() != null) {
        // Display in the system timezone
        setText(Dates.formatHttpDateHeaderLocal(value.getCreated()));
      } else {
        setText(value.getName());
      }
    } else {
      setText("");
    }
    return this;
  }

}