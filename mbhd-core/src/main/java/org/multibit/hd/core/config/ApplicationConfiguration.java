package org.multibit.hd.core.config;

import com.google.common.base.Optional;

/**
 * <p>Configuration to provide the following to application:</p>
 * <ul>
 * <li>Configuration of look and feel</li>
 * <li>Configuration of screen state</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ApplicationConfiguration {

  private Optional<String> currentScreen = Optional.absent();

  private boolean restoreApplicationLayoutOnStartup = false;

  private String bitcoinUriHandling = "FILL";

  private String applicationDirectory = ".";

  private String currentWalletRoot = "";

  private String currentTheme = "LIGHT";

  /**
   * Wallets are stored one per directory. The name of this containing directory is called the wallet root.
   *
   * @return The root of the current wallet
   */
  public String getCurrentWalletRoot() {

    return currentWalletRoot;

  }

  public void setCurrentWalletRoot(String currentWalletRoot) {

    this.currentWalletRoot = currentWalletRoot;
  }

  /**
   * @return The application directory path (e.g. ".")
   */
  public String getApplicationDirectory() {
    return applicationDirectory;
  }

  public void setApplicationDirectory(String applicationDirectory) {
    this.applicationDirectory = applicationDirectory;
  }

  /**
   * @return How Bitcoin URIs should be handled
   */
  public String getBitcoinUriHandling() {
    return bitcoinUriHandling;
  }

  public void setBitcoinUriHandling(String bitcoinUriHandling) {
    this.bitcoinUriHandling = bitcoinUriHandling;
  }

  /**
   * @return The current screen to show
   */
  public Optional<String> getCurrentScreen() {
    return currentScreen;
  }

  public void setCurrentScreen(String currentScreen) {
    this.currentScreen = Optional.fromNullable(currentScreen);
  }

  /**
   * @return True if the application layout should be preserved between startups
   */
  public boolean isRestoreApplicationLayoutOnStartup() {
    return restoreApplicationLayoutOnStartup;
  }

  public void setRestoreApplicationLayoutOnStartup(boolean restoreApplicationLayoutOnStartup) {
    this.restoreApplicationLayoutOnStartup = restoreApplicationLayoutOnStartup;
  }

  public String getCurrentTheme() {
    return currentTheme;
  }

  public void setCurrentTheme(String currentTheme) {
    this.currentTheme = currentTheme;
  }

  /**
   * @return A deep copy of this object
   */
  public ApplicationConfiguration deepCopy() {

    ApplicationConfiguration app = new ApplicationConfiguration();

    app.setCurrentScreen(getCurrentScreen().orNull());
    app.setCurrentWalletRoot((getCurrentWalletRoot()));
    app.setApplicationDirectory(getApplicationDirectory());
    app.setBitcoinUriHandling(getBitcoinUriHandling());
    app.setRestoreApplicationLayoutOnStartup(isRestoreApplicationLayoutOnStartup());
    app.setCurrentTheme(getCurrentTheme());

    return app;
  }
}
