package org.multibit.hd.ui.views.components;

import javax.swing.*;

/**
 * <p>Decorator to provide the following to panels:</p>
 * <ul>
 * <li>Application of various themed styles to panels</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ComponentDecorator {


  /**
   * Utilities have a private constructor
   */
  private ComponentDecorator() {
  }

  /**
   * <p>Adjust the component border to give a flat appearance</p>
   * <p>ComboBoxes are not affected by this change due to their construction</p>
   *
   * @param component The component to be themed
   */
  public static void applyFlatAppearance(JComponent component) {

//    Border line = new LineBorder(Themes.currentTheme.infoAlertBorder());
//    Border margin = new EmptyBorder(5, 15, 5, 15);
//    Border compound = new CompoundBorder(line,margin);
//    component.setBorder(margin);

//    if (component instanceof AbstractButton) {
//
//      AbstractButton button = (AbstractButton) component;
//
//      button.setBorderPainted(true);
//      button.setFocusPainted(true);
//      button.setContentAreaFilled(true);
//    }
//
  }

}