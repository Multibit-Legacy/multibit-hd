package org.multibit.hd.core.api.seed_phrase;

/**
 * <p>Enum to provide the following to Core API:</p>
 * <ul>
 * <li>Information about the Bitcoin network status</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum SeedPhraseSize {

  /**
   * 12 words
   */
  TWELVE_WORDS(12),

  /**
   * 18 words
   */
  EIGHTEEN_WORDS(18),

  /**
   * 24 words
   */
  TWENTY_FOUR_WORDS(24),

  // End of enum
  ;

  private final int size;

  /**
   * @param size The seed phrase size in words
   */
  SeedPhraseSize(int size) {
    this.size = size;
  }

  /**
   * @return The seed phrase size in words
   */
  public int getSize() {
    return size;
  }
}
