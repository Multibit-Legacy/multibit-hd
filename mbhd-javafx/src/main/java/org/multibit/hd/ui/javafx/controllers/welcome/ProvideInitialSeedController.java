package org.multibit.hd.ui.javafx.controllers.welcome;

import javafx.event.ActionEvent;
import org.multibit.hd.ui.javafx.controllers.MultiBitController;
import org.multibit.hd.ui.javafx.views.Screen;
import org.multibit.hd.ui.javafx.views.StageManager;

/**
 * <p>Controller to provide the following to UI:</p>
 * <ul>
 * <li>Handles events from the login view</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class ProvideInitialSeedController extends MultiBitController {

  public void onRecoverFired(ActionEvent actionEvent) {
    StageManager.handOver(StageManager.MAIN_STAGE, Screen.MAIN_HOME);
  }

}
