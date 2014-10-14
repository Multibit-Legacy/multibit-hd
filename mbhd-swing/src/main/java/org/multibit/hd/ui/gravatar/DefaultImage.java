package org.multibit.hd.ui.gravatar;

/**
 * <p>Enum to provide the following to Gravatar API:</p>
 * <ul>
 * <li>Codes for default images</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public enum DefaultImage {

  /**
   * The Gravatar icon
   */
  GRAVATAR_ICON(""),

  /**
   * Random jumble of geometric shapes
   */
  IDENTICON("identicon"),

  /**
   * A random monster with child-like rendering
   */
  MONSTER("monsterid"),

  /**
   * A caricature with shaded rendering
   */
  WAVATAR("wavatar"),

  /**
   * A silhouette of a figure
   */
  MYSTERY_MAN("mm"),

  /**
   * A 404 response code
   */
  HTTP_404("404")

  // End of enum
  ;

  private final String code;

  private DefaultImage(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}
