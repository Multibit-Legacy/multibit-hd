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

  /**
    * @return The size of the entropy bytes that correspond to this phrase size.
    * (4 bytes of entropy are encoded into 3 words)
    */
   public int getEntropyBytesSize() {
     return 4 * size / 3;
   }

   /**
   * @param ordinal The ordinal (zero-based)
   *
   * @return A matching SeedPhraseSize
   */
  public static SeedPhraseSize fromOrdinal(int ordinal) {

    switch (ordinal) {
      case 0:
        return TWELVE_WORDS;
      case 1:
        return EIGHTEEN_WORDS;
      case 2:
        return TWENTY_FOUR_WORDS;
      default:
        throw new IllegalArgumentException("Unknown index: " + ordinal);
    }

  }
}
