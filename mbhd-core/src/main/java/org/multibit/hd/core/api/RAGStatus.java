package org.multibit.hd.core.api;

/**
 * <p>Enum to provide the following to Core API:</p>
 * <ul>
 * <li>High level description of the effect this will have on a consumer (possibly a user)</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum RAGStatus {

  /**
   * Red means stop, critical, user must take action to fix
   */
  RED,
  /**
   * Amber means warning, interested users may want to explore further
   */
  AMBER,
  /**
   * Green means go, no action required all is well
   */
  GREEN

  // End of enum
  ;

}
