package org.multibit.hd.ui.views.components.text_fields;

import com.google.bitcoin.core.NetworkParameters;
import com.google.common.base.Preconditions;
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

  /**
   * @param networkParameters The network parameters
   * @param readOnly          True if the field is read only
   */
  public FormattedBitcoinAddressField(NetworkParameters networkParameters, boolean readOnly) {

    super(34);

    Preconditions.checkNotNull(networkParameters, "'networkParameters' must be present");

    setInputVerifier(new ThemeAwareBitcoinAddressInputVerifier(networkParameters));

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
