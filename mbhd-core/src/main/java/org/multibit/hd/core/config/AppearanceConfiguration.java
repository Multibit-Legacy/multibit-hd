package org.multibit.hd.core.config;

import org.multibit.hd.core.blockexplorer.BlockChainInfoBlockExplorer;

/**
 * <p>Configuration to provide the following to application:</p>
 * <ul>
 * <li>Configuration of look and feel</li>
 * <li>Configuration of screen state</li>
 * </ul>
 * <p>This configuration is for general appearance parameters</p>
 *
 * @since 0.0.1
 *
 */
public class AppearanceConfiguration {

  private String currentScreen = "SEND_REQUEST";

  private boolean restoreApplicationLayoutOnStartup = false;

  private String bitcoinUriHandling = "FILL";

  private String applicationDirectory = ".";

  /**
   * Default to true since most people will be using this in private
   */
  private boolean showBalance = true;

  private String currentTheme = "BOOTSTRAP";

  /**
   * The version field cannot contain "/" or "(" or ")" since it is used in the PeerGroup of Bitcoinj
   *
   * If you change this DON'T FORGET to update the core/test/resources/fixtures/messages/test-simple.txt
   * and rerun BitcoinMessagesTest
   */
  public static final String CURRENT_VERSION = "0.1.5";
  private String version = CURRENT_VERSION;

  /**
   * Good default width and height for centered initial screen and balance displayed
   * Arranged as either W,H (centered) or X,Y,W,H (absolute)
   */
  private String lastFrameBounds = "900,550";

  private String sidebarWidth = "180";

  private String cloudBackupLocation = "";

  /**
   * The id of the blockexplorer used to drill down into transactions
   */
  private String blockExplorerId = BlockChainInfoBlockExplorer.ID;

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


  /**
   * @return True if the balance header should be visible
   */
  public boolean isShowBalance() {
    return showBalance;
  }

  public void setShowBalance(boolean showBalance) {
    this.showBalance = showBalance;
  }

  public String getCurrentTheme() {
    return currentTheme;
  }

  public void setCurrentTheme(String currentTheme) {
    this.currentTheme = currentTheme;
  }

  /**
   * @return The version number stored in the YAML
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
   * @return The cloud backup location
   */
  public String getCloudBackupLocation() {
    return cloudBackupLocation;
  }

  public void setCloudBackupLocation(String cloudBackupLocation) {
    this.cloudBackupLocation = cloudBackupLocation;
  }


  /**
   * @return The id of the current block explorer
   */
  public String getBlockExplorerId() {
    return blockExplorerId;
  }

  public void setBlockExplorerId(String blockExplorerId) {
    this.blockExplorerId = blockExplorerId;
  }

  /**
   * @return A deep copy of this object
   */
  public AppearanceConfiguration deepCopy() {

    AppearanceConfiguration app = new AppearanceConfiguration();

    app.setCurrentScreen(getCurrentScreen());
    app.setBitcoinUriHandling(getBitcoinUriHandling());
    app.setRestoreApplicationLayoutOnStartup(isRestoreApplicationLayoutOnStartup());
    app.setLastFrameBounds(getLastFrameBounds());
    app.setSidebarWidth(getSidebarWidth());
    app.setCurrentTheme(getCurrentTheme());
    app.setShowBalance(isShowBalance());
    app.setApplicationDirectory(getApplicationDirectory());
    app.setVersion(getVersion());
    app.setCloudBackupLocation(getCloudBackupLocation());
    app.setBlockExplorerId(getBlockExplorerId());

    return app;
  }
}
