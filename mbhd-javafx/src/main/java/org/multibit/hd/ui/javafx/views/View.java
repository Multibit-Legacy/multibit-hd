package org.multibit.hd.ui.javafx.views;

import com.google.common.base.Optional;
import javafx.scene.Parent;

/**
 * <p>Value object to provide the following to UI:</p>
 * <ul>
 * <li>Simplified description of a node graph for scenes</li>
 * <li>Bi-directional link between a controller and its view</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class View {

  private final Optional<View> parentViewOptional;
  private final Parent screenParentNode;
  private final ViewAware controller;
  private final Screen screen;

  public View(
    Optional<View> parentViewOptional,
    Screen screen,
    Parent screenParentNode,
    ViewAware controller
  ) {

    this.parentViewOptional = parentViewOptional;
    this.screen = screen;
    this.screenParentNode = screenParentNode;
    this.controller = controller;
  }

  public Screen getScreen() {
    return screen;
  }

  public Optional<View> getParentViewOptional() {
    return parentViewOptional;
  }

  public Parent getScreenParentNode() {
    return screenParentNode;
  }

  public ViewAware getController() {
    return controller;
  }
}
