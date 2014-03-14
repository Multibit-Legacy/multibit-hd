package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.core.dto.PaymentRequestData;

import javax.swing.*;
import java.awt.*;

/**
 * <p>List cell renderer to provide the following to combo boxes:</p>
 * <ul>
 * <li>Rendering of paymentRequestData</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class PaymentRequestDataListCellRenderer extends JLabel implements ListCellRenderer<PaymentRequestData> {

  public PaymentRequestDataListCellRenderer() {

    setOpaque(true);
    setVerticalAlignment(CENTER);
  }

  public Component getListCellRendererComponent(
    JList list,
    PaymentRequestData value,
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

      setText(value.getDescription());

    } else {
      // No value/ not a paymentRequestData  means no text or icon
      setIcon(null);
      setText("");
    }

    return this;
  }

}