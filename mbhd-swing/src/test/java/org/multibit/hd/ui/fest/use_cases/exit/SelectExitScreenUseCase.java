package org.multibit.hd.ui.fest.use_cases.exit;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "exit" actually closes the application</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class SelectExitScreenUseCase extends AbstractFestUseCase {

  public SelectExitScreenUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    window
      .tree(MessageKey.SIDEBAR_TREE.getKey())
      .requireVisible()
      .requireEnabled()
      .selectRow(8);

    // Expect the Exit wizard to show
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled();

    window
      .button(MessageKey.EXIT.getKey())
      .requireVisible()
      .requireEnabled()
      .click();


  }

}
