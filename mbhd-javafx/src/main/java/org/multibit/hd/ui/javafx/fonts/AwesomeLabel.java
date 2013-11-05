package org.multibit.hd.ui.javafx.fonts;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;

import java.util.concurrent.Callable;

/**
 * <p>[Pattern] to provide the following to {@link Object}:</p>
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
public class AwesomeLabel extends Label {

  private final ObjectProperty<AwesomeIcon> icon = new SimpleObjectProperty<>();

  /**
   * Default constructor to configure bindings
   */
  public AwesomeLabel() {
    super();

    // All Font Awesome labels use this class and stylesheet
    this.getStyleClass().add("fa-icon-label");
    this.getStylesheets().add("assets/css/main.css");

    // Bind the icon to a string property
    this.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
      @Override
      public String call() throws Exception {
        final AwesomeIcon icon = AwesomeLabel.this.getIcon();
        return String.valueOf(icon == null ? null : icon.getChar());
      }
    }, this.iconProperty()));
  }


  /**
   * @param unicode The unicode character within Font Awesome
   */
  public AwesomeLabel(final char unicode) {
    this();
    setText(String.valueOf(unicode));
  }

  /**
   * @param icon The friendly reference to the icon (e.g. "GLASS")
   */
  public void setIcon(final AwesomeIcon icon) {
    this.icon.set(icon);
  }

  /**
   * @return The friendly reference to the icon
   */
  public AwesomeIcon getIcon() {
    return this.icon.get();
  }

  public ObjectProperty<AwesomeIcon> iconProperty() {
    return this.icon;
  }

}