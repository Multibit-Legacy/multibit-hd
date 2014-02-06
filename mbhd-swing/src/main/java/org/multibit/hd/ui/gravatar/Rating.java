package org.multibit.hd.ui.gravatar;

/**
 * <p>Enum to provide the following to Gravatar API:</p>
 * <ul>
 * <li>Codes for image ratings (G, PG, R, X)</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum Rating {

  GENERAL("g"),

  PARENTAL_GUIDANCE("pg"),

  RESTRICTED("r"),

  EXPLICIT("x");

  private final String code;

  /**
   * @param code The rating code
   */
  private Rating(String code) {
    this.code = code;
  }

  /**
   * @return The rating code
   */
  public String getCode() {
    return code;
  }

}
