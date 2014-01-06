package org.multibit.hd.ui.views.components.display_seed_phrase;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.core.api.seed_phrase.SeedPhraseSize;
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
public class DisplaySeedPhraseModel implements Model<List<String>> {

  private final SeedPhraseGenerator generator;
  private List<String> seedPhrase= Lists.newArrayList();
  private SeedPhraseSize currentSeedSize;

  // Start with the text displayed
  private boolean asClearText = true;

  public DisplaySeedPhraseModel(SeedPhraseGenerator generator) {
    this.generator = generator;

    // Default to twelve word seed
    currentSeedSize = SeedPhraseSize.TWELVE_WORDS;
    newSeedPhrase(currentSeedSize);
  }

  /**
   * <p>Generates a new seed phrase based on a new size</p>
   *
   * @param size The new size for subsequent seed phrases
   */
  public void newSeedPhrase(SeedPhraseSize size) {
    currentSeedSize = size;
    this.seedPhrase = generator.newSeedPhrase(size);
  }

  /**
   * @return The seed phrase in either clear or obscured text
   */
  public String displaySeedPhrase() {
    if (asClearText) {
      return Joiner.on(" ").join(seedPhrase);
    } else {
      return Strings.repeat(String.valueOf(TextBoxes.getPasswordEchoChar()), TextBoxes.PASSWORD_AREA);
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

  /**
   * @return The current seed size
   */
  public SeedPhraseSize getCurrentSeedSize() {
    return currentSeedSize;
  }

  @Override
  public List<String> getValue() {
    return seedPhrase;
  }

  @Override
  public void setValue(List<String> value) {
    this.seedPhrase = value;
  }
}
