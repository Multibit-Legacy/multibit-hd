package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Render a RAGStatus as an icon
 */
public class RAGStatusRenderer extends DefaultTableCellRenderer {

  JLabel statusLabel = new JLabel();

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int column) {

    // Prepare the primary icon (used always), and an extra icon and containing panel for use as required.
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    statusLabel.setVerticalAlignment(SwingConstants.CENTER);
    statusLabel.setOpaque(true);

    AwesomeDecorator.bindIcon(AwesomeIcon.CIRCLE, statusLabel, false, MultiBitUI.SMALL_ICON_SIZE);

    // Get the RAG (which is in the model as a RAGStatus
    if (value instanceof RAGStatus) {
      RAGStatus status = (RAGStatus) value;

      switch (status) {
        case RED:
          statusLabel.setForeground(Themes.currentTheme.dangerAlertBackground());
          break;
        case AMBER:
          statusLabel.setForeground(Themes.currentTheme.warningAlertBackground());
          break;
        case GREEN:
          statusLabel.setForeground(Themes.currentTheme.successAlertBackground());
          break;
        default:
          // Unknown status
          throw new IllegalStateException("Unknown status " + status);
      }
    }

    return statusLabel;
  }
}
