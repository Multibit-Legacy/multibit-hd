package org.multibit.hd.core.api.seed_phrase;

/**
 * <p>Seed phrase generator to provide the following to Core API:</p>
 * <ul>
 * <li>Implementation of BIP0039 seed phrase generation</li>
 * </ul>
 * TODO Requires proper implementation
 *
 * @since 0.0.1
 *  
 */
public class DefaultSeedPhraseGenerator implements SeedPhraseGenerator {

  private SeedPhraseSize size = SeedPhraseSize.TWELVE_WORDS;
  private int count = 0;

  private final String[] mnemonics = new String[]{
    // 12 words
    "abandon ability able about above absent\n" +
      "absorb abstract absurd abuse access accident",
    // 18 words
    "abandon ability able about above absent\n" +
      "absorb abstract absurd abuse access accident\n" +
      "account accuse achieve acid acoustic acquire",
    // 24 words
    "abandon ability able about above absent\n" +
      "absorb abstract absurd abuse access accident\n" +
      "account accuse achieve acid acoustic acquire" +
      "across act action actor actress actual"
  };

  @Override
  public char[] newSeedPhrase() {
    count++;
    return (count + mnemonics[size.ordinal()]).toCharArray();
  }

  @Override
  public char[] newSeedPhrase(SeedPhraseSize size) {

    this.size = size;

    return newSeedPhrase();
  }
}
