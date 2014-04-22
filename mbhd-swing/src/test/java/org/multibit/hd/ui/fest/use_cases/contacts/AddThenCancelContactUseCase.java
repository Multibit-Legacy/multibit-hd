package org.multibit.hd.ui.fest.use_cases.contacts;

import org.fest.swing.fixture.FrameFixture;
import org.multibit.hd.ui.fest.use_cases.AbstractFestUseCase;
import org.multibit.hd.ui.languages.MessageKey;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * <p>Use case to provide the following to FEST testing:</p>
 * <ul>
 * <li>Verify the "contacts" screen add with cancel</li>
 * </ul>
 * <p>Requires the "contacts" screen to be showing</p>
 *
 * @since 0.0.1
 *  
 */
public class AddThenCancelContactUseCase extends AbstractFestUseCase {

  public AddThenCancelContactUseCase(FrameFixture window) {
    super(window);
  }

  @Override
  public void execute(Map<String, Object> parameters) {

    // Get the initial row count
    int rowCount1 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Click on Add
    window
      .button(MessageKey.ADD.getKey())
      .click();

    // Verify the wizard appears
    window
      .label(MessageKey.NEW_CONTACT_TITLE.getKey());

    window
      .button(MessageKey.APPLY.getKey())
      .requireVisible()
      .requireEnabled();

    // Click Cancel
    window
      .button(MessageKey.CANCEL.getKey())
      .requireVisible()
      .requireEnabled()
      .click();

    // Verify the underlying screen is back
    window
      .button(MessageKey.ADD.getKey())
      .requireVisible()
      .requireEnabled();

    // Get an updated row count
    int rowCount2 = window
      .table(MessageKey.CONTACTS.getKey())
      .rowCount();

    // Verify no change has taken place
    assertThat(rowCount1).isEqualTo(rowCount2);

  }

}
