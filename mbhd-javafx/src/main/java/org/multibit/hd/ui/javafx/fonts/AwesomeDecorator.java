package org.multibit.hd.ui.javafx.fonts;

import javafx.scene.control.*;

/**
 * <p>Decorator to provide the following to UI controllers:</p>
 * <ul>
 * <li>Apply Font Awesome iconography to various controls</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class AwesomeDecorator {

  public final static String FONT_AWESOME_TTF_PATH = "assets/fonts/fontawesome-webfont.ttf";
  public final static String DEFAULT_ICON_SIZE = "16.0";
  public final static String DEFAULT_FONT_SIZE = "1em";

  public static Label createIconLabel(AwesomeIcon icon, String iconSize) {

    Label label = new Label(icon.toString());
    label.getStyleClass().add("awesome");
    label.setStyle("-fx-font-family: FontAwesome; -fx-font-size: " + iconSize + ";");

    return label;
  }

  public static Button createIconButton(AwesomeIcon icon) {
    return createIconButton(icon, "");
  }

  public static Button createIconButton(AwesomeIcon icon, String text) {

    Label label = createIconLabel(icon, DEFAULT_ICON_SIZE);
    Button button = new Button(text);
    button.setGraphic(label);

    return button;
  }

  public static Button createIconButton(AwesomeIcon icon, String text, String iconSize, String fontSize, ContentDisplay contentDisplay) {

    Label label = createIconLabel(icon, iconSize);

    Button button = new Button(text);
    button.setStyle("-fx-font-size: " + fontSize);
    button.setGraphic(label);
    button.setContentDisplay(contentDisplay);

    return button;
  }

  public static ToggleButton createIconToggleButton(AwesomeIcon icon, String text, String iconSize, ContentDisplay contentDisplay) {
    return createIconToggleButton(icon, text, iconSize, DEFAULT_FONT_SIZE, contentDisplay);
  }

  public static ToggleButton createIconToggleButton(AwesomeIcon icon, String text, String iconSize, String fontSize, ContentDisplay contentDisplay) {

    Label label = createIconLabel(icon, iconSize);

    ToggleButton button = new ToggleButton(text);
    button.setStyle("-fx-font-size: " + fontSize);
    button.setGraphic(label);
    button.setContentDisplay(contentDisplay);

    return button;
  }

  public static Label createIconLabel(AwesomeIcon icon) {
    return createIconLabel(icon, DEFAULT_ICON_SIZE);
  }

  public static void applyIcon(Tab tab, AwesomeIcon icon) {
    applyIcon(tab, icon, DEFAULT_ICON_SIZE);
  }

  public static void applyIcon(Tab tab, AwesomeIcon icon, String iconSize) {
    tab.setGraphic(createIconLabel(icon, iconSize));
  }

  public static void applyIcon(Labeled labeled, AwesomeIcon icon) {
    applyIcon(labeled, icon, ContentDisplay.LEFT, DEFAULT_ICON_SIZE);
  }

  public static void applyIcon(Labeled labeled, AwesomeIcon icon, ContentDisplay contentDisplay) {
    applyIcon(labeled, icon, contentDisplay, DEFAULT_ICON_SIZE);
  }

  public static void applyIcon(Labeled labeled, AwesomeIcon icon, ContentDisplay contentDisplay, String iconSize) {
    labeled.setGraphic(createIconLabel(icon, iconSize));
    labeled.setContentDisplay(contentDisplay);
  }

  public static void applyIcon(MenuItem menuItem, AwesomeIcon icon) {
    applyIcon(menuItem, icon, DEFAULT_FONT_SIZE, DEFAULT_ICON_SIZE);
  }

  public static void applyIcon(MenuItem menuItem, AwesomeIcon icon, String iconSize) {
    applyIcon(menuItem, icon, DEFAULT_FONT_SIZE, iconSize);
  }

  public static void applyIcon(MenuItem menuItem, AwesomeIcon icon, String fontSize, String iconSize) {

    Label label = createIconLabel(icon, iconSize);
    menuItem.setStyle("-fx-font-size: " + fontSize);
    menuItem.setGraphic(label);
  }

  public static void applyIcon(TreeItem treeItem, AwesomeIcon icon) {
    applyIcon(treeItem, icon, DEFAULT_ICON_SIZE);
  }

  public static void applyIcon(TreeItem treeItem, AwesomeIcon icon, String iconSize) {
    Label label = createIconLabel(icon, iconSize);
    treeItem.setGraphic(label);
  }

}