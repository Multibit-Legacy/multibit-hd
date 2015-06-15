package org.multibit.hd.ui.views.components.select_recipient;

import org.multibit.hd.core.dto.Recipient;
import org.multibit.hd.ui.utils.HtmlUtils;
import org.multibit.hd.ui.views.themes.Themes;

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

  private final JTextField textField;

  public RecipientListCellRenderer(JTextField textField) {

    this.textField = textField;

    // Must be opaque to ensure background color is shown
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

    // Ensure the popup list retains a nice border
    list.setBorder(BorderFactory.createLineBorder(Themes.currentTheme.dataEntryBorder()));

    if (isSelected) {
      setBackground(Themes.currentTheme.tableRowSelectedBackground());
      setForeground(Themes.currentTheme.inverseText());
    } else {
      setBackground(Themes.currentTheme.dataEntryBackground());
      setForeground(Themes.currentTheme.text());
    }

    String fragment = textField.getText();
    String sourceText;
    if (value.getContact().isPresent()) {
      sourceText = value.getContact().get().getName();
    } else {
      sourceText = value.getBitcoinAddress().toString();
    }

    // Embolden the matching fragments
    setText(HtmlUtils.applyBoldFragments(fragment, sourceText));

    // Ensure we maintain the appearance
    setFont(list.getFont());

    return this;
  }

  @Override public String getName() {
    return "List.cellRenderer";
  }


}