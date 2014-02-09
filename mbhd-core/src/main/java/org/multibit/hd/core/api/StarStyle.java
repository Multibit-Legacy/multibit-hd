package org.multibit.hd.core.api;

/**
 * <p>Enum to provide the following to Contact API:</p>
 * <ul>
 * <li>Define a presentation style for the contact star mechanism</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum StarStyle {

  /**
   * The fallback position for any future styles encountered by this version
   */
  UNKNOWN,
  /**
   * Unfilled star
   */
  EMPTY,
  /**
   * Fill style 1 (e.g YELLOW for "important, pending action etc")
   */
  FILL_1,
  /**
   * Fill style 2 (e.g. RED for "very important, critical action etc")
   */
  FILL_2,
  /**
   * Fill style 3 (e.g. GREEN for "resolved")
   */
  FILL_3

}
