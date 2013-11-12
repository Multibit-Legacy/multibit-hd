package org.multibit.hd.ui.javafx.controllers;

import javafx.fxml.Initializable;
import org.multibit.hd.ui.javafx.views.View;
import org.multibit.hd.ui.javafx.views.ViewAware;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>Abstract base controller to provide the following to UI controllers:</p>
 * <ul>
 * <li>Access to standard methods and structure</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public abstract class MultiBitController implements Initializable, ViewAware {

  private View view;

  protected ResourceBundle resourceBundle;

  protected URL url;

  /**
   * Default implementation of initialization code
   *
   * @param url             The URL to the FXML providing the view
   * @param resourceBundle1 The resource bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle1) {

    this.url = url;
    this.resourceBundle = resourceBundle1;

    initAwesome();

    initClickEvents();

  }

  /**
   * <p>Decorate any components with Font Awesome icons</p>
   */
  public void initAwesome() {

    // Do nothing

  }

  /**
   * <p>Add click events to components</p>
   */
  public void initClickEvents() {

    // Do nothing

  }

  @Override
  public View getView() {
    return view;
  }

  @Override
  public void setView(View view) {
    this.view = view;
  }

}
