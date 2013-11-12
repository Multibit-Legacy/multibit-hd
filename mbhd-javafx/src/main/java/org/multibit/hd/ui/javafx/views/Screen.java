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
   * The main stage (ongoing interactions)
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
   * The help screen within the main stage
   */
  MAIN_HELP("fxml/main/help.fxml"),

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
