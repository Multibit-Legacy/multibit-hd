package org.multibit.hd.core.config;

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

  private String currentScreen = "HELP";

  private boolean restoreApplicationLayoutOnStartup = false;

  private String bitcoinUriHandling = "FILL";

  private String applicationDirectory = ".";

  private String currentTheme = "LIGHT";

  private String version = "0.0.0 (alpha)";

  private String lastFrameBounds = "0,0,1000,560";

  private String sidebarWidth = "180";

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
  public String getCurrentScreen() {
    return currentScreen;
  }

  public void setCurrentScreen(String currentScreen) {
    this.currentScreen = currentScreen;
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
   * @return The current version number
   */
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @return The last frame bounds in px (x,y,w,h)
   */
  public String getLastFrameBounds() {
    return lastFrameBounds;
  }

  public void setLastFrameBounds(String lastFrameBounds) {
    this.lastFrameBounds = lastFrameBounds;
  }

  /**
   * @return The sidebar width in px
   */
  public String getSidebarWidth() {
    return sidebarWidth;
  }

  public void setSidebarWidth(String sidebarWidth) {
    this.sidebarWidth = sidebarWidth;
  }

  /**
   * @return A deep copy of this object
   */
  public ApplicationConfiguration deepCopy() {

    ApplicationConfiguration app = new ApplicationConfiguration();

    app.setCurrentScreen(getCurrentScreen());
    app.setApplicationDirectory(getApplicationDirectory());
    app.setBitcoinUriHandling(getBitcoinUriHandling());
    app.setRestoreApplicationLayoutOnStartup(isRestoreApplicationLayoutOnStartup());
    app.setLastFrameBounds(getLastFrameBounds());
    app.setSidebarWidth(getSidebarWidth());
    app.setCurrentTheme(getCurrentTheme());
    app.setVersion(getVersion());

    return app;
  }
}
