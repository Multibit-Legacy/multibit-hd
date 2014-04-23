package org.multibit.hd.ui.views.components.text_fields;

import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.themes.Themes;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 * <p>Text field to provide the following to UI:</p>
 * <ul>
 * <li>Accepts Base58 characters</li>
 * <li>Limits number of characters</li>
 * <li>Handles correct formatting</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class FormattedBitcoinAddressField extends JFormattedTextField {

  public FormattedBitcoinAddressField(boolean readOnly) {

    super(34);

    setInputVerifier(new ThemeAwareBitcoinAddressInputVerifier());

    setEditable(!readOnly);

    setOpaque(false);

    if (readOnly) {
      setBorder(new TextBubbleBorder(Themes.currentTheme.readOnlyBorder()));
      setBackground(Themes.currentTheme.readOnlyBackground());
    } else {
      setBorder(new TextBubbleBorder(Themes.currentTheme.dataEntryBorder()));
      setBackground(Themes.currentTheme.dataEntryBackground());
    }

    // Build string formatter
    DefaultFormatter defaultFormatter = new DefaultFormatter();
    defaultFormatter.setOverwriteMode(false);
    defaultFormatter.setValueClass(String.class);

    setFormatterFactory(new DefaultFormatterFactory(defaultFormatter));

  }


}
