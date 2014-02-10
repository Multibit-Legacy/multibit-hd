package org.multibit.hd.ui.views.components.select_contact;

import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.ui.utils.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class RecipientListCellRenderer extends JLabel implements ListCellRenderer<Recipient> {

  private static final Logger log = LoggerFactory.getLogger(RecipientListCellRenderer.class);

  private final JTextField textField;

  public RecipientListCellRenderer(JTextField textField) {

    this.textField = textField;

    setOpaque(true);
    setVerticalAlignment(CENTER);

  }

  public Component getListCellRendererComponent(
    JList list,
    Recipient value,
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

    String fragment = textField.getText();
    log.debug("Recipient fragment: '{}'",fragment);
    String sourceText;
    if (value.getContact().isPresent()) {
      sourceText = value.getContact().get().getName();
    } else {
      sourceText = value.getBitcoinAddress();
    }
    log.debug("Recipient source text: '{}'",sourceText);

    // Embolden the matching fragments
    setText(HtmlUtils.applyBoldFragments(fragment, sourceText));

    // Ensure we maintain the appearance
    setFont(list.getFont());

    return this;
  }

}