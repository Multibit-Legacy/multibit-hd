package org.multibit.hd.core.utils;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.text.Collator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

public class CollatorsTest {

  // Two example words that have different collation order in English and French
  // Taken from: http://docs.oracle.com/javase/tutorial/i18n/text/locale.html
  private static final String EXAMPLE_WORD_1 = "p\u00e9ch\u00E9";  // péché
  private static final String EXAMPLE_WORD_2 = "p\u00EAche";  // pêche

  private List<String> toSortAlpha;
  private List<String> toSortBeta;

  @Before
  public void setUp() throws Exception {
    // Create two lists that need sorting
    toSortAlpha = Lists.newArrayList();
    toSortAlpha.add(EXAMPLE_WORD_1);
    toSortAlpha.add(EXAMPLE_WORD_2);

    toSortBeta = Lists.newArrayList();
    toSortBeta.add(EXAMPLE_WORD_2);
    toSortBeta.add(EXAMPLE_WORD_1);
  }

  @Test
  public void testNoLocale() {
    // No locale collator (English)
    Collator collatorNoLocale = Collators.newCollator(Optional.<Locale>absent());

    Collections.sort(toSortAlpha, collatorNoLocale);
    checkEnglishOrder(toSortAlpha);

    Collections.sort(toSortBeta, collatorNoLocale);
    checkEnglishOrder(toSortBeta);
  }

  @Test
  public void testEnglishLocale() {
    // English locale
    Collator collatorEnglishLocale = Collators.newCollator(Optional.of(Locale.ENGLISH));

    Collections.sort(toSortAlpha, collatorEnglishLocale);
    checkEnglishOrder(toSortAlpha);

    Collections.sort(toSortBeta, collatorEnglishLocale);
    checkEnglishOrder(toSortBeta);
  }

  @Test
  public void testFrenchLocale() {
    // French locale
    Collator collatorFrenchLocale = Collators.newCollator(Optional.of(Locale.FRENCH));

    Collections.sort(toSortAlpha, collatorFrenchLocale);
    checkFrenchOrder(toSortAlpha);

    Collections.sort(toSortBeta, collatorFrenchLocale);
    checkFrenchOrder(toSortBeta);
  }

  /**
   * Check the order is using the English ordering
   *
   * @param list the list to order
   */
  private void checkEnglishOrder(List<String> list) {
    assertThat(EXAMPLE_WORD_1.equals(toSortAlpha.get(0))).isTrue();
    assertThat(EXAMPLE_WORD_2.equals(toSortAlpha.get(1))).isTrue();
  }

  /**
   * Check the order is using the French ordering
   *
   * @param list the list to order
   */
  private void checkFrenchOrder(List<String> list) {
    assertThat(EXAMPLE_WORD_2.equals(toSortAlpha.get(0))).isTrue();
    assertThat(EXAMPLE_WORD_1.equals(toSortAlpha.get(1))).isTrue();
  }
}
