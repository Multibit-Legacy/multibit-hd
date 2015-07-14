package org.multibit.hd.core.seed_phrase;

import org.multibit.hd.brit.core.exceptions.SeedPhraseException;
import org.multibit.hd.brit.core.seed_phrase.SeedPhraseGenerator;
import org.multibit.hd.brit.core.seed_phrase.SeedPhraseSize;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Seed phrase generator to provide the following to tests:</p>
 * <ul>
 * <li>Mock implementation of BIP0039 seed phrase generation</li>
 * </ul>
 *
 * @since 0.0.1
 *
 */
public class MockSeedPhraseGenerator implements SeedPhraseGenerator {

  private final String[] mnemonics = new String[]{
    // 12 words
    "abandon ability able about above absent " +
      "absorb abstract absurd abuse access accident",
    // 18 words
    "abandon ability able about above absent " +
      "absorb abstract absurd abuse access accident " +
      "account accuse achieve acid acoustic acquire",
    // 24 words
    "abandon ability able about above absent " +
      "absorb abstract absurd abuse access accident " +
      "account accuse achieve acid acoustic acquire " +
      "across act action actor actress actual"
  };

  @Override
  public List<String> newSeedPhrase() {
    return newSeedPhrase(SeedPhraseSize.TWELVE_WORDS);
  }

  @Override
  public List<String> newSeedPhrase(SeedPhraseSize size) {
    return Arrays.asList(mnemonics[size.ordinal()]);
  }

  @Override
  public byte[] convertToSeed(List<String> seedPhrase) throws SeedPhraseException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isValid(List<String> seedPhrase) {
    return true;
  }
}
