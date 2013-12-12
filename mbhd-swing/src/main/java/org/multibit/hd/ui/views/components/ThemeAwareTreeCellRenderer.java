package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *         
 */
public class ThemeAwareTreeCellRenderer extends DefaultTreeCellRenderer {

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
    final Component ret = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

    /*
    TODO Use this later
    DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        BookInfo nodeInfo =
                (BookInfo)(node.getUserObject());
        String title = nodeInfo.bookName;

     */

    setText(value.toString());
    setForeground(Themes.currentTheme.text());

    if (leaf) {
      switch (node.toString()) {

        case "Contacts":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.USER));
          break;
        case "Transactions":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.LIST));
          break;
        case "Help":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.QUESTION));
          break;
        case "History":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.ARCHIVE));
          break;
        case "Preferences":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.GEARS));
          break;
        case "Tools":
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.WRENCH));
          break;
      }
    } else {

      setOpenIcon(AwesomeDecorator.createIcon(AwesomeIcon.CARET_DOWN));
      setClosedIcon(
        AwesomeDecorator.createIcon(
          AwesomeDecorator.select(
            AwesomeIcon.CARET_RIGHT,
            AwesomeIcon.CARET_LEFT)
        ));

    }

    return ret;
  }


}
