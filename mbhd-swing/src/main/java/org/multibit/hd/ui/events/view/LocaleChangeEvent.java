package org.multibit.hd.ui.events.view;

import java.util.Locale;

/**
 * <p>Event to provide the following to views:</p>
 * <ul>
 * <li>Essential information for a locale change</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class LocaleChangeEvent {

  private final Locale locale;

  /**
   * @param locale The new locale
   */
  public LocaleChangeEvent(Locale locale) {

    this.locale = locale;

  }

  /**
   * @return The new locale
   */
  public Locale getLocale() {
    return locale;
  }
}
