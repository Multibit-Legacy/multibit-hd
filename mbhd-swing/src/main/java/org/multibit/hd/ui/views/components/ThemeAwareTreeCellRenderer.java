package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.MultiBitUI;
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
 */
public class ThemeAwareTreeCellRenderer extends DefaultTreeCellRenderer {

  // Provide padding for rows
  private Border borderTightTop = BorderFactory.createEmptyBorder(0, 0, 5, 0);
  private Border borderNormal = BorderFactory.createEmptyBorder(5, 0, 5, 0);
  private Border borderPaddedTop = BorderFactory.createEmptyBorder(25, 0, 5, 0);

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
    ret.setBorder(borderNormal);
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

        case BUY_SELL:
          // Shopping basket indicates buying and selling
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.CREDIT_CARD, iconColor, MultiBitUI.NORMAL_ICON_SIZE-3));
          setIconTextGap(8);
          ret.setBorder(borderNormal);
          break;
        case SEND_REQUEST:
          // Exchange icon represents person to person exchange associated with direct transactions
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.EXCHANGE, iconColor, MultiBitUI.NORMAL_ICON_SIZE - 3));
          setIconTextGap(9);
          ret.setBorder(borderNormal);
          break;
        case TRANSACTIONS:
          // List indicates a spreadsheet or balance sheet
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.LIST, iconColor, MultiBitUI.NORMAL_ICON_SIZE - 2));
          setIconTextGap(8);
          ret.setBorder(borderNormal);
          break;
        case CONTACTS:
          // Standard contact icon
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.USER, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
          setIconTextGap(10);
          ret.setBorder(borderPaddedTop);
          break;
        case HELP:
          // Question mark leading to answers
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.QUESTION, iconColor, MultiBitUI.NORMAL_ICON_SIZE+2));
          setIconTextGap(13);
          ret.setBorder(borderTightTop);
          break;
        case SETTINGS:
          // Standard settings icon
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.GEARS, iconColor, MultiBitUI.NORMAL_ICON_SIZE - 1));
          setIconTextGap(6);
          ret.setBorder(borderPaddedTop);
          break;
        case MANAGE_WALLET:
          // Edit indicates changing text information
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.EDIT, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
          setIconTextGap(6);
          ret.setBorder(borderNormal);
          break;
        case TOOLS:
          // Tools indicates utilities to get stuff done
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.WRENCH, iconColor, MultiBitUI.NORMAL_ICON_SIZE));
          setIconTextGap(6);
          ret.setBorder(borderNormal);
          break;
        case EXIT:
          // Exit icon
          setIcon(AwesomeDecorator.createIcon(AwesomeIcon.SIGN_OUT, iconColor, MultiBitUI.NORMAL_ICON_SIZE + 2));
          setIconTextGap(6);
          ret.setBorder(borderPaddedTop);
          break;
        default:
          throw new IllegalStateException("Unexpected screen:" + nodeInfo.getDetailScreen());
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
