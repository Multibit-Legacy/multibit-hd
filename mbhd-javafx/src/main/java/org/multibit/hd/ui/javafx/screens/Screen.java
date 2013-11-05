package org.multibit.hd.ui.javafx.screens;

/**
 * <p>Utility to provide the following to {@link Object}:</p>
 * <ul>
 * <li></li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *         
 */
public enum Screen {

  /**
   * The login screen
   */
  WELCOME_LOGIN("fxml/welcome/login.fxml"),

  WELCOME_PROVIDE_INITIAL_SEED("fxml/welcome/provide-initial-seed.fxml"),
  MAIN_HOME("fxml/main/main.fxml"),

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
