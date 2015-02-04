package org.multibit.hd.core.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.io.Serializable;

/**
 * <p>Document filter to provide the following to text fields/areas:</p>
 * <ul>
 * <li>Limiting input to a given maximum length</li>
 * </ul>

 * @since 0.0.1
 *
 */
public class DocumentMaxLengthFilter extends DocumentFilter implements Serializable {

  private final int maxCharacters;

  public DocumentMaxLengthFilter(int maxChars) {
    maxCharacters = maxChars;
  }

  public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {

    // Reject if the insertion would be too long
    if ((fb.getDocument().getLength() + str.length()) <= maxCharacters) {
      super.insertString(fb, offs, str, a);
    }
  }

  public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {

    // Reject if the replacement would be too long
    if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters) {
      super.replace(fb, offs, length, str, a);
    }
  }

}