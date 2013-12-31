package org.multibit.hd.ui.views.components.seed_phrase_display;

import com.google.common.base.Strings;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseSize;
import org.multibit.hd.ui.models.Model;

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
public class SeedPhraseDisplayModel implements Model {

  private final SeedPhraseGenerator generator;
  private List<String> seedPhrase;
  private boolean asClearText = true;

  public SeedPhraseDisplayModel(SeedPhraseGenerator generator) {
    this.generator = generator;
    newSeedPhrase();
  }

  /**
   * <p>Generates a new seed phrase based on a new size</p>
   *
   * @param size The new size for subsequent seed phrases
   */
  public void newSeedPhrase(SeedPhraseSize size) {
    this.seedPhrase = generator.newSeedPhrase(size);
  }

  /**
   * <p>Generates a new seed phrase</p>
   */
  public void newSeedPhrase() {
    this.seedPhrase = generator.newSeedPhrase();
  }

  /**
   * @return The seed phrase in either clear or obscured text
   */
  public String displaySeedPhrase() {
    if (asClearText) {
      StringBuffer buffer = new StringBuffer();
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
      return Strings.repeat("*", 200);
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
