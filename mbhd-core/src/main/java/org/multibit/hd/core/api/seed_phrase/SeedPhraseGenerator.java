package org.multibit.hd.core.api.seed_phrase;

import org.multibit.hd.core.exceptions.SeedPhraseException;

import java.util.List;

/**
 * <p>Interface to provide the following to Core API:</p>
 * <ul>
 * <li>Methods for creating a BIP0039 seed phrase</li>
 * <li>Method for converting a seed phrase to a seed</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public interface SeedPhraseGenerator {

  /**
   * @return A new seed phrase using the default size
   */
  List<String> newSeedPhrase() throws SeedPhraseException;

  /**
   * @param size The seed phrase size to use
   *
   * @return A new seed phrase based on the specified size
   */
  List<String> newSeedPhrase(SeedPhraseSize size) throws SeedPhraseException;

  /**
   * @param seedPhrase The seed phrase to convert to a wallet seed
   * @return A seed byte array that can be used to create a deterministic wallet
   */
  byte[] convertToSeed(List<String> seedPhrase) throws SeedPhraseException;

}
