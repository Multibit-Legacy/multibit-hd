package org.multibit.hd.ui.views.components;

import org.multibit.hd.ui.i18n.Languages;

import javax.swing.*;

/**
 * <p>Utility to provide the following to UI:</p>
 * <ul>
 * <li>Provision of localised combo boxes</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ComboBoxes {

  /**
   * Utilities have no public constructor
   */
  private ComboBoxes() {
  }

  /**
   * @return A new languages combo box
   */
  public static JComboBox<String> newLanguagesComboBox() {

    return new JComboBox<>(Languages.getLanguageNames(true));

  }

}
