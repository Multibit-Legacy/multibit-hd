package org.multibit.hd.ui.views.components.renderers;

import com.google.common.collect.Lists;
import org.multibit.hd.core.dto.WalletData;
import org.multibit.hd.ui.languages.Languages;

import javax.swing.*;
import java.awt.*;

/**
 * <p>List cell renderer to provide the following to combo boxes:</p>
 * <ul>
 * <li>Rendering of a wallet data DTO</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class WalletDataListCellRenderer extends JLabel implements ListCellRenderer<WalletData> {

  public WalletDataListCellRenderer() {

    setOpaque(true);
    setVerticalAlignment(CENTER);

  }

  public Component getListCellRendererComponent(
    JList list,
    WalletData value,
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

      // Create a truncated name + description
      String truncatedText = Languages.truncatedList(Lists.newArrayList(value.getName(), value.getDescription()), 100);

      setText(truncatedText);
    } else {
      setText("");
    }
    return this;
  }

}