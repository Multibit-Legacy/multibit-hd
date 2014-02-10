package org.multibit.hd.core.seed_phrase;

import com.google.bitcoin.core.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.multibit.hd.core.exceptions.SeedPhraseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;


public class Bip39SeedPhraseGeneratorTest {

  private static final Logger log = LoggerFactory.getLogger(Bip39SeedPhraseGeneratorTest.class);

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testCreateDefaultLength() throws Exception {
    SeedPhraseGenerator generator = new Bip39SeedPhraseGenerator();
    assertThat(generator).isNotNull();

    // Create a 12 word seed phrase
    List<String> phrase = generator.newSeedPhrase();
    assertThat(phrase).isNotNull();

    // Check there are 12 words
    assertThat(phrase.size()).isEqualTo(12);
  }

  @Test
  public void testCreateLengthTwelve() throws Exception {
    SeedPhraseGenerator generator = new Bip39SeedPhraseGenerator();
    assertThat(generator).isNotNull();

    // Create a 12 word seed phrase
    List<String> phrase = generator.newSeedPhrase(SeedPhraseSize.TWELVE_WORDS);
    assertThat(phrase).isNotNull();

    // Check there are 12 words
    assertThat(phrase.size()).isEqualTo(12);
  }

  @Test
  public void testCreateLengthEighteen() throws Exception {
    SeedPhraseGenerator generator = new Bip39SeedPhraseGenerator();
    assertThat(generator).isNotNull();

    // Create a 18 word seed phrase
    List<String> phrase = generator.newSeedPhrase(SeedPhraseSize.EIGHTEEN_WORDS);
    assertThat(phrase).isNotNull();

    // Check there are 18 words
    assertThat(phrase.size()).isEqualTo(18);
  }

  @Test
  public void testCreateLengthTwentyFour() throws Exception {
    SeedPhraseGenerator generator = new Bip39SeedPhraseGenerator();
    assertThat(generator).isNotNull();

    // Create a 18 word seed phrase
    List<String> phrase = generator.newSeedPhrase(SeedPhraseSize.TWENTY_FOUR_WORDS);
    assertThat(phrase).isNotNull();

    // Check there are 24 words
    assertThat(phrase.size()).isEqualTo(24);
  }

  @Test
  public void testCreateSeedBad() throws Exception {
    SeedPhraseGenerator generator = new Bip39SeedPhraseGenerator();

    // Create a seed from a bad phrase
    List<String> badPhrase = new ArrayList<>();
    badPhrase.add("blah");
    badPhrase.add("blah di");
    badPhrase.add("blah");
    try {
      generator.convertToSeed(badPhrase);
      throw new IllegalStateException("Bad phrase not rejected");
    } catch (SeedPhraseException spe) {
      // Expected path
    }
  }

  @Test
  public void testCreateSeedGood() throws Exception {
    SeedPhraseGenerator generator = new Bip39SeedPhraseGenerator();

    // Create a seed from a good phrase
    List<String> goodPhrase = generator.newSeedPhrase();

      byte[] seed = generator.convertToSeed(goodPhrase);
    log.debug("Seed generated was '" + Utils.bytesToHexString(seed) +"'");
  }
}
