package org.multibit.hd.ui.events.controller;

import java.util.Locale;

/**
 * <p>Event to provide the following to the Controller Event API:</p>
 * <ul>
 * <li>Essential information for a locale change</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ChangeLocaleEvent implements ControllerEvent {

  private final Locale locale;

  /**
   * @param locale The new locale
   */
  public ChangeLocaleEvent(Locale locale) {

    this.locale = locale;

  }

  /**
   * @return The new locale
   */
  public Locale getLocale() {
    return locale;
  }
}
