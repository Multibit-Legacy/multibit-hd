package org.multibit.hd.ui.javafx.config;

/**
 * <p>Interface to provide the following to controllers:</p>
 * <ul>
 * <li>Access to the application configuration</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public interface ConfigurationAware {

  /**
   * @param configuration The configuration
   */
  void setConfiguration(Configuration configuration);

  /**
   * @return The configuration
   */
  Configuration getConfiguration();

}
