package org.multibit.hd.core.api;

/**
 * <p>Enum to provide the following to application:</p>
 * <ul>
 * <li>Message keys to use for internationalisation of output from the core module</li>
 * </ul>
 *
 * <h3>Naming conventions</h3>
 * <p>Message keys are placed in an enum for type safety.</p>
 * <p>Message keys have their resource key so that IDEs can maintain a "where used" reference lookup.</p>
 * <p>Message keys are never concatenated to form larger sentences. Instead use token replacements
 * within a larger sentence structure.</p>
 * <p>Resource keys are simply the message key enum name prefixed with "core_" in lower case.</p>
 *
 * @since 0.0.1
 *  
 */
public enum CoreMessageKey {

  // Core messages
  START_NETWORK_CONNECTION_ERROR("core_start_network_connection_error"),
  WALLET_DIRECTORY_ERROR("core_wallet_directory_error"),
  NETWORK_CONFIGURATION_ERROR("core_network_configuration_error"),
  CHAIN_DOWNLOAD("core_chain_download"),
  PEER_COUNT("core_peer_count"),

  // End of enum
  ;

  private final String key;

  private CoreMessageKey(String key) {
    this.key = key;
  }

  /**
   * @return The key for use with the resource bundles
   */

  public String getKey() {
    return key;
  }

}
