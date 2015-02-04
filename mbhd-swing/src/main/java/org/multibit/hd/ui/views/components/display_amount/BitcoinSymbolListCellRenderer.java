package org.multibit.hd.ui.views.components.display_amount;

import org.multibit.hd.core.config.BitcoinConfiguration;
import org.multibit.hd.core.utils.BitcoinSymbol;
import org.multibit.hd.ui.views.components.LabelDecorator;

import javax.swing.*;
import java.awt.*;

/**
 * <p>List cell renderer to provide the following to combo boxes:</p>
 * <ul>
 * <li>Rendering of a contact thumbnail</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class BitcoinSymbolListCellRenderer extends JLabel implements ListCellRenderer<BitcoinSymbol> {

  /**
   * A default Bitcoin configuration to support list cell rendering
   */
  private final BitcoinConfiguration bitcoinConfiguration = new BitcoinConfiguration();

  public BitcoinSymbolListCellRenderer() {

    // Must be opaque to ensure background color is shown
    setOpaque(true);

    setVerticalAlignment(CENTER);

  }

  public Component getListCellRendererComponent(
    JList list,
    BitcoinSymbol value,
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

    // No leading text required
    bitcoinConfiguration.setBitcoinSymbol(value.name());
    LabelDecorator.applyBitcoinSymbolLabel(this, bitcoinConfiguration, "");

    // Ensure we maintain the appearance
    setFont(list.getFont());

    return this;
  }

  @Override
  public String getName() {
    return "List.cellRenderer";
  }

}