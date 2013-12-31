package org.multibit.hd.core.api.seed_phrase;

import org.multibit.hd.core.exceptions.SeedPhraseException;

import java.util.List;

/**
 * <p>Interface to provide the following to Core API:</p>
 * <ul>
 * <li>Methods for interacting with a BIP0039 seed phrase generator</li>
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

}
