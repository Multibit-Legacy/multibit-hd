package org.multibit.hd.core.api.seed_phrase;

import com.google.bitcoin.crypto.MnemonicCode;
import com.google.bitcoin.crypto.MnemonicException;
import org.multibit.hd.core.exceptions.SeedPhraseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

/**
 *  <p>Generator to provide the following to UI code:<br>
 *  <ul>
 *  <li>Generation of BIP39 seed phrases</li>
 *  <li>Validation of BIP39 seed phrases</li>
 *  <li>Conversion of BIP39 seed phrases to and from seed byte value</li>
 *  </ul>
 *  </p>
 *  
 */
public class Bip39SeedPhraseGenerator implements SeedPhraseGenerator {

  private static final Logger log = LoggerFactory.getLogger(Bip39SeedPhraseGenerator.class);

  private SeedPhraseSize size = SeedPhraseSize.TWELVE_WORDS;

  private MnemonicCode mnemonicCode;
  private SecureRandom secureRandom;


  public Bip39SeedPhraseGenerator() throws SeedPhraseException {
    try {
      mnemonicCode = new MnemonicCode();
      secureRandom = new SecureRandom();
    } catch (IOException ioe) {
      throw new SeedPhraseException("Could not initialise Bip39SeedPhraseGenerator", ioe);
    }
  }

  @Override
  public List<String> newSeedPhrase() throws SeedPhraseException {
    return newSeedPhrase(SeedPhraseSize.TWELVE_WORDS);
  }

  @Override
  public List<String> newSeedPhrase(SeedPhraseSize size) throws SeedPhraseException {
    try {
      byte[] entropy = new byte[size.getEntropyBytesSize()];
      secureRandom.nextBytes(entropy);
      return mnemonicCode.toMnemonic(entropy);
    } catch (MnemonicException.MnemonicLengthException mle) {
      throw new SeedPhraseException("Wrong length of entropy bytes", mle);
    }
  }
}
