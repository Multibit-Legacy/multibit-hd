package org.multibit.hd.ui.views.components.renderers;

import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.WalletSummary;
import org.multibit.hd.ui.languages.Languages;

import javax.swing.*;
import java.awt.*;

/**
 * <p>List cell renderer to provide the following to combo boxes:</p>
 * <ul>
 * <li>Rendering of a wallet summary DTO</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WalletSummaryListCellRenderer extends JLabel implements ListCellRenderer<WalletSummary> {

  public WalletSummaryListCellRenderer() {

    setOpaque(true);
    setVerticalAlignment(CENTER);
    setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

  }

  public Component getListCellRendererComponent(
    JList list,
    WalletSummary value,
    int index,
    boolean isSelected,
    boolean cellHasFocus
  ) {

    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }

    if (value != null) {

      // Create a truncated name (description is shown outside the list)
      String truncatedText = Languages.truncatedList(Lists.newArrayList(value.getName()), 100);

      setText(truncatedText);
    } else {
      setText("");
    }
    return this;
  }

}