package org.multibit.hd.ui.views.components.enter_seed_phrase;

import com.google.common.base.Strings;
import org.multibit.hd.ui.models.Model;
import org.multibit.hd.ui.views.components.TextBoxes;

import java.util.List;

/**
 * <p>Model to provide the following to view:</p>
 * <ul>
 * <li>Show/hide the seed phrase (initially hidden)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class EnterSeedPhraseModel implements Model {

  private List<String> seedPhrase;

  // Start with the text displayed
  private boolean asClearText = true;

  /**
   * @return The seed phrase in either clear or obscured text
   */
  public String displaySeedPhrase() {
    if (asClearText) {
      StringBuilder buffer = new StringBuilder();
      int count = 0;
      for (String word : seedPhrase) {
        buffer.append(word);
        count++;
        if (count != seedPhrase.size()) {
          buffer.append(" ");
        }
      }
      return buffer.toString();
    } else {
      return Strings.repeat(String.valueOf(TextBoxes.getPasswordEchoChar()), 200);
    }
  }

  /**
   * @param asClearText True if the seed phrase should be presented as clear text
   */
  public void setAsClearText(boolean asClearText) {
    this.asClearText = asClearText;
  }

  /**
   * @return The current state of the clear text display
   */
  public boolean asClearText() {
    return asClearText;
  }

}
