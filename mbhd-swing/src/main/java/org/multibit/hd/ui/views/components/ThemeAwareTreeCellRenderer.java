package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * <p>Tree cell renderer to provide the following to sidebar tree:</p>
 * <ul>
 * <li>Adding appropriate spacing across platforms</li>
 * <li>Font Awesome iconography</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ThemeAwareTreeCellRenderer extends DefaultTreeCellRenderer {

  // Provide padding for rows
  private Border border = BorderFactory.createEmptyBorder(5, 0, 5, 0);

  @Override
  public Color getBackgroundNonSelectionColor() {
    return (null);
  }

  @Override
  public Color getBackgroundSelectionColor() {
    return Themes.currentTheme.sidebarPanelBackground();
  }

  @Override
  public Color getBackground() {
    return (null);
  }

  @Override
  public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {

    final JLabel ret = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

    /*
    TODO Use this later
    DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        BookInfo nodeInfo =
                (BookInfo)(node.getUserObject());
        String title = nodeInfo.bookName;

     */

    ret.setBorder(border);

    ret.setText(value.toString());
    ret.setForeground(Themes.currentTheme.text());

    if (leaf) {
      switch (node.toString()) {

        case "Contacts":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.USER, Color.BLACK, 20));
          break;
        case "Transactions":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.LIST, Color.BLACK, 18));
          break;
        case "Help":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.QUESTION, Color.BLACK, 20));
          break;
        case "History":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.ARCHIVE, Color.BLACK, 20));
          break;
        case "Preferences":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.GEARS, Color.BLACK, 20));
          break;
        case "Tools":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.WRENCH, Color.BLACK, 20));
          break;
      }
    } else {

      setOpenIcon(AwesomeDecorator.createIcon(AwesomeIcon.CARET_DOWN, Color.BLACK, 20));
      setClosedIcon(
        AwesomeDecorator.createIcon(
          AwesomeDecorator.select(
            AwesomeIcon.CARET_RIGHT,
            AwesomeIcon.CARET_LEFT),
          Color.BLACK, 20));

    }

    return ret;
  }


}
