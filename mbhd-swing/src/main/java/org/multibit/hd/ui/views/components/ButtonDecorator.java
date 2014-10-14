package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;

import javax.swing.*;

/**
 * <p>Decorator to provide the following to UI:</p>
 * <ul>
 * <li>Various button effects</li>
 * <li>Consistent iconography and accessibility</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ButtonDecorator {

  /**
   * Utilities have no public constructor
   */
  private ButtonDecorator() {
  }

  /**
   * <p>Decorate the button so that clicking will cause a "show"</p>
   * <p>The icon reflects the current state to make it more intuitive</p>
   *
   * @param button The button
   */
  public static void applyShow(JButton button) {

    // #53 Do not use an eye for reveal
    AwesomeDecorator.applyIcon(
      AwesomeIcon.LOCK,
      button,
      true,
      MultiBitUI.NORMAL_ICON_SIZE
    );

    AccessibilityDecorator.apply(button, MessageKey.SHOW, MessageKey.SHOW);

  }

  /**
   * <p>Decorate the button so that clicking will cause a "hide"</p>
   * <p>The icon reflects the current state to make it more intuitive</p>
   *
   * @param button The button
   */
  public static void applyHide(JButton button) {

    // #53 Do not use an eye for reveal
    AwesomeDecorator.applyIcon(
      AwesomeIcon.UNLOCK_ALT,
      button,
      true,
      MultiBitUI.NORMAL_ICON_SIZE
    );

    AccessibilityDecorator.apply(button, MessageKey.HIDE, MessageKey.HIDE);

  }

}
