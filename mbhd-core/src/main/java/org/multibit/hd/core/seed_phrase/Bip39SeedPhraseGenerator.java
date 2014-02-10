package org.multibit.hd.core.seed_phrase;

import com.google.bitcoin.crypto.MnemonicCode;
import com.google.bitcoin.crypto.MnemonicException;
import org.multibit.hd.core.exceptions.SeedPhraseException;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
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

  public static List<String> split(String words) {
      return new ArrayList<>(Arrays.asList(words.split("\\s+")));
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

  @Override
  public byte[] convertToSeed(List<String> seedPhrase) throws SeedPhraseException {
    try {
      mnemonicCode.check(seedPhrase);

      // Convert to seed byte array using an empty password
      return MnemonicCode.toSeed(seedPhrase, "");
    } catch (MnemonicException e) {
      throw new SeedPhraseException("The seed phrase is not valid", e);
    }
  }
}
