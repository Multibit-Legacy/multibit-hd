package org.multibit.hd.ui.fest.use_cases.welcome_select;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the welcome wizard "select language" panel view</li>
 * <li>Selected language will be en_US</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class AcceptLicenceUseCase extends AbstractFestUseCase {

  public AcceptLicenceUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    assertLabelText(MessageKey.WELCOME_TITLE);

    // Verify that disagree licence is selected by default
    window
      .radioButton(MessageKey.REJECT_LICENCE.getKey())
      .requireSelected();

    // Verify the exit button is enabled
    window
      .button(MessageKey.EXIT.getKey())
      .requireVisible()
      .requireEnabled();

    // Verify the next button is disabled
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    // Verify the reject radio is selected
    window
      .radioButton(MessageKey.REJECT_LICENCE.getKey())
      .requireVisible()
      .requireSelected();

    // Verify the accept radio is not selected
    window
      .radioButton(MessageKey.ACCEPT_LICENCE.getKey())
      .requireVisible()
      .requireNotSelected();

    // Click the agree radio
    window
      .radioButton(MessageKey.ACCEPT_LICENCE.getKey())
      .click();

    // Verify the next button is enabled
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireEnabled();

    // Click the reject radio again
    window
      .radioButton(MessageKey.REJECT_LICENCE.getKey())
      .click();

    // Verify the next button is disabled
    window
      .button(MessageKey.NEXT.getKey())
      .requireVisible()
      .requireDisabled();

    // Click the agree radio again
    window
      .radioButton(MessageKey.ACCEPT_LICENCE.getKey())
      .click();

    // Click the next button to proceed
    window
      .button(MessageKey.NEXT.getKey())
      .click();

  }

}
