package org.multibit.hd.ui.javafx.views;

/**
 * <p>Enum to provide the following to UI controllers:</p>
 * <ul>
 * <li>References to Views and their FXML</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public enum Screen {

  /**
   * The welcome stage (present during startup and major configuration)
   */
  WELCOME_STAGE("fxml/welcome/welcome.fxml"),

  /**
   * The login screen
   */
  WELCOME_LOGIN("fxml/welcome/login.fxml"),

  WELCOME_PROVIDE_INITIAL_SEED("fxml/welcome/provide-initial-seed.fxml"),

  // Main stage screens

  /**
   * The main stage (ongoing interactions)
   */
  MAIN_STAGE("fxml/main/main.fxml"),

  /**
   * The home screen within the main stage
   */
  MAIN_HOME("fxml/main/home.fxml"),

  /**
   * The contacts screen within the main stage
   */
  MAIN_CONTACTS("fxml/main/contacts.fxml"),

  /**
   * The settings screen within the main stage
   */
  MAIN_SETTINGS("fxml/main/settings.fxml"),

  /**
   * The help screen within the main stage
   */
  MAIN_HELP("fxml/main/help.fxml"),

  /**
   * The wallet screen within the main stage
   */
  MAIN_WALLET("fxml/main/wallet.fxml"),

  /**
   * The error reporting screen within the main stage
   */
  MAIN_ERROR("fxml/main/error.fxml"),

  // End of enum
  ;

  private String fxmlResource;

  /**
   * @param fxmlResource The resource path to the FXML
   */
  private Screen(String fxmlResource) {
    this.fxmlResource = fxmlResource;
  }

  public String getFxmlResource() {
    return fxmlResource;
  }
}
