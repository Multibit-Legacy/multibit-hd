package org.multibit.hd.ui.views.components.renderers;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * <p>List cell renderer to provide the following to lists:</p>
 * <ul>
 * <li>Rendering of a tag pill with delete button</li>
 * <li>Theme integration based on read-only colours</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class TagPillListCellRenderer extends DefaultListCellRenderer {

  public TagPillListCellRenderer() {

    // Ensure consistent color
    setBackground(Themes.currentTheme.readOnlyBackground());
    setForeground(Themes.currentTheme.buttonText());

    // Place close icon on right to ensure consistent rendering
    AwesomeDecorator.applyIcon(
      AwesomeIcon.TIMES,
      this,
      true,
      MultiBitUI.SMALL_ICON_SIZE
    );

  }

  @Override
  public Component getListCellRendererComponent(
    JList list,
    Object value,
    int index,
    boolean isSelected,
    boolean cellHasFocus
  ) {

    setText(value.toString());

    setComponentOrientation(Languages.currentComponentOrientation());

    setEnabled(list.isEnabled());
    setFont(list.getFont());

    Border border = new TextBubbleBorder(Themes.currentTheme.readOnlyBorder());
    if (cellHasFocus) {
      if (isSelected) {
        border = new TextBubbleBorder(UIManager.getColor("nimbusFocus"));
      }
    }

    setBorder(border);

    return this;
  }
}