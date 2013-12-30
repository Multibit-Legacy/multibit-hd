package org.multibit.hd.ui.views.components.seed_phrase_display;

import com.google.common.base.Strings;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseSize;
import org.multibit.hd.ui.models.Model;

import java.util.Arrays;

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
  private char[] seedPhrase;
  private boolean asClearText = false;

  public SeedPhraseDisplayModel(SeedPhraseGenerator generator) {
    this.generator = generator;
    newSeedPhrase();
  }

  /**
   * Zero out the seed phrase before closing
   */
  public void dispose() {
    Arrays.fill(seedPhrase, '0');
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
      return String.valueOf(seedPhrase);
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
