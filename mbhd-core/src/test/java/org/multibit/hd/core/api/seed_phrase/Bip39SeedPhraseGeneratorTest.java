package org.multibit.hd.core.api.seed_phrase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;


public class Bip39SeedPhraseGeneratorTest {

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
}
