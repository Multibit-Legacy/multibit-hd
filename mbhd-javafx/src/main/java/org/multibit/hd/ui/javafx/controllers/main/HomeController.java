package org.multibit.hd.ui.javafx.controllers.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.multibit.hd.ui.javafx.controllers.MultiBitController;
import org.multibit.hd.ui.javafx.fonts.AwesomeDecorator;
import org.multibit.hd.ui.javafx.fonts.AwesomeIcon;

public class HomeController extends MultiBitController  {

  @FXML
  public Button sendButton  ;

  @FXML
  public Button receiveButton;

  @FXML
  public ImageView receivingQRImage;

  @FXML
  public Label receivingAddress;

  @FXML
  public Label exchangeRateLabel;

  @FXML
  public Button copyButton;

  @FXML
  public Button shareButton;

  @Override
  public void initClickEvents() {

  }

  @Override
  public void initAwesome() {

    AwesomeDecorator.applyIcon(copyButton, AwesomeIcon.COPY, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(shareButton, AwesomeIcon.SHARE, ContentDisplay.LEFT);

    AwesomeDecorator.applyIcon(sendButton, AwesomeIcon.ARROW_UP, ContentDisplay.LEFT);
    AwesomeDecorator.applyIcon(receiveButton, AwesomeIcon.ARROW_DOWN, ContentDisplay.LEFT);

  }

}
