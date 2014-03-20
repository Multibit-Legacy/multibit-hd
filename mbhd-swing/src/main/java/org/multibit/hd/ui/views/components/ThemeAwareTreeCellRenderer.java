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
    //return Themes.currentTheme.sidebarPanelBackground();
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
    Color iconColor = Themes.currentTheme.text();
    ret.setForeground(iconColor);

    // Minimalist focus
    setBorderSelectionColor(UIManager.getColor("nimbusFocus"));

    // Iconography (not all icons are created with the same size)
    if (leaf) {
      switch (nodeInfo.getDetailScreen()) {

        case WALLET:
          if (nodeInfo.getText().equals(Languages.safeText(MessageKey.SEND_OR_REQUEST))) {
            setIcon(AwesomeDecorator.createIcon(AwesomeIcon.EXCHANGE, iconColor, MultiBitUI.NORMAL_ICON_SIZE - 3));
          }
          break;
        case CONTACTS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.USER, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
          break;
        case TRANSACTIONS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.LIST, iconColor, MultiBitUI.NORMAL_ICON_SIZE - 2));
          break;
        case HELP:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.QUESTION, iconColor, MultiBitUI.NORMAL_ICON_SIZE + 15));
          break;
        case HISTORY:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.ARCHIVE, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
          break;
        case SETTINGS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.GEARS, iconColor, MultiBitUI.NORMAL_ICON_SIZE - 1));
          break;
        case TOOLS:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.WRENCH, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
          break;
        case EXIT:
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.SIGN_OUT, iconColor, MultiBitUI.NORMAL_ICON_SIZE + 2));
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
          AwesomeDecorator.select(AwesomeIcon.CARET_RIGHT,AwesomeIcon.CARET_LEFT),
          Themes.currentTheme.text(),
          MultiBitUI.NORMAL_ICON_SIZE));

    }

    return ret;
  }


}
