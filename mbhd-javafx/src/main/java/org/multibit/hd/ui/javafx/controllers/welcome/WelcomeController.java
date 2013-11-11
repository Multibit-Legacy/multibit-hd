package org.multibit.hd.ui.javafx.controllers.welcome;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.multibit.hd.ui.javafx.controllers.MultiBitController;

public class WelcomeController extends MultiBitController  {

  @FXML
  public AnchorPane detailAnchorPane;

  public AnchorPane getDetailAnchorPane() {
    return detailAnchorPane;
  }
}
