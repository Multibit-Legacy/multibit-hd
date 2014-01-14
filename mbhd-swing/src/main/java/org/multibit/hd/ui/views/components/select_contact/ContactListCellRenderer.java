package org.multibit.hd.ui.views.components.select_contact;

import org.multibit.hd.core.api.Contact;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;
import java.awt.*;

/**
 * <p>List cell renderer to provide the following to combo boxes:</p>
 * <ul>
 * <li>Rendering of a contact</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ContactListCellRenderer extends JLabel implements ListCellRenderer<Contact> {

  public ContactListCellRenderer() {

    setOpaque(true);
    setVerticalAlignment(CENTER);

  }

  public Component getListCellRendererComponent(
    JList list,
    Contact value,
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

    // Apply the icon
    // TODO Link into the microthumbnail images
    AwesomeDecorator.applyIcon(
      AwesomeIcon.USER,
      this,
      true,
      AwesomeDecorator.NORMAL_ICON_SIZE
    );
    setText(value.getName());

    setFont(list.getFont());

    return this;
  }
}