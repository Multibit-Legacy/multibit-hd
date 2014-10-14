package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
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

    return null;

  }

  @Override
  public Color getBackgroundSelectionColor() {

    return null;

  }

  @Override
  public Color getBackground() {

    return null;
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
    final Color iconColor;
    if (sel) {
      iconColor = Themes.currentTheme.sidebarSelectedText();
    } else {
      iconColor = Themes.currentTheme.text();
    }
    ret.setForeground(iconColor);

    // No border selection
    setBorderSelectionColor(Themes.currentTheme.sidebarPanelBackground());

    // Iconography (not all icons are created with the same size)
    if (leaf) {
      switch (nodeInfo.getDetailScreen()) {

        case SEND_REQUEST:
          if (nodeInfo.getText().equals(Languages.safeText(MessageKey.SEND_OR_REQUEST))) {
            setIcon(AwesomeDecorator.createIcon(AwesomeIcon.EXCHANGE, iconColor, MultiBitUI.NORMAL_ICON_SIZE - 3));
            setIconTextGap(9);
          } else {
            // In "single mode" this is the home
            setIcon(AwesomeDecorator.createIcon(AwesomeIcon.HOME, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
            setIconTextGap(7);
          }
          break;
        case CONTACTS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.USER, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
          setIconTextGap(10);
          break;
        case TRANSACTIONS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.LIST, iconColor, MultiBitUI.NORMAL_ICON_SIZE - 2));
          setIconTextGap(8);
          break;
        case HELP:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.QUESTION, iconColor, MultiBitUI.NORMAL_ICON_SIZE + 10));
          setIconTextGap(9);
          break;
        case HISTORY:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.HISTORY, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
          setIconTextGap(9);
          break;
        case SETTINGS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.GEARS, iconColor, MultiBitUI.NORMAL_ICON_SIZE - 1));
          setIconTextGap(6);
          break;
        case TOOLS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.WRENCH, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
          setIconTextGap(6);
          break;
        case EXIT:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.SIGN_OUT, iconColor, MultiBitUI.NORMAL_ICON_SIZE + 2));
          setIconTextGap(6);
          break;
      }

    } else {

      setOpenIcon(
        AwesomeDecorator.createIcon(
          AwesomeIcon.CARET_DOWN,
          Themes.currentTheme.text(),
          MultiBitUI.NORMAL_ICON_SIZE)
      );

      setClosedIcon(
        AwesomeDecorator.createIcon(
          AwesomeDecorator.select(AwesomeIcon.CARET_RIGHT, AwesomeIcon.CARET_LEFT),
          Themes.currentTheme.text(),
          MultiBitUI.NORMAL_ICON_SIZE)
      );

    }

    return ret;
  }


}
