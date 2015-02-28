package org.multibit.hd.core.utils;

import com.google.common.base.Optional;

import java.text.Collator;
import java.util.Locale;

/**
 *  <p>Utility to provide the following to WalletManager:<br>
 *  <ul>
 *  <li>Collators</li>
 *  </ul>
 *  </p>
 *  
 */
public class Collators {
  /**
   * Create a new Collator for language sensitive collation
   * @param localeOptional The locale to use, or absent if none set
   * @return the Collator to use
   */
  public static Collator newCollator(Optional<Locale> localeOptional) {
    if (localeOptional.isPresent()) {
      return Collator.getInstance(localeOptional.get());
    } else {
      return Collator.getInstance();
    }
  }
}
