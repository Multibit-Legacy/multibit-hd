package org.multibit.hd.ui.javafx.config;

import com.google.common.base.Optional;
import org.multibit.hd.ui.javafx.views.Screen;
import org.multibit.hd.ui.javafx.views.StageManager;

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

  private Optional<Screen> currentScreen = Optional.absent();

  private Optional<String> currentTab = Optional.absent();

  private Optional<StageManager> currentStageManager = Optional.absent();

  private boolean restoreApplicationLayoutOnStartup = false;

  private BitcoinUriHandling bitcoinUriHandling = BitcoinUriHandling.FILL;

  private String applicationDirectory = ".";

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
  public BitcoinUriHandling getBitcoinUriHandling() {
    return bitcoinUriHandling;
  }

  public void setBitcoinUriHandling(BitcoinUriHandling bitcoinUriHandling) {
    this.bitcoinUriHandling = bitcoinUriHandling;
  }

  /**
   * @return The current stage manager
   */
  public Optional<StageManager> getCurrentStageManager() {
    return currentStageManager;
  }

  public void setCurrentStageManager(StageManager currentStageManager) {
    this.currentStageManager = Optional.fromNullable(currentStageManager);
  }

  /**
   * @return The current screen to show
   */
  public Optional<Screen> getCurrentScreen() {
    return currentScreen;
  }

  public void setCurrentScreen(Screen currentScreen) {
    this.currentScreen = Optional.fromNullable(currentScreen);
  }

  public Optional<String> getCurrentTab() {
    return currentTab;
  }

  public void setCurrentTab(String currentTab) {
    this.currentTab = Optional.fromNullable(currentTab);
  }

  public boolean isRestoreApplicationLayoutOnStartup() {
    return restoreApplicationLayoutOnStartup;
  }

  public void setRestoreApplicationLayoutOnStartup(boolean restoreApplicationLayoutOnStartup) {
    this.restoreApplicationLayoutOnStartup = restoreApplicationLayoutOnStartup;
  }
}
