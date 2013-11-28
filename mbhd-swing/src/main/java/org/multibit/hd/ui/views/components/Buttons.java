package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.views.fonts.AwesomeDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.i18n.Languages;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised buttons</li>
 * </ul>
 *new JComboBox<>(Languages.getLanguageNames(resourceBundle, true))
 * @since 0.0.1
 *         
 */
public class Buttons {

  private static final String APPLY_BUTTON = "showPreferencesSubmitAction.text";
  private static final String UNDO_BUTTON = "undoPreferencesChangesSubmitAction.text";

  /**
   * Utilities have no public constructor
   */
  private Buttons() {
  }

  /**
   * @return A new "Apply" button with icon
   */
  public static JButton newApplyButton() {

    ResourceBundle rb = Languages.currentResourceBundle();

    return AwesomeDecorator.createIconButton(
      AwesomeIcon.ARROW_RIGHT,
      rb.getString(APPLY_BUTTON)
    );

  }

  /**
   * @return A new "Undo" button with icon
   */
  public static JButton newUndoButton() {

    ResourceBundle rb = Languages.currentResourceBundle();

    return AwesomeDecorator.createIconButton(
      AwesomeIcon.UNDO,
      rb.getString(UNDO_BUTTON)
    );

  }

}
