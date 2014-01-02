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

    SidebarNodeInfo nodeInfo = (SidebarNodeInfo) node.getUserObject();

    // Content
    ret.setText(nodeInfo.getText());

    // Theme
    ret.setBorder(border);
    ret.setForeground(Themes.currentTheme.text());

    // Iconography
    if (leaf) {
      switch (nodeInfo.getDetailScreen()) {

        case CONTACTS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.USER, Color.BLACK, 20));
          break;
        case TRANSACTIONS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.LIST, Color.BLACK, 18));
          break;
        case HELP:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.QUESTION, Color.BLACK, 20));
          break;
        case HISTORY:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.ARCHIVE, Color.BLACK, 20));
          break;
        case PREFERENCES:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.GEARS, Color.BLACK, 20));
          break;
        case TOOLS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.WRENCH, Color.BLACK, 20));
          break;
        case EXIT:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.SIGN_OUT, Color.BLACK, 20));
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
