package org.multibit.hd.core.dto;

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
  UNKNOWN(false),
  /**
   * Unfilled star
   */
  EMPTY(false),
  /**
   * Fill style 1 (e.g YELLOW for "important, pending action etc")
   */
  FILL_1(true),
  /**
   * Fill style 2 (e.g. RED for "very important, critical action etc")
   */
  FILL_2(true),
  /**
   * Fill style 3 (e.g. GREEN for "resolved")
   */
  FILL_3(true);

  private final boolean starred;

  /**
   * @param starred True if a solid star is showing
   */
  StarStyle(boolean starred) {
    this.starred = starred;
  }

  public boolean isStarred() {
    return starred;
  }

  /**
   * @return The next star style in the enum wrapping as required
   */
  public StarStyle next() {

    int ordinal = this.ordinal();

    ordinal = (ordinal + 1) % StarStyle.values().length;

    return StarStyle.class.getEnumConstants()[ordinal];

  }
}
